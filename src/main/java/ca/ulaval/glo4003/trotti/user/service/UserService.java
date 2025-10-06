package ca.ulaval.glo4003.trotti.user.service;

import ca.ulaval.glo4003.trotti.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class UserService {
    private static final Map<String, User> users = new ConcurrentHashMap<>();

    private static final Pattern PASSWORD_PATERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$"
    );

    private Boolean verifyEmailAlreadyUsed(String email) {
        for  (User user : users.values()) {
            if (user.getEmail().equals(email)) return true;
        }
        return false;
    }

    public void register(User user) {
        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }

        List<String> missingFields = new ArrayList<>();

        if (user.getGender() == null) missingFields.add("gender");
        if (user.getDateOfBirth() == null) missingFields.add("date of birth");
        if (user.getIdul() == null) missingFields.add("idul");
        if (user.getAge() == null) missingFields.add("age");
        if (user.getName() == null) missingFields.add("name");
        if (user.getEmail() == null) {
            missingFields.add("email");
        } else if (verifyEmailAlreadyUsed(user.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (user.getPassword() == null) {
            missingFields.add("password");
        } else if (!PASSWORD_PATERN.matcher(user.getPassword()).matches()) {
            throw new RuntimeException("Password must have at least 10 characters including uppercases, number and special characters");
        }

        if (!missingFields.isEmpty()) {
            throw new RuntimeException("Missing or invalid fields: " + String.join(", ", missingFields));
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

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User findByEmail(String email) {
        return users.get(email);
    }

    public void clearAllUsers() {
        users.clear();
    }
}
