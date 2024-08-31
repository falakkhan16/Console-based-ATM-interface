
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;

class ATM {
    private ArrayList<Account> accounts;
    private Account currentAccount; // Store logged-in account

    public ATM() {
        accounts = new ArrayList<>();
        // Initialize accounts with sample data for testing
        accounts.add(new Account("user1", "1234", 10000.0));
        accounts.add(new Account("user2", "4321", 25000.0));
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean loggedIn = false;

        while (!loggedIn) {
            System.out.println("Welcome to the ATM");
            System.out.print("Enter your user ID: ");
            String userId = scanner.nextLine();
            System.out.print("Enter your PIN: ");
            String pin = scanner.nextLine();

            loggedIn = authenticateUser(userId, pin);

            if (!loggedIn) {
                System.out.println("Invalid user ID or PIN. Please try again.");
            }
        }

        if (loggedIn) {
            displayMenu();

            while (true) {
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        displayTransactionHistory();
                        break;
                    case 2:
                        withdraw();
                        break;
                    case 3:
                        deposit();
                        break;
                    case 4:
                        transfer();
                        break;
                    case 5:
                        System.out.println("Thank you for using the ATM!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    private boolean authenticateUser(String userId, String pin) {
        for (Account account : accounts) {
            if (account.getUserId().equals(userId) && account.getPin().equals(pin)) {
                currentAccount = account; // Store logged-in account
                return true;
            }
        }
        return false;
    }

    private void displayMenu() {
        System.out.println("1. Transaction History");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. Transfer");
        System.out.println("5. Quit");
    }

    private void displayTransactionHistory() {
        if (currentAccount != null) {
            ArrayList<Transaction> history = currentAccount.getTransactionHistory();
            if (history.isEmpty()) {
                System.out.println("No transaction history found.");
            } else {
                System.out.println("Transaction History:");
                for (Transaction transaction : history) {
                    System.out.println(transaction.toString());
                }
            }
        } else {
            System.out.println("Error: Could not find logged-in account.");
        }
    }

    private void withdraw() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter withdrawal amount: ");
        double withdrawalAmount = scanner.nextDouble();

        if (currentAccount != null) {
            if (withdrawalAmount <= currentAccount.getBalance()) {
                // Update the account balance
                currentAccount.setBalance(currentAccount.getBalance() - withdrawalAmount);

                // Create a transaction record
                Transaction transaction = new Transaction(new Date(), withdrawalAmount, "Withdrawal", currentAccount);
                currentAccount.addTransaction(transaction);

                System.out.println("Withdrawal successful. Your new balance is: " + currentAccount.getBalance());
            } else {
                System.out.println("Insufficient funds.");
            }
        } else {
            System.out.println("Error: Could not find logged-in account.");
        }
    }

    private void deposit() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter deposit amount: ");
        double depositAmount = scanner.nextDouble();

        if (currentAccount != null) {
            // Update the account balance
            currentAccount.setBalance(currentAccount.getBalance() + depositAmount);

            // Create a transaction record
            Transaction transaction = new Transaction(new Date(), depositAmount, "Deposit", currentAccount);
            currentAccount.addTransaction(transaction);

            System.out.println("Deposit successful. Your new balance is: " + currentAccount.getBalance());
        } else {
            System.out.println("Error: Could not find logged-in account.");
        }
    }

    private void transfer() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the recipient's user ID: ");
        String recipientId = scanner.nextLine();

        System.out.print("Enter transfer amount: ");
        double transferAmount = scanner.nextDouble();

        if (currentAccount != null) {
            Account recipientAccount = findAccountByUserId(recipientId);

            if (recipientAccount != null && recipientAccount != currentAccount) {
                if (transferAmount <= currentAccount.getBalance()) {
                    // Update both accounts
                    currentAccount.setBalance(currentAccount.getBalance() - transferAmount);
                    recipientAccount.setBalance(recipientAccount.getBalance() + transferAmount);

                    // Create transaction records
                    Transaction transaction1 = new Transaction(new Date(), transferAmount, "Transfer (Sent)", currentAccount);
                    currentAccount.addTransaction(transaction1);
                    Transaction transaction2 = new Transaction(new Date(), transferAmount, "Transfer (Received)", recipientAccount);
                    recipientAccount.addTransaction(transaction2);

                    System.out.println("Transfer successful. Your new balance is: " + currentAccount.getBalance());
                } else {
                    System.out.println("Insufficient funds.");
                }
            } else {
                System.out.println("Error: Recipient account not found or cannot transfer to yourself.");
            }
        } else {
            System.out.println("Error: Could not find logged-in account.");
        }
    }

    private Account findAccountByUserId(String userId) {
        for (Account account : accounts) {
            if (account.getUserId().equals(userId)) {
                return account;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.start();
    }
}

class Account {
    private String userId;
    private String pin;
    private double balance;
    private ArrayList<Transaction> transactionHistory;

    public Account(String userId, String pin, double balance) {
        this.userId = userId;
        this.pin = pin;
        this.balance = balance;
        transactionHistory = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ArrayList<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }
}

class Transaction {
    private Date timestamp;
    private double amount;
    private String type;
    private Account account;

    public Transaction(Date timestamp, double amount, String type, Account account) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.type = type;
        this.account = account;
    }

    @Override
    public String toString() {
        return "Date: " + timestamp + " | Amount: " + amount + " | Type: " + type;
    }
}