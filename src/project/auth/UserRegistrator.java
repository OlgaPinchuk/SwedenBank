package project.auth;

import project.user.User;
import project.utils.ConsoleMessage;
import project.utils.Storage;
import project.utils.ObjectFactory;

import java.security.NoSuchAlgorithmException;


public class UserRegistrator {
    private Storage storage;
    private Validator validator;

    public UserRegistrator() {
        this.storage = ObjectFactory.getStorage();
        this.validator = ObjectFactory.getValidator();
    }

    public User register(String fullName, String socialNumber, String password) throws NoSuchAlgorithmException {
        if(!validator.validateSocialNumber(socialNumber) || !validator.validateUserAge(socialNumber)) {
            registrationFailedMessage();
            invalidSocialNumberWarning();
            return null;
        }

        User user = storage.getUserBySocialNumber(socialNumber);

        if(user != null) {
            registrationFailedMessage();
            userExistWarning();
            return null;
         }

        String hashedPassword = PasswordHasher.hashPassword(password);
        User newUser = new User(fullName, socialNumber, hashedPassword);
        storage.saveUser(newUser);

        return newUser;
    }

    private void registrationFailedMessage() {
        ConsoleMessage.showErrorMessage("Registration failed!");
    }

    private void invalidSocialNumberWarning() {
        ConsoleMessage.showErrorMessage("Invalid social number or age.");
    }
    private void userExistWarning() {
        ConsoleMessage.showErrorMessage("User with this social number already exist.");
    }

}
