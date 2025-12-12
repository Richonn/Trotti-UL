package ca.ulaval.glo4003.trotti.application.ride.mapper;

import ca.ulaval.glo4003.trotti.application.ride.dto.RideDTO;
import ca.ulaval.glo4003.trotti.domain.ride.Ride;

import java.util.List;
import java.util.stream.Collectors;

public class RideMapper {

    public static RideDTO toDTO(Ride ride) {
        return new RideDTO(
            ride.getId(),
            ride.getUserId(),
            ride.getScooterId(),
            ride.getStartStationId(),
            ride.getEndStationId(),
            ride.getStartTime(),
            ride.getEndTime(),
            ride.getStatus()
        );
    }

    public static List<RideDTO> toDTOList(List<Ride> rides) {
        return rides.stream()
            .map(RideMapper::toDTO)
            .collect(Collectors.toList());
    }
}
