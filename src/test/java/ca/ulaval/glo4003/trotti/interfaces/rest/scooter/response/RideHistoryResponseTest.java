package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.response;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RideHistoryResponseTest {

    @Test
    void givenEmptyList_whenCreated_thenReturnsEmptyRides() {
        RideHistoryResponse response = new RideHistoryResponse(Collections.emptyList());

        assertThat(response.getRides()).isEmpty();
    }

    @Test
    void givenRides_whenCreated_thenReturnsRides() {
        RideHistoryResponse.RideHistoryItem item1 = new RideHistoryResponse.RideHistoryItem(
                "ride-1", "VACHON", "PEPS", "2025-01-01T10:00:00", "2025-01-01T10:15:00", 15, 0
        );
        RideHistoryResponse.RideHistoryItem item2 = new RideHistoryResponse.RideHistoryItem(
                "ride-2", "PEPS", "VACHON", "2025-01-01T11:00:00", "2025-01-01T11:20:00", 20, 0
        );
        List<RideHistoryResponse.RideHistoryItem> items = Arrays.asList(item1, item2);

        RideHistoryResponse response = new RideHistoryResponse(items);

        assertThat(response.getRides()).hasSize(2);
        assertThat(response.getRides().get(0).getRideId()).isEqualTo("ride-1");
        assertThat(response.getRides().get(1).getRideId()).isEqualTo("ride-2");
    }

    @Test
    void givenRideHistoryItem_whenCreated_thenReturnsCorrectValues() {
        RideHistoryResponse.RideHistoryItem item = new RideHistoryResponse.RideHistoryItem(
                "ride-1", "VACHON", "PEPS", "2025-01-01T10:00:00", "2025-01-01T10:15:00", 15, 30
        );

        assertThat(item.getRideId()).isEqualTo("ride-1");
        assertThat(item.getStartStation()).isEqualTo("VACHON");
        assertThat(item.getEndStation()).isEqualTo("PEPS");
        assertThat(item.getStartTime()).isEqualTo("2025-01-01T10:00:00");
        assertThat(item.getEndTime()).isEqualTo("2025-01-01T10:15:00");
        assertThat(item.getDurationMinutes()).isEqualTo(15);
        assertThat(item.getDurationSeconds()).isEqualTo(30);
    }
}
