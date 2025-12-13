package ca.ulaval.glo4003.trotti.application.scooter;

import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.scooter.exception.ScooterNotFoundException;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.StationState;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartTransferScooterUseCaseTest {
    @Mock
    private StationRepository stationRepository;

    @Mock
    private ScooterRepository scooterRepository;

    private StartTransferScooterUseCase useCase;
    private String stationId;
    private int slotNumber;
    private Scooter scooter;
    private Station station;

    @BeforeEach
    void setUp() {
        useCase = new StartTransferScooterUseCase(stationRepository, scooterRepository);
        stationId = "VACHON";
        slotNumber = 1;
        scooter = Scooter.create("scooter-1", 80);
        scooter.returnToStation(stationId, slotNumber);
        station = new Station(stationId, "Campus Principal", 10);
        station.startMaintenance();
    }

    @Test
    void givenValidScooterAndStationUnderMaintenance_whenExecute_thenStartsTransfer() {
        when(scooterRepository.findByStationIdAndSlotNumber(stationId, slotNumber)).thenReturn(Optional.of(scooter));
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));

        String scooterId = useCase.execute(slotNumber, stationId);

        assertThat(scooterId).isEqualTo(scooter.getId());
        verify(scooterRepository).save(scooter);
    }

    @Test
    void givenScooterNotFound_whenExecute_thenThrowsScooterNotFoundException() {
        when(scooterRepository.findByStationIdAndSlotNumber(stationId, slotNumber)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(slotNumber, stationId))
                .isInstanceOf(ScooterNotFoundException.class);
    }

    @Test
    void givenStationNotFound_whenExecute_thenThrowsStationNotFoundException() {
        when(scooterRepository.findByStationIdAndSlotNumber(stationId, slotNumber)).thenReturn(Optional.of(scooter));
        when(stationRepository.findById(stationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(slotNumber, stationId))
                .isInstanceOf(StationNotFoundException.class);
    }

    @Test
    void givenStationNotUnderMaintenance_whenExecute_thenThrowsIllegalArgumentException() {
        Station activeStation = new Station(stationId, "Campus Principal", 10);
        when(scooterRepository.findByStationIdAndSlotNumber(stationId, slotNumber)).thenReturn(Optional.of(scooter));
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(activeStation));

        assertThatThrownBy(() -> useCase.execute(slotNumber, stationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Station is not under maintenance");
    }
}
