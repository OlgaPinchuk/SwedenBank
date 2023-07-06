package project.auth;

import project.user.User;
import project.utils.ObjectFactory;

import java.security.NoSuchAlgorithmException;

public class UserAuthenticator {
    private Validator validator;
    public UserAuthenticator() {
       this.validator = ObjectFactory.getValidator();
    }
    public boolean logIn(User user, String password) throws NoSuchAlgorithmException {
        return validator.validatePassword(user.getPassword(), password);
    }
}
