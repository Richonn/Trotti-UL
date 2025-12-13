package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EndTransferRequestTest {

    @Test
    void givenValidValues_whenSet_thenReturnsCorrectValues() {
        EndTransferRequest request = new EndTransferRequest();
        request.setScooterId("scooter-123");
        request.setStation("VACHON");
        request.setSlot("10");

        assertThat(request.getScooterId()).isEqualTo("scooter-123");
        assertThat(request.getStation()).isEqualTo("VACHON");
        assertThat(request.getSlot()).isEqualTo("10");
    }

    @Test
    void givenDifferentScooterId_whenSet_thenReturnsScooterId() {
        EndTransferRequest request = new EndTransferRequest();
        request.setScooterId("scooter-999");

        assertThat(request.getScooterId()).isEqualTo("scooter-999");
    }
}
