package ca.ulaval.glo4003.trotti.application.user.dto;

public class UserDTO {
    private final String email;
    private final String name;
    private final String dateOfBirth;
    private final String gender;
    private final String idul;
    private final boolean activated;
    private final int age;

    public UserDTO(String email, String name, String dateOfBirth, String gender,
                   String idul, boolean activated, int age) {
        this.email = email;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.idul = idul;
        this.activated = activated;
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getIdul() {
        return idul;
    }

    public boolean isActivated() {
        return activated;
    }

    public int getAge() {
        return age;
    }
}
