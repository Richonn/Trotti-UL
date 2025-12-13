package ca.ulaval.glo4003.trotti.domain.user;

import ca.ulaval.glo4003.trotti.domain.pass.Money;
import ca.ulaval.glo4003.trotti.domain.pass.Pass;
import ca.ulaval.glo4003.trotti.domain.pass.PaymentInfo;
import ca.ulaval.glo4003.trotti.domain.pass.PricingPolicy;
import ca.ulaval.glo4003.trotti.domain.pass.Session;
import ca.ulaval.glo4003.trotti.domain.pass.exception.NoActivePassException;
import ca.ulaval.glo4003.trotti.domain.ride.Ride;
import ca.ulaval.glo4003.trotti.domain.ride.RideCode;
import ca.ulaval.glo4003.trotti.domain.ride.RideCodeGenerator;
import ca.ulaval.glo4003.trotti.domain.ride.email.RideCodeEmail;
import ca.ulaval.glo4003.trotti.domain.ride.exception.ExpiredRideCodeException;
import ca.ulaval.glo4003.trotti.domain.ride.exception.InvalidRideCodeException;
import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.shared.Clock;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.user.email.ActivationEmail;
import ca.ulaval.glo4003.trotti.domain.user.exception.AccountAlreadyActivatedException;
import ca.ulaval.glo4003.trotti.domain.user.exception.AccountNotActivatedException;
import ca.ulaval.glo4003.trotti.domain.user.exception.UnpaidInvoiceException;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private final Role role;
    private boolean activated;
    private PaymentInfo savedPaymentInfo;
    private MonthlyBalance monthlyBalance;
    private final List<Pass> passes;
    private boolean autoPay;
    private RideCode currentRideCode;

    User(String id, Email email, Password password, String name, LocalDate dateOfBirth, Gender gender, Idul idul, Role role) {
        this.id = Objects.requireNonNull(id, "User ID cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.dateOfBirth = Objects.requireNonNull(dateOfBirth, "Date of birth cannot be null");
        this.gender = Objects.requireNonNull(gender, "Gender cannot be null");
        this.idul = Objects.requireNonNull(idul, "IDUL cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.activated = false;
        this.monthlyBalance = new MonthlyBalance();
        this.passes = new ArrayList<>();
        this.autoPay = false;
        this.currentRideCode = null;
    }

    static User create(String id, Email email, Password password, String name, LocalDate dateOfBirth, Gender gender, Idul idul, Role role) {
        return new User(id, email, password, name, dateOfBirth, gender, idul, role);
    }

    public static User register(Email email, Password password, String name, String dateOfBirth, String gender, String idul, Role role) {
        UserFactory factory = new UserFactory();
        return factory.createForRegistration(email, password, name, dateOfBirth, gender, idul, role);
    }

    public void activate(EmailService emailService) {
        if (activated) {
            throw new AccountAlreadyActivatedException();
        }
        this.activated = true;

        ActivationEmail email = new ActivationEmail(this.email.getValue(), this.name);
        emailService.send(email);
    }

    public boolean canLogin(String rawPassword) {
        return activated && password.matches(rawPassword);
    }

    public int getAge(Clock clock) {
        return Period.between(dateOfBirth, clock.today()).getYears();
    }

    public int getDailyQuota(Pass activePass) {
        int baseQuota = 30;
        return activePass != null ? baseQuota + activePass.getExtraMinutesPerDay() : baseQuota;
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

    public Role getRole() {
        return role;
    }

    public boolean isActivated() {
        return activated;
    }

    public boolean isAutoPayEnabled() {
        return autoPay;
    }
    public void setAutoPayEnabled(boolean autoPayEnabled) {
        this.autoPay = autoPayEnabled;
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

    public void addToMonthlyBalance(Money amount, LocalDate currentDate) {
        this.monthlyBalance = this.monthlyBalance.add(amount, currentDate);
    }

    public MonthlyBalance getMonthlyBalance() {
        return monthlyBalance;
    }

    public void resetMonthlyBalance() {
        this.monthlyBalance = new MonthlyBalance(Money.zero());
    }

    public boolean hasOverduePayment(Clock clock) {
        return monthlyBalance.isOverdue(clock.today(), 10);
    }

    public List<Pass> getPasses() {
        return Collections.unmodifiableList(passes);
    }

    public void addPass(Pass pass) {
        this.passes.add(pass);
    }

    public void removePass(Pass pass) {
        this.passes.remove(pass);
    }

    public Pass purchasePass(Session session, int extraMinutesPerDay, boolean monthlyBilling, ca.ulaval.glo4003.trotti.domain.pass.PricingStrategy pricingStrategy, ca.ulaval.glo4003.trotti.domain.pass.PassFactory passFactory) {
        Pass pass = passFactory.createForPurchase(this.id, session, extraMinutesPerDay, monthlyBilling, pricingStrategy);
        this.passes.add(pass);
        return pass;
    }

    public boolean hasActivePass(LocalDate date) {
        return passes.stream()
                .anyMatch(pass -> pass.isActiveOn(date));
    }

    public boolean hasActivePass(List<Pass> passes, LocalDate date) {
        return passes.stream()
                .filter(pass -> pass.getUserId().equals(this.id))
                .anyMatch(pass -> pass.isActiveOn(date));
    }

    public RideCode requestRideCode(Clock clock, RideCodeGenerator rideCodeGenerator, EmailService emailService) {
        if (!this.activated) {
            throw new AccountNotActivatedException();
        }

        if (!hasActivePass(clock.today()) && this.role != Role.TECHNICIAN) {
            throw new NoActivePassException();
        }

        if (hasOverduePayment(clock) && this.role != Role.TECHNICIAN) {
            throw new UnpaidInvoiceException();
        }

        this.currentRideCode = rideCodeGenerator.generate();

        RideCodeEmail email = new RideCodeEmail(this.email.getValue(), this.name, this.currentRideCode);
        emailService.send(email);

        return this.currentRideCode;
    }

    public Optional<RideCode> getCurrentRideCode() {
        return Optional.ofNullable(currentRideCode);
    }

    public void setCurrentRideCode(RideCode rideCode) {
        this.currentRideCode = rideCode;
    }

    public Ride startRide(Clock clock, String inputCode, Station station, int slotNumber) {
        if (!this.activated) {
            throw new AccountNotActivatedException();
        }

        if (!hasActivePass(clock.today()) && this.role != Role.TECHNICIAN) {
            throw new NoActivePassException();
        }

        if (hasOverduePayment(clock) && this.role != Role.TECHNICIAN) {
            throw new UnpaidInvoiceException();
        }

        if (this.currentRideCode == null || !this.currentRideCode.matches(inputCode)) {
            throw new InvalidRideCodeException();
        }
        if (this.currentRideCode.isExpired(clock)) {
            throw new ExpiredRideCodeException();
        }

        Scooter scooter = station.rentScooter(slotNumber, this.id);

        return Ride.start(this.id, scooter.getId(), station.getId(), clock.now());
    }

    public Pass getActivePass(LocalDate date) {
        return passes.stream()
                .filter(pass -> pass.isActiveOn(date))
                .findFirst()
                .orElse(null);
    }

    public RideBillingResult calculateRideBilling(java.time.Duration rideDuration, int dailyUsage, Clock clock, ca.ulaval.glo4003.trotti.domain.ride.RideFeeCalculator feeCalculator) {
        long rideMinutes = rideDuration.toMinutes();
        Pass activePass = getActivePass(clock.today());
        int dailyQuota = getDailyQuota(activePass);

        Money additionalFees = Money.zero();
        boolean chargedImmediately = false;

        if (rideMinutes <= 15) {
            int newUsedToday = dailyUsage + (int) rideMinutes;
            if (newUsedToday > dailyQuota) {
                additionalFees = feeCalculator.getQuotaExceededFee();
            }
        } else {
            additionalFees = feeCalculator.calculateAdditionalFees(rideDuration);
        }

        if (additionalFees.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            if (activePass != null && activePass.isMonthlyBilling()) {
                addToMonthlyBalance(additionalFees, clock.today());
                chargedImmediately = false;
            } else {
                chargedImmediately = true;
            }
        }

        return new RideBillingResult(additionalFees, chargedImmediately, getMonthlyBalance().getAmount());
    }
}
