import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CurrencyConverterApp {

    // Counter for unique transaction IDs
    static AtomicInteger idCounter = new AtomicInteger(1);
    
    // Map to store transactions by ID
    static Map<Integer, PurchaseTransaction> transactions = new HashMap<>();

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.flush();

        // Print welcome message and program description
        System.out.println("Welcome to Currency Converter!");
        System.out.println("This application allows you to:");
        System.out.println("- Add purchase transactions in USD");
        System.out.println("- Convert and retrieve transactions in other currencies");
        System.out.println();

        // Infinite loop for main menu
        while (true) {
            System.out.println("1. Add Transaction");
            System.out.println("2. Retrieve Transaction in Another Currency");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // clear buffer

            // Handle user input
            switch (choice) {
                case 1 -> addTransaction(scanner);
                case 2 -> retrieveConvertedTransaction(scanner);
                case 3 -> System.exit(0);
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // Add a new purchase transaction
    static void addTransaction(Scanner scanner) {
        System.out.print("Description (max 50 chars): ");
        String desc = scanner.nextLine();
        if (desc.length() > 50) {
            System.out.println("Description too long.");
            return;
        }

        System.out.print("Amount in USD: ");
        double amount;
        try {
            // Read and round amount
            amount = Math.round(scanner.nextDouble() * 100.0) / 100.0;
            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Invalid amount.");
            return;
        }
        scanner.nextLine(); // clear newline

        int id = idCounter.getAndIncrement();
        LocalDate date = LocalDate.now();

        // Create and save transaction
        PurchaseTransaction transaction = new PurchaseTransaction(id, desc, date, amount);
        transactions.put(id, transaction);
        saveToCSV(transaction);

        System.out.println("Transaction added successfully!");
    }

    // Save a transaction to a CSV file
    static void saveToCSV(PurchaseTransaction transaction) {
        try (FileWriter writer = new FileWriter("transactions.csv", true)) {
            writer.append(transaction.id + ",");
            writer.append(transaction.description + ",");
            writer.append(transaction.date.toString() + ",");
            writer.append(String.valueOf(transaction.amount));
            writer.append("\n");
        } catch (IOException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }

    // Retrieve and convert a transaction into a different currency
    static void retrieveConvertedTransaction(Scanner scanner) throws Exception {
        System.out.print("Transaction ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        // Look up transaction by ID
        PurchaseTransaction txn = transactions.get(id);
        if (txn == null) {
            System.out.println("Transaction not found.");
            return;
        }

        // Ask user for target currency
        System.out.print("Target Currency Code (e.g., EUR, JPY): ");
        String target = scanner.nextLine().toUpperCase();

        // Build API request URL
        String rateApi = "https://api.exchangerate.host/" + txn.date + "?base=USD&symbols=" + target;
        URL url = new URL(rateApi);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Read response from API
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        // Parse exchange rate from response JSON
        String response = content.toString();
        int startIndex = response.indexOf(target + ":") + target.length() + 1;
        int endIndex = response.indexOf("}", startIndex);
        double rate = Double.parseDouble(response.substring(startIndex, endIndex).replaceAll("[^0-9.]", ""));

        // Calculate converted amount
        double converted = Math.round(txn.amount * rate * 100.0) / 100.0;

        // Output transaction details
        System.out.printf("ID: %d\nDescription: %s\nDate: %s\nUSD: %.2f\nRate: %.4f\nConverted (%s): %.2f\n",
                txn.id, txn.description, txn.date, txn.amount, rate, target, converted);
    }
}

// Class to represent a purchase transaction
class PurchaseTransaction {
    int id;                  // Transaction ID
    String description;      // Description of transaction
    LocalDate date;          // Date of transaction
    double amount;           // Amount in USD

    public PurchaseTransaction(int id, String description, LocalDate date, double amount) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.amount = amount;
    }
}
