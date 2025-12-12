package ca.ulaval.glo4003.trotti.application.ride;

import ca.ulaval.glo4003.trotti.application.ride.dto.RideDTO;
import ca.ulaval.glo4003.trotti.application.ride.mapper.RideMapper;
import ca.ulaval.glo4003.trotti.domain.ride.Ride;
import ca.ulaval.glo4003.trotti.domain.ride.RideRepository;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.domain.user.UserRepository;
import ca.ulaval.glo4003.trotti.domain.user.exception.UserNotFoundException;

import java.util.List;

public class GetRideHistoryUseCase {
    private final UserRepository userRepository;
    private final RideRepository rideRepository;

    public GetRideHistoryUseCase(UserRepository userRepository, RideRepository rideRepository) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
    }

    public List<RideDTO> execute(String userEmail) {
        Email email = new Email(userEmail);
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        List<Ride> rides = rideRepository.findCompletedRidesByUserId(user.getId());
        return RideMapper.toDTOList(rides);
    }
}
