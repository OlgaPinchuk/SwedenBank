package project.ui;

import project.account.*;
import project.auth.PasswordHasher;
import project.customer.Customer;
import project.utils.*;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CustomerMenu extends Menu {
    private  Storage storage;
    private Customer customer;
    private List<BankAccount> customerAccounts;
    private final static String instruction = "Please select an option:";
    private final static List<String> options = Arrays.asList(
            "Show balance",
            "Send money",
            "Check transactions",
            "Rename account",
            "Change password",
            "Deposit Money (DEMO ONLY)",
            "Withdraw Money (DEMO ONLY)",
            "Log out",
            "Exit"
    );

    public CustomerMenu(Customer customer) {
        super(instruction, options);
        this.storage = ObjectFactory.getStorage();
        this.customer = storage.getCustomerBySocialNumber(customer.getSocialNumber());
       this.customerAccounts = storage.getAccountsByCustomerId(customer.getId());
    }

    public void run() {
            printCustomerDetails();
            printAccountsDetails();

            showMenu();
    }

    @Override
    public void handleChoice() {
        String input = scanner.nextLine();

        try {
            int selectedOption = Integer.parseInt(input.trim());
            switch (selectedOption) {
                case 1 -> checkBalance();
                case 2 -> transferMoney();
                case 3 -> printTransactionsHistory();
                case 4 -> renameAccount();
                case 5 -> changePassword();
                case 6 -> depositMoney();
                case 7 -> withdrawMoney();
                case 8 -> logout();
                case 9 -> exit();
                default -> showInvalidOptionMessage();
            }
        }
        catch (NumberFormatException exception) {
            showInvalidOptionMessage();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        showMenu();
    }

    private void printCustomerDetails() {
        List<Integer> columnsWidth = List.of(40, 25);
        List<String> headers = List.of("Full Name",  "Social Security Number");
        List<List<String>> rows = new ArrayList<>();

        List<String> newRow = List.of(customer.getFullName(), customer.getSocialNumber());
        rows.add(newRow);

        new Table(columnsWidth, headers, rows);
        printBlankLine();
    }

    private void printAccountsDetails() {
        List<Integer> columnsWidth = List.of(40, 25);
        List<String> headers = List.of("Account",  "Balance");
        List<List<String>> rows = new ArrayList<>();
        List<BankAccount> customerAccounts = storage.getAccountsByCustomerId(customer.getId());

        for(BankAccount account : customerAccounts) {
            String accountName = account.getName() + " - " + account.getAccountNumber();
            String balance = account.getFormattedBalance();
            List<String> newRow = List.of(accountName, balance);
            rows.add(newRow);
        }

        new Table(columnsWidth, headers, rows);

        printBlankLine();
    }

    private void checkBalance() {
        BankAccount customerAccount = customerAccounts.get(0); // In this sprint a customer has 1 account.
        ConsoleMessage.showSuccessMessage("Available balance: " + customerAccount.getFormattedBalance());
    }

    private void changePassword() throws NoSuchAlgorithmException {
        String newPassword = getInput("Insert new password or print exit to return: ");
        if (newPassword == null) {
            return;
        }

        String hashedPassword = PasswordHasher.hashPassword(newPassword);
        customer.setPassword(hashedPassword);
        storage.updateCustomer(customer);
        ConsoleMessage.showSuccessMessage("The password changed successfully!");
    }

    private void renameAccount() {
       BankAccount account = customerAccounts.get(0);
       String newName = getInput("Insert new name or print exit to return: ");

       if (newName == null) {
           return;
       }
       account.setName(newName.trim());
       storage.updateAccount(account);
       ConsoleMessage.showSuccessMessage("The account renamed successfully. New name: " + account.getName());
       printAccountsDetails();
    }

    private void depositMoney() {
        BankAccount customerAccount = customerAccounts.get(0); // In this sprint a customer has 1 account.
        UUID accountId = customerAccount.getAccountId();

        while(true) {
            BigDecimal amount = getTransactionAmount();
            if (amount == null) {
                continue;
            }

            try {
                BigDecimal newBalance = customerAccount.getBalance().add(amount);
                customerAccount.setBalance(newBalance);
                storage.updateAccount(customerAccount);

                saveTransaction(accountId, null, amount, customerAccount.getCurrency(), TransactionType.DEPOSIT);
                customerAccount.setTransactionsHistory(storage.getAccountTransactionsHistory(accountId));

                ConsoleMessage.showSuccessMessage("Deposit successful. New balance: " + customerAccount.getFormattedBalance());
                break;
            }
            catch (NumberFormatException e) {
                ConsoleMessage.showErrorMessage("Invalid amount entered. Please enter a valid number.");
            }
        }
    }

    private void withdrawMoney() {
        BankAccount account = customerAccounts.get(0); // In this sprint a customer has 1 account.
        UUID accountId = account.getAccountId();

        while(true) {
            BigDecimal amount = getTransactionAmount();
            if (amount == null) {
                continue;
            }

            BigDecimal newBalance = account.getBalance().subtract(amount);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                ConsoleMessage.showErrorMessage("Insufficient funds in the account. Withdrawal failed.");
                return;
            }

            try {
                saveTransaction(accountId, null, amount, account.getCurrency(), TransactionType.WITHDRAWAL);
                account.setTransactionsHistory(storage.getAccountTransactionsHistory(accountId));

                account.setBalance(newBalance);
                storage.updateAccount(account);

                ConsoleMessage.showSuccessMessage("Withdrawal successful. New balance: " + account.getFormattedBalance());
                break;
            }
            catch (NumberFormatException e) {
                ConsoleMessage.showErrorMessage("Invalid amount entered. Please enter a valid number.");
            }
            catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void transferMoney() {
        BankAccount senderAccount = customerAccounts.get(0);

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
    private void printTransactionsHistory() {
        BankAccount customerAccount = storage.getAccountsByCustomerId(customer.getId()).get(0);
        List<Transaction> transactions = storage.getAccountTransactionsHistory(customerAccount.getAccountId());

        if (transactions.size() == 0) {
            ConsoleMessage.showInfoMessage("No transactions found.");
            return;
        }

        List<Integer> columnsWidth = List.of(20, 15, 30);
        List<String> headers = List.of("Date", "Sum", "Sender");
        List<List<String>> rows = new ArrayList<>();

        for (Transaction transaction : transactions) {
            String date = transaction.date().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String sum = formatAmountWithSign(transaction);
            String sender = getTransactionLabel(transaction);

            List<String> newRow = List.of(date, sum, sender);
            rows.add(newRow);
        }

        new Table(columnsWidth, headers, rows);
        printBlankLine();
    }

    private void logout() {
        ConsoleMessage.clearConsole();
        HomeMenu homeMenu = new HomeMenu();
        homeMenu.run();
    }

    private void performMoneyTransfer(BankAccount senderAccount, BankAccount recipientAccount, BigDecimal amount) {
        UUID senderAccountId = senderAccount.getAccountId();
        UUID recipientAccountId = recipientAccount.getAccountId();
        Currency currency = senderAccount.getCurrency();

        saveTransaction(senderAccountId, recipientAccountId, amount, currency, TransactionType.TRANSFER);

        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        recipientAccount.setBalance(recipientAccount.getBalance().add(amount));
        storage.updateAccount(senderAccount);
        storage.updateAccount(recipientAccount);
        senderAccount.setTransactionsHistory(storage.getAccountTransactionsHistory(senderAccountId));
        recipientAccount.setTransactionsHistory(storage.getAccountTransactionsHistory(recipientAccountId));

        ConsoleMessage.showSuccessMessage("Money transfer successful.");
    }
    private String getTransactionLabel(Transaction transaction) {
        TransactionType transactionType = transaction.transactionType();

        switch (transactionType) {
            case WITHDRAWAL -> {
                return "Withdraw";
            }
            case DEPOSIT -> {
                return "Deposit";
            }
            case TRANSFER -> {
                UUID senderAccountId = transaction.senderAccountId();
                UUID recipientAccountId = transaction.recipientAccountId();
                if (senderAccountId.equals(customerAccounts.get(0).getAccountId())) {
                    return storage.getCustomerByAccountId(recipientAccountId).getFullName();
                } else {
                    return storage.getCustomerByAccountId(senderAccountId).getFullName();
                }
            }
            default -> {
                return "Unknown";
            }
        }
    }

    private String getRecipientAccountNumber() {
        String recipientAccountNumber;
        String regex = "\\d{" + BankAccount.ACCOUNT_NUMBER_LENGTH + "}";

        while (true) {
            try {
                recipientAccountNumber = getInput("Enter the recipient's account number or 'exit' to return: ");
                if (shouldReturnToMenu(recipientAccountNumber)) {
                    returnToMenu();
                    return null;
                }

                if (!recipientAccountNumber.matches(regex)) {
                    ConsoleMessage.showErrorMessage("Invalid bank account number.");
                    continue;
                }

                if (recipientAccountNumber.equals(customerAccounts.get(0).getAccountNumber())) {
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
            String input = getInput("Enter the amount or print 'exit' to return: ");

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
    private String formatAmountWithSign(Transaction transaction) {
        BankAccount customerAccount = customerAccounts.get(0);
        BigDecimal amount = transaction.amount();
        TransactionType transactionType = transaction.transactionType();
        String transactionSign;


        switch (transactionType) {
            case WITHDRAWAL -> transactionSign = "-";
            case DEPOSIT -> transactionSign = "+";
            case TRANSFER -> {
                if (transaction.senderAccountId().equals(customerAccount.getAccountId())) {
                    transactionSign = "-";
                } else {
                    transactionSign = "+";
                }
            }
            default -> transactionSign = "";
        }

        return transactionSign + amount.toString();
    }

    private void saveTransaction(UUID senderAccountId, UUID recipientAccountId, BigDecimal amount, Currency currency, TransactionType type) {
        Transaction transaction = new Transaction(senderAccountId, recipientAccountId, LocalDateTime.now(), amount, currency, type);
        storage.saveTransaction(transaction);
    }

}