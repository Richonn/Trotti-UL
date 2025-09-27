package ca.ulaval.glo4003.trotti.user.service;

import ca.ulaval.glo4003.trotti.user.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class UserService {
    private static final Map<String, User> users = new ConcurrentHashMap<>();

    private static final Pattern PASSWORD_PATERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$"
    );

    public void register(User user) {
        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }
        if (user.getEmail() == null) {
            throw new RuntimeException("User email cannot be null");
        }
        if (user.getPassword() == null) {
            throw new RuntimeException("User password cannot be null");
        }
        if (!PASSWORD_PATERN.matcher(user.getPassword()).matches()) {
            throw new RuntimeException("Invalid password");
        }
        users.put(user.getEmail(), user);
    }

    public User login(String email, String password) {
        User user = users.get(email);
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid email or password");
        }
        return user;
    }

    public User findByEmail(String email) {
        return users.get(email);
    }
}
