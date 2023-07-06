package project.auth;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

public class Validator {

    public boolean validateSocialNumber(String socialNumber) {
        String SOCIAL_NUMBER_PATTERN = "\\d{4}-[a-zA-Z]{3}";
        return socialNumber.matches(SOCIAL_NUMBER_PATTERN);
    }

    public boolean validateUserAge(String socialNumber) {
        int MINIMUM_AGE = 18;

        int birthYear = extractBirthYear(socialNumber);
        int currentYear = LocalDate.now().getYear();
        int age = currentYear - birthYear;

        return age >= MINIMUM_AGE;
    }

    public boolean validatePassword(String storedPassword, String userInputPassword) throws NoSuchAlgorithmException {
        String hashedPassword = PasswordHasher.hashPassword(userInputPassword);
        return storedPassword.equals(hashedPassword);
    }

    private static int extractBirthYear(String socialNumber) {
        String year = socialNumber.substring(0, 4);
        return Integer.parseInt(year);
    }
}
