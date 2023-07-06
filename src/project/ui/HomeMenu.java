package project.ui;

import project.auth.*;
import project.user.User;
import project.utils.ConsoleMessage;
import project.utils.ObjectFactory;
import project.utils.Storage;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.Console;

public class HomeMenu extends Menu {
    private final String header = "Welcome to the Bank App!";
    private final static String instruction = "Please select an option:";
    private final static List<String> options = Arrays.asList("Log In", "Register", "Exit");

    private final String FULLNAME_REQUEST = "Enter your full name (or type 'exit' to return to the previous menu): ";
    private final String SOCIAL_NUMBER_REQUEST = "Enter your social security number in the following format 'yyyy-abc' (or type 'exit' to return to the previous menu): ";
    private final String PASSWORD_REQUEST = "Enter your password (or type 'exit' to return to the previous menu): ";


    private Scanner scanner;

    public HomeMenu() {
        super(instruction, options);
        this.scanner = new Scanner(System.in);
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
        }
        handleUserChoice();
    }

    private void runCustomerMenu(User user) {
        CustomerMenu customerMenu = ObjectFactory.getUserMenu(user);
        customerMenu.run();
    }

    private User registerUser() {
        UserRegistrator userRegistrator = new UserRegistrator();
        User newUser;

        while (true) {
            String fullName = getUserInput(FULLNAME_REQUEST);
            if (shouldReturnToMenu(fullName)) {
                returnToMenu();
                return null;
            }

            String socialNumber = getUserInput(SOCIAL_NUMBER_REQUEST);
            if (shouldReturnToMenu(socialNumber)) {
                returnToMenu();
                return null;
            }

            String password = getUserPassword(PASSWORD_REQUEST);
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
        UserAuthenticator userAuth = new UserAuthenticator();
        User user;

        while(true) {
            String socialNumber = getUserInput(SOCIAL_NUMBER_REQUEST);
            if (shouldReturnToMenu(socialNumber)) {
                returnToMenu();
                return null;
            }

            String password = getUserPassword(PASSWORD_REQUEST);
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
                userNotFoundWarning();
                continue;
            }

            boolean isLoggedIn = userAuth.logIn(user, password);

            if (isLoggedIn) {
                loginSuccessMessage();
                return user;
            }
            else {
                invalidPasswordWarning();
            }
        }
    }

    private String getUserPassword(String message) {
        Console console = System.console();
        if (console != null) {
            System.out.print(message);
            char[] passwordChars = console.readPassword();
            return new String(passwordChars);
        } else {
            return getUserInput(message);
        }
    }

    private void invalidPasswordWarning() {
       ConsoleMessage.showErrorMessage("Invalid password. Please try again.");
    }

    private void userNotFoundWarning() {
       ConsoleMessage.showErrorMessage("User not found. Please check your social number and try again.");
    }
    private void loginSuccessMessage() {
        ConsoleMessage.showSuccessMessage("Login successful! Welcome!");
    }

    private void registerSuccessMessage() {
        ConsoleMessage.showSuccessMessage("User registered successfully.");
    }
}