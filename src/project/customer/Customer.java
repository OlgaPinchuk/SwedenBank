package project.customer;

import project.account.BankAccount;
import project.utils.ObjectFactory;
import project.utils.Storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Customer implements Serializable {
    private UUID id;
    private String fullName;
    private String socialNumber;
    private String password;
    private ArrayList<BankAccount> accounts;
    private transient Storage storage;
   private static final long serialVersionUID = 6093496094500689635L;


    public Customer(String fullName, String socialNumber, String password) {
        this.storage = ObjectFactory.getStorage();

        this.id = UUID.randomUUID();
        this.fullName = fullName;
        this.socialNumber = socialNumber;
        this.password = password;

        createDefaultBankAccount();

        this.accounts = storage.getAccountsByCustomerId(id);
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSocialNumber() {
        return socialNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<BankAccount> getAccounts() {
        return accounts;
    }

   private void createDefaultBankAccount() {
        BankAccount newAccount = new BankAccount(id);
        storage.saveAccount(newAccount);
    }

}

