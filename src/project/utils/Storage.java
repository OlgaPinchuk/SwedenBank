package project.utils;


import project.account.BankAccount;
import project.account.Transaction;
import project.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Storage {
    private final FileManager fileManager;
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

    public void updateUser(User updatedUser) {
        ArrayList<User> users = getUsers();
        for(int i = 0; i < users.size(); i++) {
            User user = users.get(i);

            if(user.getId().equals(updatedUser.getId())) {
                users.set(i, updatedUser);
                fileManager.writeObjects(USERS_FILE, users);
                return;
            }
        }
    }

    public User getUserByAccountId(UUID accountId) {
        List<BankAccount> accounts = getAccounts();

        for (BankAccount account : accounts) {
            if (account.getAccountId().equals(accountId)) {
                UUID userId = account.getUserId();
                return getUserById(userId);
            }
        }
        return null;
    }

    public User getUserById(UUID userId) {
        List<User> users = getUsers();

        for (User user : users) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }

        return null;
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

    public ArrayList<BankAccount> getAccounts() {
        return fileManager.readObjects(ACCOUNTS_FILE);
    }

    public void updateAccount(BankAccount updatedAccount) {
        ArrayList<BankAccount> accounts = getAccounts();

        for(int i = 0; i < accounts.size(); i++) {
            BankAccount account = accounts.get(i);

            if(account.getAccountId().equals(updatedAccount.getAccountId())) {
                accounts.set(i, updatedAccount);
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
            UUID senderAccountId = transaction.senderAccountId();
            UUID recipientAccountId =  transaction.recipientAccountId();

            if(senderAccountId.equals(accountId) || (recipientAccountId != null && recipientAccountId.equals(accountId))) {
                userTransactions.add(transaction);
            }
        }
        return userTransactions;
    }
}
