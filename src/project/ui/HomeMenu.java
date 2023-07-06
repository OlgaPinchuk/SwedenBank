package project.ui;

import project.auth.*;
import project.user.User;
import project.utils.ConsoleMessage;
import project.utils.ObjectFactory;
import project.utils.Storage;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.io.Console;

public class HomeMenu extends Menu {
    private final String header = "Welcome to the Bank App!";
    private final static String instruction = "Please select an option:";
    private final static List<String> options = Arrays.asList("Log In", "Register", "Exit");
    private final String FULL_NAME_REQUEST = "Enter your full name (or type 'exit' to return to the previous menu): ";
    private final String SOCIAL_NUMBER_REQUEST = "Enter your social security number in the following format 'yyyy-abc' (or type 'exit' to return to the previous menu): ";
    public HomeMenu() {
        super(instruction, options);
    }

    public void run() {
        showMenu();
    }

    @Override
    protected String getHeader() {
        return header;
    }
    @Override
    public void handleUserChoice() {
        String input = scanner.nextLine();
        try {
            int selectedOption = Integer.parseInt(input.trim());
            switch (selectedOption) {
                case 1 -> {
                    User currentUser = loginUser();
                        if (currentUser != null) {
                            runCustomerMenu(currentUser);
                        }
                    }
                    case 2 -> {
                        User newUser = registerUser();

                        if (newUser != null) {
                            runCustomerMenu(newUser);
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
        handleUserChoice();
    }

    private void runCustomerMenu(User user) {
        CustomerMenu customerMenu = ObjectFactory.getUserMenu(user);
        customerMenu.run();
    }

    private User registerUser() throws NoSuchAlgorithmException {
        UserRegistrator userRegistrator = ObjectFactory.createUserRegistrator();
        User newUser;

        while (true) {
            String fullName = getUserInput(FULL_NAME_REQUEST);
            if (shouldReturnToMenu(fullName)) {
                returnToMenu();
                return null;
            }

            String socialNumber = getUserInput(SOCIAL_NUMBER_REQUEST);
            if (shouldReturnToMenu(socialNumber)) {
                returnToMenu();
                return null;
            }

            String password = getUserPassword();
            if (shouldReturnToMenu(password)) {
                returnToMenu();
                return null;
            }

            if (fullName.isEmpty() || socialNumber.isEmpty() || password.isEmpty()) {
                showInvalidInputMessage();
                continue;
            }

            newUser = userRegistrator.register(fullName, socialNumber, password);

            if (newUser != null) {
                ConsoleMessage.showSuccessMessage("User registered successfully.");
                break;
            }
        }

        return newUser;
    }

    private User loginUser() {
        Storage storage = ObjectFactory.getStorage();
        UserAuthenticator userAuth = ObjectFactory.createUserAuthenticator();
        User user;

        while(true) {
            String socialNumber = getUserInput(SOCIAL_NUMBER_REQUEST);
            if (shouldReturnToMenu(socialNumber)) {
                returnToMenu();
                return null;
            }

            String password = getUserPassword();
            if (shouldReturnToMenu(password)) {
                returnToMenu();
                return null;
            }

            if (socialNumber.isEmpty() || password.isEmpty()) {
                showInvalidInputMessage();
                continue;
            }

            user = storage.getUserBySocialNumber(socialNumber);

            if (user == null) {
                ConsoleMessage.showErrorMessage("User not found. Please check your social number and try again.");
                continue;
            }

            boolean isLoggedIn;
            try {
                isLoggedIn = userAuth.logIn(user, password);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            if (isLoggedIn) {
                ConsoleMessage.showSuccessMessage("Login successful! Welcome!");
                return user;
            }
            else {
                ConsoleMessage.showErrorMessage("Invalid password. Please try again.");
            }
        }
    }

    private String getUserPassword() {
       String request = "Enter your password (or type 'exit' to return to the previous menu):";
        Console console = System.console();
        if (console != null) {
            System.out.print(request);
            char[] passwordChars = console.readPassword();
            return new String(passwordChars);
        } else {
            return getUserInput(request);
        }
    }

}