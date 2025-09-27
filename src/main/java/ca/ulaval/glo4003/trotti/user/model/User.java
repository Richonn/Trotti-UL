package ca.ulaval.glo4003.trotti.user.model;

public class User {
    private String name;
    private String dateOfBirth;
    private String gender;
    private Integer age;
    private String idul;
    private String email;
    private String password;

    public User() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getIdul() { return idul; }
    public void setIdul(String idul) { this.idul = idul; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
