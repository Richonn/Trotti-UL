package ca.ulaval.glo4003.trotti.interfaces.rest.ride.response;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TripHistoryResponseTest {

    @Test
    void givenValidValues_whenCreated_thenReturnsCorrectValues() {
        TripHistoryResponse.TripItem item1 = new TripHistoryResponse.TripItem(
                "ride-1", "VACHON", "PEPS", "2025-01-01T10:00:00", "2025-01-01T10:15:00", 15, 0
        );
        TripHistoryResponse.TripSummaryDto summary = new TripHistoryResponse.TripSummaryDto(
                1, 15, 15, "VACHON"
        );
        List<TripHistoryResponse.TripItem> trips = Collections.singletonList(item1);

        TripHistoryResponse response = new TripHistoryResponse(trips, summary);

        assertThat(response.getTrips()).hasSize(1);
        assertThat(response.getSummary()).isEqualTo(summary);
    }

    @Test
    void givenTripItem_whenCreated_thenReturnsCorrectValues() {
        TripHistoryResponse.TripItem item = new TripHistoryResponse.TripItem(
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

    @Test
    void givenTripSummary_whenCreated_thenReturnsCorrectValues() {
        TripHistoryResponse.TripSummaryDto summary = new TripHistoryResponse.TripSummaryDto(
                10, 150, 15, "VACHON"
        );

        assertThat(summary.getTotalTrips()).isEqualTo(10);
        assertThat(summary.getTotalDurationMinutes()).isEqualTo(150);
        assertThat(summary.getAverageDurationMinutes()).isEqualTo(15);
        assertThat(summary.getFavoriteStation()).isEqualTo("VACHON");
    }

    @Test
    void givenSetters_whenUsed_thenValuesChange() {
        TripHistoryResponse.TripItem item = new TripHistoryResponse.TripItem(
                "ride-1", "VACHON", "PEPS", "2025-01-01T10:00:00", "2025-01-01T10:15:00", 15, 30
        );
        item.setRideId("ride-2");
        item.setStartStation("PEPS");
        item.setEndStation("VACHON");
        item.setDurationMinutes(20);
        item.setDurationSeconds(45);

        assertThat(item.getRideId()).isEqualTo("ride-2");
        assertThat(item.getStartStation()).isEqualTo("PEPS");
        assertThat(item.getEndStation()).isEqualTo("VACHON");
        assertThat(item.getDurationMinutes()).isEqualTo(20);
        assertThat(item.getDurationSeconds()).isEqualTo(45);
    }

    @Test
    void givenSummarySetters_whenUsed_thenValuesChange() {
        TripHistoryResponse.TripSummaryDto summary = new TripHistoryResponse.TripSummaryDto(
                10, 150, 15, "VACHON"
        );
        summary.setTotalTrips(20);
        summary.setTotalDurationMinutes(300);
        summary.setAverageDurationMinutes(15);
        summary.setFavoriteStation("PEPS");

        assertThat(summary.getTotalTrips()).isEqualTo(20);
        assertThat(summary.getTotalDurationMinutes()).isEqualTo(300);
        assertThat(summary.getFavoriteStation()).isEqualTo("PEPS");
    }
}
