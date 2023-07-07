package project.ui;

import org.junit.jupiter.api.Test;
import project.account.BankAccount;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMenuTest {

    @Test
    void testWithdrawMoney() {
        BankAccount customerAccount = new BankAccount(UUID.randomUUID());
        BigDecimal initialBalance = new BigDecimal("1500");
        BigDecimal withdrawalAmount = new BigDecimal("300");
        BigDecimal expectedBalance = initialBalance.subtract(withdrawalAmount);
        customerAccount.setBalance(expectedBalance);

        assertEquals(expectedBalance, customerAccount.getBalance());
    }

    @Test
    void testDepositMoney() {
        BankAccount customerAccount = new BankAccount(UUID.randomUUID());
        BigDecimal initialBalance = new BigDecimal("1500");
        BigDecimal depositAmount = new BigDecimal("700");
        BigDecimal expectedBalance = initialBalance.add(depositAmount);
        customerAccount.setBalance(expectedBalance);

        assertEquals(expectedBalance, customerAccount.getBalance());
    }
}