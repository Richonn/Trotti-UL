package ca.ulaval.glo4003.trotti.interfaces.rest.user.model;

import java.time.LocalDate;
import java.time.Period;

public class User {
    private String name;
    private String dateOfBirth;
    private String gender;
    private String idul;
    private String email;
    private String password;
    private boolean isActivated = false;

    public User() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getIdul() { return idul; }
    public void setIdul(String idul) { this.idul = idul; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isActivated() { return isActivated; }
    public void activate() { this.isActivated = true; }

    public boolean activateAccount() {
        if (isActivated) return false;
        isActivated = true;
        return true;
    }
}
