package project.ui;

import project.auth.*;
import project.customer.Customer;
import project.utils.ConsoleMessage;
import project.utils.ObjectFactory;
import project.utils.Storage;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.io.Console;

public class HomeMenu extends Menu {
    private final String header = "Welcome to the SwedenBank App!";
    private final static String instruction = "Please select an option: ";
    private final static List<String> options = Arrays.asList("Log In", "Register", "Exit");
    private final String FULL_NAME_REQUEST = "Full name (or type 'exit' to return): ";
    private final String SOCIAL_NUMBER_REQUEST = "Social security number (in format 'yyyy-abc') (or type 'exit' to return): ";
    public HomeMenu() {
        super(instruction, options);
    }

    public void run() {
        showMenu(header);
    }

    @Override
    public void handleChoice() {
        String input = scanner.nextLine();
        try {
            int selectedOption = Integer.parseInt(input.trim());
            switch (selectedOption) {
                case 1 -> {
                    Customer currentCustomer = loginCustomer();
                        if (currentCustomer != null) {
                            runCustomerMenu(currentCustomer);
                        }
                    }
                    case 2 -> {
                        Customer newCustomer = registerCustomer();

                        if (newCustomer != null) {
                            runCustomerMenu(newCustomer);
                        }
                    }
                    case 3 -> exit();
                    default -> showInvalidOptionMessage();
                }
        }
        catch (NumberFormatException exception) {
            showInvalidOptionMessage();
        } catch (NoSuchAlgorithmException e) {
            ConsoleMessage.showErrorMessage("Hashing password is failed.");
            throw new RuntimeException(e);
        }
        handleChoice();
    }

    private void runCustomerMenu(Customer customer) {
        CustomerMenu customerMenu = ObjectFactory.getCustomerMenu(customer);
        customerMenu.run();
    }

    private Customer registerCustomer() throws NoSuchAlgorithmException {
        CustomerRegistrator registrator = ObjectFactory.getCustomerRegistrator();
        Customer newCustomer;

        while (true) {
            String fullName = getInput(FULL_NAME_REQUEST);
            if (shouldReturnToMenu(fullName)) {
                returnToMenu();
                return null;
            }

            String socialNumber = getInput(SOCIAL_NUMBER_REQUEST);
            if (shouldReturnToMenu(socialNumber)) {
                returnToMenu();
                return null;
            }

            String password = getCustomerPassword();
            if (shouldReturnToMenu(password)) {
                returnToMenu();
                return null;
            }

            if (fullName.isEmpty() || socialNumber.isEmpty() || password.isEmpty()) {
                showInvalidInputMessage();
                continue;
            }

            newCustomer = registrator.register(fullName, socialNumber, password);

            if (newCustomer != null) {
                ConsoleMessage.showSuccessMessage("New customer was registered successfully.");
                break;
            }
        }

        return newCustomer;
    }

    private Customer loginCustomer() {
        Storage storage = ObjectFactory.getStorage();
        CustomerAuthenticator authenticator = ObjectFactory.getCustomerAuthenticator();
        Customer customer;

        while(true) {
            String socialNumber = getInput(SOCIAL_NUMBER_REQUEST);
            if (shouldReturnToMenu(socialNumber)) {
                returnToMenu();
                return null;
            }

            String password = getCustomerPassword();
            if (shouldReturnToMenu(password)) {
                returnToMenu();
                return null;
            }

            if (socialNumber.isEmpty() || password.isEmpty()) {
                showInvalidInputMessage();
                continue;
            }

            customer = storage.getCustomerBySocialNumber(socialNumber);

            if (customer == null) {
                ConsoleMessage.showErrorMessage("Customer not found. Please check your social number and try again.");
                continue;
            }

            boolean isLoggedIn;
            try {
                isLoggedIn = authenticator.logIn(customer, password);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            if (isLoggedIn) {
                ConsoleMessage.showSuccessMessage("Login successful! Welcome!");
                return customer;
            }
            else {
                ConsoleMessage.showErrorMessage("Invalid password. Please try again.");
            }
        }
    }

    private String getCustomerPassword() {
       String request = "Password (or type 'exit' to return): ";
        Console console = System.console();
        if (console != null) {
            System.out.print(request);
            char[] passwordChars = console.readPassword();
            return new String(passwordChars);
        } else {
            return getInput(request);
        }
    }

}