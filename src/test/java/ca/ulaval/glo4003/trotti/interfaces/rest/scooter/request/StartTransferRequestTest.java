package ca.ulaval.glo4003.trotti.interfaces.rest.scooter.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StartTransferRequestTest {

    @Test
    void givenValidValues_whenSet_thenReturnsCorrectValues() {
        StartTransferRequest request = new StartTransferRequest();
        request.setStation("VACHON");
        request.setSlot("5");

        assertThat(request.getStation()).isEqualTo("VACHON");
        assertThat(request.getSlot()).isEqualTo("5");
    }

    @Test
    void givenDifferentStation_whenSet_thenReturnsStation() {
        StartTransferRequest request = new StartTransferRequest();
        request.setStation("PEPS");

        assertThat(request.getStation()).isEqualTo("PEPS");
    }
}
