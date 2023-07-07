package project.utils;

import project.auth.*;
import project.ui.CustomerMenu;
import project.customer.Customer;

public class ObjectFactory {

    private ObjectFactory() {}
    public static CustomerAuthenticator createCustomerAuthenticator() {
        return new CustomerAuthenticator();
    }
    public static CustomerRegistrator createCustomerRegistrator() {
        return new CustomerRegistrator();
    }

    public static Storage getStorage() {
        return new Storage();
    }

    public static Validator getValidator() {
        return new Validator();
    }

    public static CustomerMenu getCustomerMenu(Customer customer) {
        return new CustomerMenu(customer);
    }
}
