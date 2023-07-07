package project.auth;

import project.customer.Customer;
import project.utils.ObjectFactory;

import java.security.NoSuchAlgorithmException;

public class CustomerAuthenticator {
    private final Validator validator;
    public CustomerAuthenticator() {
       this.validator = ObjectFactory.getValidator();
    }
    public boolean logIn(Customer customer, String password) throws NoSuchAlgorithmException {
        return validator.validatePassword(customer.getPassword(), password);
    }
}
