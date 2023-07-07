package project.account;

import java.io.Serializable;

public enum TransactionType implements Serializable {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER;
}
