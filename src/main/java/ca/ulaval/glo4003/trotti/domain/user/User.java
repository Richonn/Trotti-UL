package ca.ulaval.glo4003.trotti.domain.user;

import ca.ulaval.glo4003.trotti.domain.pass.Money;
import ca.ulaval.glo4003.trotti.domain.pass.PaymentInfo;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.user.exception.AccountAlreadyActivatedException;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class User {
    private final String id;
    private final Email email;
    private final Password password;
    private final String name;
    private final LocalDate dateOfBirth;
    private final Gender gender;
    private final Idul idul;
    private boolean activated;
    private PaymentInfo savedPaymentInfo;
    private MonthlyBalance monthlyBalance;

    private User(String id, Email email, Password password, String name, LocalDate dateOfBirth, Gender gender, Idul idul) {
        this.id = Objects.requireNonNull(id, "User ID cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.dateOfBirth = Objects.requireNonNull(dateOfBirth, "Date of birth cannot be null");
        this.gender = Objects.requireNonNull(gender, "Gender cannot be null");
        this.idul = Objects.requireNonNull(idul, "IDUL cannot be null");
        this.activated = false;
        this.monthlyBalance = new MonthlyBalance();
    }

    public static User register(Email email, Password password, String name, String dateOfBirth, String gender, String idul) {
        return new User(
                UUID.randomUUID().toString(),
                email,
                password,
                name,
                LocalDate.parse(dateOfBirth),
                Gender.fromString(gender),
                new Idul(idul)
        );
    }

    public void activate() {
        if (activated) {
            throw new AccountAlreadyActivatedException();
        }
        this.activated = true;
    }

    public boolean canLogin(String rawPassword) {
        return activated && password.matches(rawPassword);
    }

    public int getAge(Clock clock) {
        return Period.between(dateOfBirth, clock.today()).getYears();
    }

    public String getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public Idul getIdul() {
        return idul;
    }

    public boolean isActivated() {
        return activated;
    }

    public void savePaymentInfo(PaymentInfo paymentInfo) {
        this.savedPaymentInfo = paymentInfo;
    }

    public Optional<PaymentInfo> getSavedPaymentInfo() {
        return Optional.ofNullable(savedPaymentInfo);
    }

    public void addToMonthlyBalance(Money amount) {
        this.monthlyBalance = this.monthlyBalance.add(amount);
    }

    public MonthlyBalance getMonthlyBalance() {
        return monthlyBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
