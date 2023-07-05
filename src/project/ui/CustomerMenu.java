package project.ui;

import project.account.BankAccount;
import project.account.Transaction;
import project.user.User;
import project.utils.ConsoleMessage;
import project.utils.ObjectFactory;
import project.utils.Storage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class CustomerMenu extends Menu {
    private User user;
    private Scanner scanner;
    private final static String instruction = "Please select an option (or type 'exit' to return to the previous menu):";
    private final static List<String> options = Arrays.asList("Send money", "Check the latest transactions", "Deposit Money (demo only)");
    public CustomerMenu(User user) {
        super(instruction, options);
        this.user = user;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        printCustomerDetails();
        printAccountsDetails();

        displayMenu();
        handleUserChoice();
    }


    @Override
    public void handleUserChoice() {
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("exit")) {
            ConsoleMessage.showInfoMessage("Returning to the previous menu");
            return;
        }

        try {
            int selectedOption = Integer.parseInt(input.trim());
            if (selectedOption > 0 && selectedOption <= options.size()) {
                switch (selectedOption) {
                    case 1 -> {
                        transferMoney();
                    }
                    case 2 -> {
                        //  see transactions history

                    }
                    case 3 -> {
                        depositMoney();

                    }
                    default -> showInvalidOptionMessage();
                }
            }
            else {
                showInvalidOptionMessage();
                handleUserChoice();
            }
        }
        catch (NumberFormatException exception) {
            showInvalidOptionMessage();
            handleUserChoice();
        }
    }


    @Override
    protected String getHeader() {
        return null;
    }

    private void depositMoney() {
        BankAccount userAccount = user.getAccounts().get(0); // In this sprint a user has 1 account.

        while(true) {
            String input = getUserInput("Enter the amount you want to deposit: ");
            if (input == null) {
                return;
            }
            try {
                BigDecimal amount = new BigDecimal(input);
                if(amount.compareTo(BigDecimal.ZERO) < 0) {
                    ConsoleMessage.showErrorMessage("Amount should be greater than zero.");
                    return;
                }
                System.out.println("B: " + userAccount.getBalance().add(amount));
                userAccount.setBalance(userAccount.getBalance().add(amount));

                ConsoleMessage.showSuccessMessage("Deposit successful. New balance: " + userAccount.getFormattedBalance());
                break;
            }
            catch (NumberFormatException e) {
                ConsoleMessage.showErrorMessage("Invalid amount entered. Please enter a valid number.");
            }

        }
    }

    private void transferMoney() {
        Storage storage = ObjectFactory.getStorage();
        BankAccount senderAccount = user.getAccounts().get(0); // In this sprint a user has 1 account.

        while(true) {
           String recipientAccountNumber = getUserInput("Enter the recipient's account number: ");
            if (recipientAccountNumber == null) {
                return;
            }

            BigDecimal amount = getTransactionAmount();
            if (amount == null) {
                return;
            }

            if(senderAccount.getBalance().compareTo(amount) < 0) {
                ConsoleMessage.showErrorMessage("Insufficient funds in the sender's account.");
                return;
            }
            BankAccount recipientAccount = storage.findAccountByNumber(recipientAccountNumber);

            UUID senderAccountId = senderAccount.getAccountId();
            UUID recipientAccountId = recipientAccount.getAccountId();
            LocalDateTime currentTime = LocalDateTime.now();
            Currency currency = senderAccount.getCurrency();

            Transaction transaction = new Transaction(senderAccountId, recipientAccountId, currentTime, amount, currency);
            storage.saveTransaction(transaction);

            senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
            recipientAccount.setBalance(recipientAccount.getBalance().add(amount));

            ConsoleMessage.showSuccessMessage("Money transfer successful.");
        }
    }

    private BigDecimal getTransactionAmount() {
        String input = getUserInput("Enter the transaction amount: ");
        if (input == null) {
            return null;
        }

        try {
            BigDecimal amount = new BigDecimal(input);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                ConsoleMessage.showErrorMessage("Transaction amount should be greater than zero.");
                return null;
            }
            return amount;
        } catch (NumberFormatException exception) {
            ConsoleMessage.showErrorMessage("Invalid transaction amount.");
            return null;
        }
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
        List<BankAccount> userAccounts = user.getAccounts();

        for(BankAccount account : userAccounts) {
            String accountName = account.getName() + " - " + account.getAccountNumber();
            String balance = account.getFormattedBalance();
            List<String> newRow = List.of(accountName, balance);
            rows.add(newRow);
        };

        new Table(columnsWidth, headers, rows);

        printBlankLine();
    }
}