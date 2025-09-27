package ca.ulaval.glo4003.trotti.user.dto;

import ca.ulaval.glo4003.trotti.user.model.User;

public class RegisterResponse {
    private String name;
    private String dateOfBirth;
    private String gender;
    private Integer age;
    private String idul;
    private String email;

    public RegisterResponse(User user) {
        this.name = user.getName();
        this.dateOfBirth = user.getDateOfBirth();
        this.gender = user.getGender();
        this.age = user.getAge();
        this.idul = user.getIdul();
        this.email = user.getEmail();
    }

    public String getName() { return name; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public Integer getAge() { return age; }
    public String getIdul() { return idul; }
    public String getEmail() { return email; }
}