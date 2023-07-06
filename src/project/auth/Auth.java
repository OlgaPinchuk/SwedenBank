package project.auth;

import project.user.User;

import java.util.List;

public abstract class Auth {

    public Auth() {} // check
    public User findUser(List<User> users, String socialNumber) {
        for(User user : users) {
            if(user.getSocialNumber().equals(socialNumber)) {
                return user;
            }
        }
        return null;
    }

}
