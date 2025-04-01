import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CurrencyConverterApp {

    // Store transaction IDs starting from 1
    static AtomicInteger idCounter = new AtomicInteger(1);
    // Store all transactions in memory using ID as key
    static Map<Integer, PurchaseTransaction> transactions = new HashMap<>();

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.flush();
        // Welcome message for the Currency Converter Application
        System.out.println("Welcome to Currency Converter!");
        System.out.println("This application allows you to:");
        System.out.println("- Add purchase transactions in USD");
        System.out.println("- Convert and retrieve transactions in other currencies");
        System.out.println();

        // Main program loop
        while (true) {
            // Display menu options
            System.out.println("1. Add Transaction");
            System.out.println("2. Retrieve Transaction in Another Currency");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear the input buffer

            // Process user choice
            switch (choice) {
                case 1 -> addTransaction(scanner);
                case 2 -> retrieveConvertedTransaction(scanner);
                case 3 -> System.exit(0);
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void addTransaction(Scanner scanner) {
        // Get transaction description
        System.out.print("Description (max 50 chars): ");
        String desc = scanner.nextLine();
        if (desc.length() > 50) {
            System.out.println("Description too long.");
            return;
        }

        // Get and validate transaction date
        System.out.print("Transaction Date (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine();
        LocalDate date;
        try {
            date = LocalDate.parse(dateInput);
        } catch (Exception e) {
            System.out.println("Invalid date.");
            return;
        }

        // Get and validate transaction amount
        System.out.print("Amount in USD: ");
        double amount;
        try {
            // Round to 2 decimal places
            amount = Math.round(scanner.nextDouble() * 100.0) / 100.0;
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive.");
            }
        } catch (Exception e) {
            System.out.println("Invalid amount.");
            return;
        }

        // Save the transaction
        int id = idCounter.getAndIncrement();
        transactions.put(id, new PurchaseTransaction(id, desc, date, amount));
        System.out.println("Transaction stored with ID: " + id);
    }

    static void retrieveConvertedTransaction(Scanner scanner) throws Exception {
        // Get transaction ID
        System.out.print("Transaction ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        // Check if transaction exists
        PurchaseTransaction txn = transactions.get(id);
        if (txn == null) {
            System.out.println("Transaction not found.");
            return;
        }

        // Get target currency for conversion
        System.out.print("Target Currency Code (e.g., EUR, JPY): ");
        String target = scanner.nextLine().toUpperCase();

        // Build API URL for exchange rate
        String rateApi = "https://api.exchangerate.host/" + txn.date + "?base=USD&symbols=" + target;
        URL url = new URL(rateApi);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Read API response
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        // Extract exchange rate from response
        String response = content.toString();
        int startIndex = response.indexOf(target + ":") + target.length() + 1;
        int endIndex = response.indexOf("}", startIndex);
        double rate = Double.parseDouble(response.substring(startIndex, endIndex).replaceAll("[^0-9.]", ""));

        // Calculate converted amount
        double converted = Math.round(txn.amount * rate * 100.0) / 100.0;

        // Display transaction details
        System.out.printf("ID: %d\nDescription: %s\nDate: %s\nUSD: %.2f\nRate: %.4f\nConverted (%s): %.2f\n",
                txn.id, txn.description, txn.date, txn.amount, rate, target, converted);
    }
}

// Class to store transaction details
class PurchaseTransaction {
    int id;                  // Unique identifier
    String description;      // Transaction description
    LocalDate date;         // Transaction date
    double amount;          // Amount in USD

    public PurchaseTransaction(int id, String description, LocalDate date, double amount) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.amount = amount;
    }
}
