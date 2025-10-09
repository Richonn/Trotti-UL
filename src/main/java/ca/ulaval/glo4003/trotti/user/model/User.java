package ca.ulaval.glo4003.trotti.user.model;

import ca.ulaval.glo4003.trotti.pass.model.Pass;
import ca.ulaval.glo4003.trotti.pass.model.PaymentInfo;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String dateOfBirth;
    private String gender;
    private String idul;
    private String email;
    private String password;

    private PaymentInfo paymentInfo;
    private List<Pass> purchasedPasses = new ArrayList<>();

    public User() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Integer getAge() {
        if (dateOfBirth == null) return 0;
        LocalDate birthDate = LocalDate.parse(this.dateOfBirth);
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String getIdul() { return idul; }
    public void setIdul(String idul) { this.idul = idul; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public List<Pass> getPurchasedPasses() {
        return purchasedPasses;
    }

    public void addPass(Pass pass) {
        this.purchasedPasses.add(pass);
    }
}
