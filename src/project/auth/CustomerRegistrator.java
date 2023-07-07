package project.auth;

import project.customer.Customer;
import project.utils.ConsoleMessage;
import project.utils.Storage;
import project.utils.ObjectFactory;

import java.security.NoSuchAlgorithmException;


public class CustomerRegistrator {
    private Storage storage;
    private Validator validator;

    public CustomerRegistrator() {
        this.storage = ObjectFactory.getStorage();
        this.validator = ObjectFactory.getValidator();
    }

    public Customer register(String fullName, String socialNumber, String password) throws NoSuchAlgorithmException {
        if(!validator.validateSocialNumber(socialNumber) || !validator.validateUserAge(socialNumber)) {
            registrationFailedMessage();
            invalidSocialNumberWarning();
            return null;
        }

        Customer customer = storage.getCustomerBySocialNumber(socialNumber);

        if(customer != null) {
            registrationFailedMessage();
            customerExistWarning();
            return null;
         }

        String hashedPassword = PasswordHasher.hashPassword(password);
        Customer newCustomer = new Customer(fullName, socialNumber, hashedPassword);
        storage.saveCustomer(newCustomer);

        return newCustomer;
    }

    private void registrationFailedMessage() {
        ConsoleMessage.showErrorMessage("Registration failed!");
    }

    private void invalidSocialNumberWarning() {
        ConsoleMessage.showErrorMessage("Invalid social number or age.");
    }
    private void customerExistWarning() {
        ConsoleMessage.showErrorMessage("Customer with this social number already exist.");
    }

}
