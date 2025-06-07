import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.table.DefaultTableModel;

/**
 * A GUI application for tracking student budgets, allowing users to add transactions,
 * view transaction history, analyze monthly summaries, and view category breakdowns.
 */
public class BudgetTrackerGUI {
    private JFrame mainFrame;
    private JTabbedPane tabbedPane;
    private TransactionManager transactionManager;
    private DefaultTableModel transactionsTableModel;

    /**
     * Constructs a new BudgetTrackerGUI and initializes the GUI components.
     */
    public BudgetTrackerGUI() {
        transactionManager = new TransactionManager();
        prepareGUI();
    }

    /**
     * The main entry point for the application.
     * Sets the system look and feel and creates an instance of BudgetTrackerGUI.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new BudgetTrackerGUI();
        });
    }

    /**
     * Initializes and prepares the main GUI components including the main frame,
     * tabbed interface, and menu bar.
     */
    private void prepareGUI() {
        mainFrame = new JFrame("Student Budget Tracker");
        mainFrame.setSize(900, 650);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        // Create tabbed interface
        tabbedPane = new JTabbedPane();

        // Initialize table model
        String[] columnNames = {"Date", "Description", "Amount", "Category", "Type"};
        transactionsTableModel = new DefaultTableModel(columnNames, 0);

        // Add tabs
        tabbedPane.addTab("Add Transaction", createAddTransactionPanel());
        tabbedPane.addTab("View Transactions", createViewTransactionsPanel());
        tabbedPane.addTab("Monthly Summary", createMonthlySummaryPanel());
        tabbedPane.addTab("Category Breakdown", createCategoryBreakdownPanel());

        mainFrame.add(tabbedPane, BorderLayout.CENTER);

        // Add menu bar
        mainFrame.setJMenuBar(createMenuBar());

        mainFrame.setLocationRelativeTo(null); // Center on screen
        mainFrame.setVisible(true);
    }

    /**
     * Creates and returns the application's menu bar with File and Help menus.
     *
     * @return the configured JMenuBar for the application
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(mainFrame,
                "Student Budget Tracker\nVersion 1.0\nAuthor: Abderrahim Benlalam", "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    /**
     * Creates and returns the panel for adding new transactions.
     * Contains form fields for transaction details and an add button.
     *
     * @return the configured JPanel for adding transactions
     */
    private JPanel createAddTransactionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Transaction Type
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Transaction Type:"), gbc);

        ButtonGroup typeGroup = new ButtonGroup();
        JRadioButton incomeRadio = new JRadioButton("Income", true);
        JRadioButton expenseRadio = new JRadioButton("Expense");
        typeGroup.add(incomeRadio);
        typeGroup.add(expenseRadio);

        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.add(incomeRadio);
        typePanel.add(expenseRadio);
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(typePanel, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        JTextField dateField = new JTextField(15);
        dateField.setText(java.time.LocalDate.now().toString());
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(dateField, gbc);

        // Amount
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Amount:"), gbc);
        JTextField amountField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(amountField, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Category:"), gbc);
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{
                "Food", "Transport", "Entertainment", "Rent", "Utilities", "Books", "Other"
        });
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(categoryCombo, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Description:"), gbc);
        JTextField descField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 4;
        panel.add(descField, gbc);

        // Add Button
        JButton addButton = new JButton("Add Transaction");
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            try {
                boolean isIncome = incomeRadio.isSelected();
                String dateStr = dateField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String category = (String) categoryCombo.getSelectedItem();
                String description = descField.getText();

                if (description.isEmpty()) {
                    throw new IllegalArgumentException("Description cannot be empty");
                }

                transactionManager.addTransaction(new Transaction(dateStr, description, amount, category, isIncome));

                // Clear fields
                amountField.setText("");
                descField.setText("");
                dateField.setText(java.time.LocalDate.now().toString());

                JOptionPane.showMessageDialog(mainFrame, "Transaction added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                // Refresh other tabs
                refreshTransactionsTable();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Error: " + ex.getMessage(),
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    /**
     * Creates and returns the panel for viewing and filtering transactions.
     * Includes a table display, filter controls, and export functionality.
     *
     * @return the configured JPanel for viewing transactions
     */
    private JPanel createViewTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table setup
        transactionsTableModel = new DefaultTableModel(
                new String[]{"Date", "Description", "Amount", "Category", "Type"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        JTable transactionsTable = new JTable(transactionsTableModel);
        transactionsTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(transactionsTable);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));

        JComboBox<String> filterTypeCombo = new JComboBox<>(new String[]{"All", "Income", "Expense"});
        filterPanel.add(filterTypeCombo);

        JButton filterButton = new JButton("Apply");
        filterPanel.add(filterButton);

        JButton clearFilterButton = new JButton("Clear");
        filterPanel.add(clearFilterButton);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        JButton exportButton = new JButton("Export to CSV");
        buttonPanel.add(exportButton);
        buttonPanel.add(refreshButton);

        // Add components
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Initial load
        refreshTransactionsTable();

        // Button actions
        refreshButton.addActionListener(e -> refreshTransactionsTable());

        filterButton.addActionListener(e -> {
            String filterType = (String) filterTypeCombo.getSelectedItem();
            refreshTransactionsTable(filterType);
        });

        clearFilterButton.addActionListener(e -> {
            filterTypeCombo.setSelectedIndex(0);
            refreshTransactionsTable();
        });

        exportButton.addActionListener(e -> exportToCSV());

        return panel;
    }

    /**
     * Refreshes the transactions table with all transactions from the TransactionManager.
     * This is a convenience method that calls refreshTransactionsTable("All").
     */
    private void refreshTransactionsTable() {
        refreshTransactionsTable("All");
    }

    /**
     * Refreshes the transactions table with transactions filtered by the specified type.
     *
     * @param filterType the type of transactions to display ("All", "Income", or "Expense")
     */
    private void refreshTransactionsTable(String filterType) {
        transactionsTableModel.setRowCount(0);

        for (Transaction t : transactionManager.getTransactions()) {
            if (filterType.equals("All") ||
                    (filterType.equals("Income") && t.isIncome()) ||
                    (filterType.equals("Expense") && !t.isIncome())) {

                transactionsTableModel.addRow(new Object[]{
                        t.getDate(),
                        t.getDescription(),
                        String.format("%.2f", t.getAmount()),
                        t.getCategory(),
                        t.isIncome() ? "Income" : "Expense"
                });
            }
        }
    }

    /**
     * Exports the current transactions to a CSV file selected by the user.
     * Displays success or error messages in dialog boxes.
     */
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Transactions");
        fileChooser.setSelectedFile(new File("transactions.csv"));

        if (fileChooser.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                // Write header
                writer.println("Date,Description,Amount,Category,Type");

                // Write data
                for (Transaction t : transactionManager.getTransactions()) {
                    writer.printf("\"%s\",\"%s\",%.2f,\"%s\",\"%s\"%n",
                            t.getDate(),
                            t.getDescription(),
                            t.getAmount(),
                            t.getCategory(),
                            t.isIncome() ? "Income" : "Expense");
                }

                JOptionPane.showMessageDialog(mainFrame, "Data exported successfully to " + file.getName(),
                        "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Error exporting data: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Creates and returns the panel for displaying monthly summary information.
     * Includes income/expense totals, net savings, and a pie chart visualization.
     *
     * @return the configured JPanel for monthly summary
     */
    private JPanel createMonthlySummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Month selection
        JPanel monthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        monthPanel.add(new JLabel("Select Month:"));

        JComboBox<String> monthCombo = new JComboBox<>(new String[]{
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        });
        monthCombo.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        monthPanel.add(monthCombo);

        JComboBox<Integer> yearCombo = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear - 5; year <= currentYear + 5; year++) {
            yearCombo.addItem(year);
        }
        yearCombo.setSelectedItem(currentYear);
        monthPanel.add(yearCombo);

        JButton calculateButton = new JButton("Calculate");
        monthPanel.add(calculateButton);

        // Results display
        JTextArea resultsArea = new JTextArea(10, 30);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane resultsScroll = new JScrollPane(resultsArea);

        // Chart panel
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int month = monthCombo.getSelectedIndex() + 1;
                int year = (Integer) yearCombo.getSelectedItem();
                double[] summary = transactionManager.getMonthlySummary(month, year);

                if (summary[0] == 0 && summary[1] == 0) {
                    g.drawString("No data available for selected month", 50, 50);
                    return;
                }

                int width = getWidth();
                int height = getHeight();
                int diameter = Math.min(width, height) - 40;
                int x = (width - diameter) / 2;
                int y = (height - diameter) / 2;

                double total = summary[0] + summary[1];
                double incomeAngle = 360 * (summary[0] / total);

                // Draw pie slices
                g.setColor(new Color(50, 205, 50)); // Income - green
                g.fillArc(x, y, diameter, diameter, 0, (int) incomeAngle);

                g.setColor(new Color(220, 20, 60)); // Expenses - red
                g.fillArc(x, y, diameter, diameter, (int) incomeAngle, 360 - (int) incomeAngle);

                // Draw legend
                int legendY = 20;
                g.setColor(new Color(50, 205, 50));
                g.fillRect(20, legendY, 15, 15);
                g.setColor(Color.BLACK);
                g.drawString(String.format("Income (%.1f%%)", (summary[0] / total) * 100), 40, legendY + 12);

                legendY += 20;
                g.setColor(new Color(220, 20, 60));
                g.fillRect(20, legendY, 15, 15);
                g.setColor(Color.BLACK);
                g.drawString(String.format("Expenses (%.1f%%)", (summary[1] / total) * 100), 40, legendY + 12);
            }
        };
        chartPanel.setPreferredSize(new Dimension(300, 300));

        // Add components to a container
        JPanel container = new JPanel(new BorderLayout());
        container.add(monthPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.add(resultsScroll);
        centerPanel.add(chartPanel);
        container.add(centerPanel, BorderLayout.CENTER);

        panel.add(container, BorderLayout.CENTER);

        calculateButton.addActionListener(e -> {
            int month = monthCombo.getSelectedIndex() + 1;
            int year = (Integer) yearCombo.getSelectedItem();

            double[] summary = transactionManager.getMonthlySummary(month, year);
            resultsArea.setText(String.format(
                    "Monthly Summary for %s %d\n\n" +
                            "Total Income: $%,.2f\n" +
                            "Total Expenses: $%,.2f\n" +
                            "Net Savings: $%,.2f\n\n" +
                            "Savings Rate: %.1f%%",
                    monthCombo.getSelectedItem(), year,
                    summary[0], summary[1], (summary[0] - summary[1]),
                    (summary[0] > 0 ? ((summary[0] - summary[1]) / summary[0]) * 100 : 0)
            ));
            chartPanel.repaint();
        });

        // Trigger initial calculation
        calculateButton.doClick();

        return panel;
    }

    /**
     * Creates and returns the panel for displaying category breakdown information.
     * Includes month/year selection controls and a pie chart visualization.
     *
     * @return the configured JPanel for category breakdown
     */
    private JPanel createCategoryBreakdownPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Month selection
        JPanel monthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        monthPanel.add(new JLabel("Select Month:"));

        JComboBox<String> monthCombo = new JComboBox<>(new String[]{
                "All Months", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        });
        monthPanel.add(monthCombo);

        // Year selection - using String for display but storing actual years
        JComboBox<String> yearCombo = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearCombo.addItem("All Years");  // First item is "All Years"
        for (int year = currentYear - 5; year <= currentYear + 5; year++) {
            yearCombo.addItem(Integer.toString(year));
        }
        yearCombo.setSelectedIndex(0);
        monthPanel.add(yearCombo);

        JButton calculateButton = new JButton("Calculate");
        monthPanel.add(calculateButton);

        // Pie chart panel
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int month = monthCombo.getSelectedIndex(); // 0 = all months
                String selectedYear = (String) yearCombo.getSelectedItem();

                Map<String, Double> breakdown = getFilteredCategoryBreakdown(month, selectedYear);

                if (breakdown.isEmpty()) {
                    g.drawString("No expense data available", 50, 50);
                    return;
                }

                // ... rest of your pie chart drawing code ...
            }
        };
        chartPanel.setPreferredSize(new Dimension(400, 400));

        panel.add(monthPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        calculateButton.addActionListener(e -> chartPanel.repaint());
        return panel;
    }

    /**
     * Retrieves a filtered breakdown of expenses by category for the specified month and year.
     *
     * @param month the month to filter by (0 for all months)
     * @param yearFilter the year to filter by as a String ("All Years" or specific year)
     * @return a Map of category names to total amounts for the filtered transactions
     */
    private Map<String, Double> getFilteredCategoryBreakdown(int month, String yearFilter) {
        Map<String, Double> breakdown = new LinkedHashMap<>();
        Calendar cal = Calendar.getInstance();

        for (Transaction t : transactionManager.getTransactions()) {
            if (!t.isIncome()) {
                cal.setTime(t.getDate());

                // Month filter (0 means all months)
                boolean monthMatch = (month == 0) || (cal.get(Calendar.MONTH) + 1 == month);

                // Year filter
                boolean yearMatch;
                if (yearFilter.equals("All Years")) {
                    yearMatch = true;
                } else {
                    int selectedYear = Integer.parseInt(yearFilter);
                    yearMatch = (cal.get(Calendar.YEAR) == selectedYear);
                }

                if (monthMatch && yearMatch) {
                    breakdown.merge(t.getCategory(), t.getAmount(), Double::sum);
                }
            }
        }
        return breakdown;
    }
}