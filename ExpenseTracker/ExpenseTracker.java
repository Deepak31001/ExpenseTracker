package ExpenseTracker;

import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Transaction {
    String type; // Income or Expense
    String category;
    double amount;
    LocalDate date;

    public Transaction(String type, String category, double amount, LocalDate date) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public String toCSV() {
        return type + "," + category + "," + amount + "," + date;
    }

    public static Transaction fromCSV(String line) {
        String[] parts = line.split(",");
        return new Transaction(parts[0], parts[1], Double.parseDouble(parts[2]), LocalDate.parse(parts[3]));
    }
}

public class ExpenseTracker {
    static List<Transaction> transactions = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Expense Tracker ---");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. Load from file");
            System.out.println("4. Save to file");
            System.out.println("5. Show Monthly Summary");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) { 
                case 1: addTransaction("Income"); break;
                case 2: addTransaction("Expense"); break;
                case 3: loadFromFile(); break;
                case 4: saveToFile(); break;
                case 5: showMonthlySummary(); break;
                case 6: System.exit(0);
                default: System.out.println("Invalid choice!");
            }
        }
    }

    static void addTransaction(String type) {
        System.out.print("Enter category (e.g., ");
        System.out.print(type.equals("Income") ? "Salary, Business" : "Food, Rent, Travel");
        System.out.print("): ");
        String category = scanner.nextLine();

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine(), dateFormat);

        transactions.add(new Transaction(type, category, amount, date));
        System.out.println("Transaction added!");
    }

    static void saveToFile() {
        System.out.print("Enter filename to save (e.g., data.csv): ");
        String fileName = scanner.nextLine();

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Transaction t : transactions) {
                writer.println(t.toCSV());
            }
            System.out.println("Data saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    static void loadFromFile() {
        System.out.print("Enter filename to load (e.g., data.csv): ");
        String fileName = scanner.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                transactions.add(Transaction.fromCSV(line));
                count++;
            }
            System.out.println(count + " transactions loaded from " + fileName);
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    static void showMonthlySummary() {
        Map<Month, Double> incomeSummary = new HashMap<>();
        Map<Month, Double> expenseSummary = new HashMap<>();

        for (Transaction t : transactions) {
            Month month = t.date.getMonth();
            if (t.type.equals("Income")) {
                incomeSummary.put(month, incomeSummary.getOrDefault(month, 0.0) + t.amount);
            } else {
                expenseSummary.put(month, expenseSummary.getOrDefault(month, 0.0) + t.amount);
            }
        }

        System.out.println("\n--- Monthly Summary ---");
        for (Month month : Month.values()) {
            double income = incomeSummary.getOrDefault(month, 0.0);
            double expense = expenseSummary.getOrDefault(month, 0.0);
            if (income > 0 || expense > 0) {
                System.out.printf("%s -> Income: %.2f, Expense: %.2f, Net: %.2f\n",
                        month, income, expense, income - expense);
            }
        }
    }
}
