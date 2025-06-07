import java.io.Serializable;
import java.util.Date;

/**
 * Represents a financial transaction with date, description, amount, category, and type.
 * This class implements Serializable to allow for object serialization.
 */
public class Transaction implements Serializable {
    /**
     * A version control identifier for serialization.
     * This ensures compatibility during deserialization of serialized Transaction objects.
     */
    private static final long serialVersionUID = 1L;

    private Date date;
    private String description;
    private double amount;
    private String category;
    private boolean isIncome;


    /**
     * Constructs a new Transaction with the specified parameters.
     * If the date string is invalid, the current date will be used instead.
     *
     * @param dateStr     the transaction date in YYYY-MM-DD format
     * @param description a description of the transaction (cannot be null or empty)
     * @param amount      the monetary amount of the transaction (must be positive)
     * @param category    the category of the transaction (e.g., "Food", "Rent")
     * @param isIncome    true if this is an income transaction, false if it's an expense
     * @throws IllegalArgumentException if description is null or empty
     */
    public Transaction(String dateStr, String description, double amount, String category, boolean isIncome) {
        try {
            this.date = java.sql.Date.valueOf(dateStr);
        } catch (IllegalArgumentException e) {
            this.date = new Date(); // Use current date if invalid format
        }
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.isIncome = isIncome;
    }

    // Getters
    public Date getDate() { return date; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public boolean isIncome() { return isIncome; }
}