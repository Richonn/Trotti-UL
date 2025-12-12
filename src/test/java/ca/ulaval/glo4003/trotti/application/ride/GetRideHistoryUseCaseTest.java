package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.application.ride.dto.RideDTO;
import ca.ulaval.glo4003.trotti.domain.ride.Ride;
import ca.ulaval.glo4003.trotti.domain.ride.RideRepository;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Password;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRideHistoryUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RideRepository rideRepository;

    private GetRideHistoryUseCase useCase;
    private String userEmail;
    private User user;

    @BeforeEach
    void setUp() {
        useCase = new GetRideHistoryUseCase(userRepository, rideRepository);
        userEmail = "test@example.com";
        user = User.register(
                new Email("test@example.com"),
                new Password("ValidPass123!"),
                "Test User",
                "2000-01-01",
                "H",
                "AAAA00000001",
                Role.USER
        );
    }

    @Test
    void givenValidUser_whenExecute_thenReturnsRideHistory() {
        Ride ride1 = Ride.start(user.getId(), "scooter-1", "VACHON", LocalDateTime.now());
        Ride ride2 = Ride.start(user.getId(), "scooter-2", "PEPS", LocalDateTime.now());
        List<Ride> expectedRides = Arrays.asList(ride1, ride2);

        Email email = new Email(userEmail);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(rideRepository.findCompletedRidesByUserId(user.getId())).thenReturn(expectedRides);

        List<RideDTO> rides = useCase.execute(userEmail);

        assertThat(rides).hasSize(2);
    }

    @Test
    void givenUserNotFound_whenExecute_thenThrowsUserNotFoundException() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userEmail))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void givenUserWithNoRides_whenExecute_thenReturnsEmptyList() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(rideRepository.findCompletedRidesByUserId(user.getId())).thenReturn(Arrays.asList());

        List<RideDTO> rides = useCase.execute(userEmail);

        assertThat(rides).isEmpty();
    }
}
