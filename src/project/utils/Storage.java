package project.utils;


import project.account.BankAccount;
import project.account.Transaction;
import project.customer.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Storage {
    private final FileManager fileManager;
    private final String CUSTOMERS_FILE = "src/project/data/customers.txt";
    private final String ACCOUNTS_FILE = "src/project/data/accounts.txt";

    private final String TRANSACTIONS_FILE = "src/project/data/transactions.txt";

    public Storage() {
        this.fileManager = new FileManager();
    }

    public ArrayList<Customer> getCustomers() {
        return fileManager.readObjects(CUSTOMERS_FILE);
    }

    public void saveCustomer(Customer newCustomer) {
        ArrayList<Customer> customers = getCustomers();
        customers.add(newCustomer);
        fileManager.writeObjects(CUSTOMERS_FILE, customers);
    }

    public void updateCustomer(Customer updatedCustomer) {
        ArrayList<Customer> customers = getCustomers();
        for(int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);

            if(customer.getId().equals(updatedCustomer.getId())) {
                customers.set(i, updatedCustomer);
                fileManager.writeObjects(CUSTOMERS_FILE, customers);
                return;
            }
        }
    }

    public Customer getCustomerByAccountId(UUID accountId) {
        List<BankAccount> accounts = getAccounts();

        for (BankAccount account : accounts) {
            if (account.getAccountId().equals(accountId)) {
                UUID customerId = account.getCustomerId();
                return getCustomerById(customerId);
            }
        }
        return null;
    }

    public Customer getCustomerById(UUID customerId) {
        List<Customer> customers = getCustomers();

        for (Customer customer : customers) {
            if (customer.getId().equals(customerId)) {
                return customer;
            }
        }

        return null;
    }
    public Customer getCustomerBySocialNumber(String socialNumber) {
        var customers = getCustomers();
        for(Customer customer : customers) {
            if(customer.getSocialNumber().equals(socialNumber)) {
                return customer;
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

    public ArrayList<BankAccount> getAccountsByCustomerId(UUID customerId) {
        ArrayList<BankAccount> accounts = new ArrayList<>();
        for(BankAccount account : getAccounts() ) {
            if(account.getCustomerId().equals(customerId)) {
                accounts.add(account);
            }
        }
        return accounts;
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
        ArrayList<Transaction> accountTransactions = new ArrayList<>();

        for(Transaction transaction : transactions) {
            UUID senderAccountId = transaction.senderAccountId();
            UUID recipientAccountId =  transaction.recipientAccountId();

            if(senderAccountId.equals(accountId) || (recipientAccountId != null && recipientAccountId.equals(accountId))) {
                accountTransactions.add(transaction);
            }
        }
        return accountTransactions;
    }
}
