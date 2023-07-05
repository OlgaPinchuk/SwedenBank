package project.utils;


import project.account.BankAccount;
import project.account.Transaction;
import project.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Storage {
    private FileManager fileManager;
    private final String USERS_FILE = "src/project/data/users.txt";
    private final String ACCOUNTS_FILE = "src/project/data/accounts.txt";

    private final String TRANSACTIONS_FILE = "src/project/data/transactions.txt";

    public Storage() {
        this.fileManager = new FileManager();
    }

    public ArrayList<User> getUsers() {
        return fileManager.readObjects(USERS_FILE);
    }

    public void saveUser(User newUser) {
        ArrayList<User> users = getUsers();
        users.add(newUser);
        fileManager.writeObjects(USERS_FILE, users);
    }

    public ArrayList<BankAccount> getAccounts() {
        return fileManager.readObjects(ACCOUNTS_FILE);
    }

    public void updateAccount(BankAccount account) {
        ArrayList<BankAccount> accounts = getAccounts();
        for(int i = 0; i < accounts.size(); i++) {
            BankAccount currentAccount = accounts.get(i);
            if(currentAccount.getAccountId().equals(account.getAccountId())) {
                System.out.println("Account " + currentAccount.getAccountId());
                System.out.println("Account balance" + currentAccount.getBalance());
                accounts.set(i, currentAccount);
                fileManager.writeObjects(ACCOUNTS_FILE, accounts);
                return;
            }
        }
    }

    public void saveAccount(BankAccount account) {
        ArrayList<BankAccount> accounts = getAccounts();
        accounts.add(account);
        fileManager.writeObjects(ACCOUNTS_FILE, accounts);
    }

    public User getUserBySocialNumber(String socialNumber) {
        var users = getUsers();
        for(User user : users) {
            if(user.getSocialNumber().equals(socialNumber)) {
                return user;
            }
        }
        return null;
    }
    public ArrayList<BankAccount> getAccountsByUserId(UUID userId) {
        ArrayList<BankAccount> userAccounts = new ArrayList<>();
        for(BankAccount account : getAccounts() ) {
            if(account.getUserId().equals(userId)) {
                userAccounts.add(account);
            }
        }
        return userAccounts;
    }
    public BankAccount findAccountByNumber(String accountNumber) {
        for (BankAccount account : getAccounts()) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    public ArrayList<Transaction> getTransactions() {
        return fileManager.readObjects(TRANSACTIONS_FILE);
    }

    public void saveTransaction(Transaction transaction) {
        ArrayList<Transaction> transactions = getTransactions();
        transactions.add(transaction);
        fileManager.writeObjects(TRANSACTIONS_FILE, transactions);
    }

    public  ArrayList<Transaction> getAccountTransactionsHistory(UUID accountId) {
        List<Transaction> transactions = fileManager.readObjects(TRANSACTIONS_FILE);
        ArrayList<Transaction> userTransactions = new ArrayList<>();

        for(Transaction transaction : transactions) {
            if(transaction.senderAccountId() == accountId || transaction.recipientAccountId() == accountId) {
                userTransactions.add(transaction);
            }
        }
        return userTransactions;
    }
}
