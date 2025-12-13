package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EndRideRequestTest {

    @Test
    void givenValidValues_whenSet_thenReturnsCorrectValues() {
        EndRideRequest request = new EndRideRequest();
        request.setStation("VACHON");
        request.setSlot("3");

        assertThat(request.getStation()).isEqualTo("VACHON");
        assertThat(request.getSlot()).isEqualTo("3");
    }

    @Test
    void givenDifferentStation_whenSet_thenReturnsStation() {
        EndRideRequest request = new EndRideRequest();
        request.setStation("PEPS");

        assertThat(request.getStation()).isEqualTo("PEPS");
    }
}
