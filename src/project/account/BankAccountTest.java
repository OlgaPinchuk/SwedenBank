package project.account;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    @Test
    void testGenerateAccountNumber() {
        BankAccount account = new BankAccount(UUID.randomUUID());
        String accountNumber = account.getAccountNumber();
        assertNotNull(accountNumber);
        assertEquals(6, accountNumber.length());
        assertTrue(accountNumber.matches("\\d{6}"));

    }

    @Test
    void testGenerateCheckSum() {
        String accountFirstPart = "1233";
        int checksum = BankAccount.generateCheckSum(accountFirstPart);
        assertEquals(9, checksum);
    }
}