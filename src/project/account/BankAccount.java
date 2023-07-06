package project.account;

import project.utils.ConsoleMessage;
import project.utils.ObjectFactory;
import project.utils.Storage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.text.NumberFormat;

public class BankAccount implements Serializable {
    private String name;
    private UUID accountId;

    private String accountNumber;

    private UUID userId;
    private BigDecimal balance;

    private Currency currency;
    private List<Transaction> transactionsHistory;
    public static int ACCOUNT_NUMBER_LENGTH = 6;

    private static final long serialVersionUID = -2174361050351122055L;

    public BankAccount(UUID userId) {
        this.name = "Main Account";
        this.accountId = UUID.randomUUID();
        this.userId = userId;
        this.accountNumber = generateAccountNumber();
        this.balance = BigDecimal.ZERO;
        this.currency = Currency.getInstance("SEK");
        this.transactionsHistory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        if(balance.compareTo(BigDecimal.ZERO) < 0) {
            ConsoleMessage.showErrorMessage("Balance cannot be negative.");
        }
        else {
            this.balance = balance;
            Storage storage = ObjectFactory.getStorage();

            storage.updateAccount(this);
        }
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getFormattedBalance() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormat.setGroupingUsed(true);
        String formattedBalance = numberFormat.format(balance);

        return  formattedBalance + " " + currency.getSymbol();
    }

    public List<Transaction> getTransactionsHistory() {
        return transactionsHistory;
    }

    public void setTransactionsHistory(ArrayList<Transaction> transactions) {
        this.transactionsHistory = transactions;
    }


    private String generateAccountNumber() {
        int CHECKSUM_LENGTH = 2;

        StringBuilder accountNumber = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < ACCOUNT_NUMBER_LENGTH - CHECKSUM_LENGTH; i++) {
            int randomNumber = random.nextInt(8) + 1;
            accountNumber.append(randomNumber);
        }
        int checksum = generateCheckSum(accountNumber.toString());

        if(checksum < 10) {
            accountNumber.append(0).append(checksum);
        }
        accountNumber.append(checksum);

        return accountNumber.toString();
    }

    private int generateCheckSum(String accountNumber) {
        int sum = 0;
        for(char digit : accountNumber.toCharArray()) {
            sum += Character.getNumericValue(digit);
        }
        return sum;
    }
}