package ca.ulaval.glo4003.trotti.application.station;

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
class StartStationMaintenanceUseCaseTest {
    @Mock
    private StationRepository stationRepository;

    private StartStationMaintenanceUseCase useCase;
    private String stationId;
    private Station station;

    @BeforeEach
    void setUp() {
        useCase = new StartStationMaintenanceUseCase(stationRepository);
        stationId = "VACHON";
        station = new Station(stationId, "Campus Principal", 10);
    }

    @Test
    void givenValidStation_whenExecute_thenStartsMaintenance() {
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));

        useCase.execute(stationId);

        verify(stationRepository).save(station);
    }

    @Test
    void givenStationNotFound_whenExecute_thenThrowsStationNotFoundException() {
        when(stationRepository.findById(stationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(stationId))
                .isInstanceOf(StationNotFoundException.class);
    }
}
