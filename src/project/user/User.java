package project.user;

import project.account.BankAccount;
import project.utils.ObjectFactory;
import project.utils.Storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class User implements Serializable {
    private transient Storage storage;
    private UUID id;
    private String fullName;
    private String socialNumber;
    private String password;
    private ArrayList<BankAccount> accounts;
   private static final long serialVersionUID = 6093496094500689635L;


    public User(String fullName, String socialNumber, String password) {
        this.storage = ObjectFactory.getStorage();

        this.id = UUID.randomUUID();
        this.fullName = fullName;
        this.socialNumber = socialNumber;
        this.password = password;

        createDefaultBankAccount();

        this.accounts = storage.getAccountsByUserId(id);
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

    public ArrayList<BankAccount> getAccounts() {
        return accounts;
    }

   private void createDefaultBankAccount() {
        BankAccount newAccount = new BankAccount(id);
        storage.saveAccount(newAccount);
    }

}

