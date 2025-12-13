package ca.ulaval.glo4003.trotti.application.scooter;

import ca.ulaval.glo4003.trotti.domain.scooter.Scooter;
import ca.ulaval.glo4003.trotti.domain.scooter.ScooterRepository;
import ca.ulaval.glo4003.trotti.domain.scooter.exception.ScooterNotFoundException;
import ca.ulaval.glo4003.trotti.domain.station.Station;
import ca.ulaval.glo4003.trotti.domain.station.StationRepository;
import ca.ulaval.glo4003.trotti.domain.station.exception.StationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EndTransferScooterUseCaseTest {
    @Mock
    private StationRepository stationRepository;

    @Mock
    private ScooterRepository scooterRepository;

    private EndTransferScooterUseCase useCase;
    private String scooterId;
    private String stationId;
    private int slotNumber;
    private Scooter scooter;
    private Station station;

    @BeforeEach
    void setUp() {
        useCase = new EndTransferScooterUseCase(stationRepository, scooterRepository);
        scooterId = "scooter-1";
        stationId = "PEPS";
        slotNumber = 3;
        scooter = Scooter.create(scooterId, 75);
        scooter.startTransfer();
        station = new Station(stationId, "PEPS Location", 10);
    }

    @Test
    void givenValidScooterInTransfer_whenExecute_thenCompletesTransfer() {
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.of(scooter));
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
        when(scooterRepository.findByStationIdAndSlotNumber(stationId, slotNumber)).thenReturn(Optional.empty());

        useCase.execute(scooterId, slotNumber, stationId);

        verify(scooterRepository).save(scooter);
    }

    @Test
    void givenScooterNotFound_whenExecute_thenThrowsScooterNotFoundException() {
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(scooterId, slotNumber, stationId))
                .isInstanceOf(ScooterNotFoundException.class);
    }

    @Test
    void givenStationNotFound_whenExecute_thenThrowsStationNotFoundException() {
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.of(scooter));
        when(stationRepository.findById(stationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(scooterId, slotNumber, stationId))
                .isInstanceOf(StationNotFoundException.class);
    }

    @Test
    void givenStationUnderMaintenance_whenExecute_thenThrowsIllegalStateException() {
        station.startMaintenance();
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.of(scooter));
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));

        assertThatThrownBy(() -> useCase.execute(scooterId, slotNumber, stationId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot transfer scooter to a station under maintenance");
    }

    @Test
    void givenSlotNotAvailable_whenExecute_thenThrowsIllegalStateException() {
        Scooter otherScooter = Scooter.create("scooter-2", 80);
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.of(scooter));
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
        when(scooterRepository.findByStationIdAndSlotNumber(stationId, slotNumber)).thenReturn(Optional.of(otherScooter));

        assertThatThrownBy(() -> useCase.execute(scooterId, slotNumber, stationId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Slot " + slotNumber + " is not available");
    }

    @Test
    void givenScooterNotInTransfer_whenExecute_thenThrowsIllegalStateException() {
        Scooter dockedScooter = Scooter.create(scooterId, 75);
        dockedScooter.returnToStation("VACHON", 1);
        when(scooterRepository.findById(scooterId)).thenReturn(Optional.of(dockedScooter));
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
        when(scooterRepository.findByStationIdAndSlotNumber(stationId, slotNumber)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(scooterId, slotNumber, stationId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Scooter is not in transfer state");
    }
}
