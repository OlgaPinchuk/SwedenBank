package project.ui;

import project.account.*;
import project.user.User;
import project.utils.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CustomerMenu extends Menu {
    private User user;
    private  Storage storage;
    private List<BankAccount> userAccounts;
    private final static String instruction = "Please select an option:";
    private final static List<String> options = Arrays.asList(
            "Show balance",
            "Send money",
            "Check the latest transactions",
            "Deposit Money (DEMO ONLY)",
            "Withdraw Money (DEMO ONLY)",
            "Exit"
    );

    public CustomerMenu(User user) {
        super(instruction, options);
        this.storage = ObjectFactory.getStorage();
        this.user = storage.getUserBySocialNumber(user.getSocialNumber());
      //  this.user = user;
        this.userAccounts = storage.getAccountsByUserId(user.getId());
    }

    public void run() {
            printCustomerDetails();
            printAccountsDetails();

            showMenu();
    }

    @Override
    public void handleUserChoice() {
        String input = scanner.nextLine();

        try {
            int selectedOption = Integer.parseInt(input.trim());
            switch (selectedOption) {
                case 1 -> checkBalance();
                case 2 -> transferMoney();
                case 3 -> printTransactionsHistory();
                case 4 -> depositMoney();
                case 5 -> withdrawMoney();
                // logout
                case 6 -> exit();
                default -> showInvalidOptionMessage();
            }
        }
        catch (NumberFormatException exception) {
            showInvalidOptionMessage();
        }
        showMenu();
    }

    @Override
    protected String getHeader() {
        return null;
    }

    private void checkBalance() {
        BankAccount userAccount = userAccounts.get(0);// In this sprint a user has 1 account.
        ConsoleMessage.showSuccessMessage("Available balance: " + userAccount.getFormattedBalance());
    }

    private void printCustomerDetails() {
        List<Integer> columnsWidth = List.of(40, 25);
        List<String> headers = List.of("Full Name",  "Social Security Number");
        List<List<String>> rows = new ArrayList<>();

        List<String> newRow = List.of(user.getFullName(), user.getSocialNumber());
        rows.add(newRow);

        new Table(columnsWidth, headers, rows);
        printBlankLine();
    }

    private void printAccountsDetails() {
        List<Integer> columnsWidth = List.of(40, 40);
        List<String> headers = List.of("Account",  "Balance");
        List<List<String>> rows = new ArrayList<>();
        List<BankAccount> userAccounts = storage.getAccountsByUserId(user.getId());

        for(BankAccount account : userAccounts) {
            String accountName = account.getName() + " - " + account.getAccountNumber();
            String balance = account.getFormattedBalance();
            List<String> newRow = List.of(accountName, balance);
            rows.add(newRow);
        }

        new Table(columnsWidth, headers, rows);

        printBlankLine();
    }

    private void depositMoney() {
        BankAccount userAccount = userAccounts.get(0); // In this sprint a user has 1 account.
        UUID userAccountId = userAccount.getAccountId();

        while(true) {
            BigDecimal amount = getTransactionAmount();
            if (amount == null) {
                continue;
            }

            try {
                BigDecimal newBalance = userAccount.getBalance().add(amount);
                userAccount.setBalance(newBalance);

                saveTransaction(userAccountId, null, amount, userAccount.getCurrency());
                userAccount.setTransactionsHistory(storage.getAccountTransactionsHistory(userAccountId));

                ConsoleMessage.showSuccessMessage("Deposit successful. New balance: " + userAccount.getFormattedBalance());
                break;
            }
            catch (NumberFormatException e) {
                ConsoleMessage.showErrorMessage("Invalid amount entered. Please enter a valid number.");
            }
        }
    }

    private void withdrawMoney() {
        BankAccount userAccount = userAccounts.get(0); // In this sprint a user has 1 account.
        UUID userAccountId = userAccount.getAccountId();

        while(true) {
            BigDecimal amount = getTransactionAmount();
            if (amount == null) {
                continue;
            }

            try {
                BigDecimal newBalance = userAccount.getBalance().subtract(amount);
                if(newBalance.compareTo(BigDecimal.ZERO) >= 0) {
                    userAccount.setBalance(newBalance);
                }

                saveTransaction(userAccountId, null, amount, userAccount.getCurrency());
                userAccount.setTransactionsHistory(storage.getAccountTransactionsHistory(userAccountId));

                ConsoleMessage.showSuccessMessage("Withdrawal successful. New balance: " + userAccount.getFormattedBalance());
                break;
            }
            catch (NumberFormatException e) {
                ConsoleMessage.showErrorMessage("Invalid amount entered. Please enter a valid number.");
            }
        }
    }

    private void transferMoney() {
        BankAccount senderAccount = userAccounts.get(0);

        String recipientAccountNumber = getRecipientAccountNumber();
        if (recipientAccountNumber == null) {
            return;
        }

        BigDecimal amount = getTransactionAmount();
        if (amount == null) {
            return;
        }

        if (senderAccount.getBalance().compareTo(amount) < 0) {
            ConsoleMessage.showErrorMessage("Insufficient funds in the sender's account.");
            return;
        }

        BankAccount recipientAccount = storage.findAccountByNumber(recipientAccountNumber);
        if (recipientAccount == null) {
            ConsoleMessage.showErrorMessage("Recipient account not found.");
            return;
        }

        performMoneyTransfer(senderAccount, recipientAccount, amount);
    }

    private String getRecipientAccountNumber() {
        String recipientAccountNumber;
        String regex = "\\d{" + BankAccount.ACCOUNT_NUMBER_LENGTH + "}";

        while (true) {
            try {
                recipientAccountNumber = getUserInput("Enter the recipient's account number or 'exit' to return: ");
                if (shouldReturnToMenu(recipientAccountNumber)) {
                    returnToMenu();
                    return null;
                }

                if (!recipientAccountNumber.matches(regex)) {
                    ConsoleMessage.showErrorMessage("Invalid bank account number.");
                    continue;
                }

                if (recipientAccountNumber.equals(userAccounts.get(0).getAccountNumber())) {
                    ConsoleMessage.showErrorMessage("Cannot transfer money to the same account.");
                    continue;
                }

                return recipientAccountNumber;
            }
            catch (Exception e) {
                ConsoleMessage.showErrorMessage("An error occurred while getting input. Please try again.");
            }
        }
    }

    private BigDecimal getTransactionAmount() {
        while (true) {
            String input = getUserInput("Enter the amount or print 'exit' to return: ");

            if (shouldReturnToMenu(input)) {
                returnToMenu();
                return null;
            }

            try {
                BigDecimal amount = new BigDecimal(input);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    ConsoleMessage.showErrorMessage("Transaction amount should be greater than zero.");
                } else {
                    return amount;
                }
            } catch (NumberFormatException exception) {
                ConsoleMessage.showErrorMessage("Invalid transaction amount.");
            }
        }
    }

    private void saveTransaction(UUID senderAccountId, UUID recipientAccountId, BigDecimal amount, Currency currency) {
        Transaction transaction = new Transaction(senderAccountId, recipientAccountId, LocalDateTime.now(), amount, currency);
        storage.saveTransaction(transaction);
    }

    private void performMoneyTransfer(BankAccount senderAccount, BankAccount recipientAccount, BigDecimal amount) {
        UUID senderAccountId = senderAccount.getAccountId();
        UUID recipientAccountId = recipientAccount.getAccountId();
        Currency currency = senderAccount.getCurrency();

        saveTransaction(senderAccountId, recipientAccountId, amount, currency);

        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        recipientAccount.setBalance(recipientAccount.getBalance().add(amount));
        senderAccount.setTransactionsHistory(storage.getAccountTransactionsHistory(senderAccountId));
        recipientAccount.setTransactionsHistory(storage.getAccountTransactionsHistory(recipientAccountId));

        ConsoleMessage.showSuccessMessage("Money transfer successful.");
    }

    private void printTransactionsHistory() {
        BankAccount userAccount = storage.getAccountsByUserId(user.getId()).get(0);
        List<Transaction> transactions = storage.getAccountTransactionsHistory(userAccount.getAccountId());

        if(transactions.size() == 0) {
            ConsoleMessage.showInfoMessage("No transactions found.");
            return;
        }

        List<Integer> columnsWidth = List.of(20, 15, 30);
        List<String> headers = List.of("Date", "Sum", "Sender");
        List<List<String>> rows = new ArrayList<>();


        for (Transaction transaction : transactions) {
            String date = transaction.date().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String sum = transaction.amount().toString();
            String sender = transaction.recipientAccountId() == null ? "Deposit" : storage.getUserByAccountId(transaction.senderAccountId()).getFullName();

            List<String> newRow = List.of(date, sum, sender);
            rows.add(newRow);
        }
        new Table(columnsWidth, headers, rows);
        printBlankLine();

    }

}