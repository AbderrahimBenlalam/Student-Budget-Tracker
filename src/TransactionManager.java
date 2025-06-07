import java.util.*;
import java.io.*;
import javax.swing.JOptionPane;

/**
 * Manages a collection of financial transactions with persistence capabilities.
 * Provides methods for adding transactions, retrieving transaction data,
 * generating summaries, and automatically saving/loading data to/from disk.
 */
public class TransactionManager {
    private List<Transaction> transactions = new ArrayList<>();
    private static final String DATA_FILE = "transactions.dat";

    /**
     * Constructs a new TransactionManager and immediately attempts
     * to load any previously saved transactions from disk.
     */
    public TransactionManager() {
        loadData();
    }

    /**
     * Adds a new transaction to the manager and persists the changes to disk.
     *
     * @param transaction the transaction to add (cannot be null)
     * @throws IllegalArgumentException if transaction is null
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        saveData();
    }


    /**
     * Returns an unmodifiable view of all transactions.
     *
     * @return an unmodifiable list containing all transactions
     */
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    /**
     * Calculates monthly summary statistics for the specified month and year.
     *
     * @param month the month to summarize (1-12)
     * @param year the year to summarize
     * @return an array where:
     *         - index 0 contains total income
     *         - index 1 contains total expenses
     */
    public double[] getMonthlySummary(int month, int year) {
        double totalIncome = 0;
        double totalExpenses = 0;

        Calendar cal = Calendar.getInstance();
        for (Transaction t : transactions) {
            cal.setTime(t.getDate());

            if (cal.get(Calendar.MONTH) + 1 == month && cal.get(Calendar.YEAR) == year) {
                if (t.isIncome()) {
                    totalIncome += t.getAmount();
                } else {
                    totalExpenses += t.getAmount();
                }
            }
        }

        return new double[]{totalIncome, totalExpenses};
    }

    /**
     * Saves all transactions to disk in serialized form.
     * Displays an error message dialog if the operation fails.
     */
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(transactions);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving transactions: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads transactions from disk if the data file exists.
     * Displays an error message dialog if the operation fails.
     *
     * @SuppressWarnings("unchecked") Suppresses warnings about unchecked cast
     * from Object to List<Transaction> during deserialization
     */
    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            transactions = (List<Transaction>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error loading transactions: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
