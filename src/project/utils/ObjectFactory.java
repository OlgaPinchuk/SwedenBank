package project.utils;

import project.auth.*;
import project.ui.CustomerMenu;
import project.user.User;

public class ObjectFactory {

    private ObjectFactory() {}
    public static UserAuthenticator createUserAuthenticator() {
        return new UserAuthenticator();
    }
    public static UserRegistrator createUserRegistrator() {
        return new UserRegistrator();
    }

    public static Storage getStorage() {
        return new Storage();
    }

    public static Validator getValidator() {
        return new Validator();
    }

    public static CustomerMenu getUserMenu(User user) {
        return new CustomerMenu(user);
    }
}
