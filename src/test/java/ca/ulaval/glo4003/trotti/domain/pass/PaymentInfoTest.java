package ca.ulaval.glo4003.trotti.domain.pass;

import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class PaymentInfoTest {

    @Test
    void givenValidData_whenCreatingPaymentInfo_thenSucceeds() {
        int futureYear = (Year.now().getValue() + 1) % 100;
        String expiryDate = String.format("12/%02d", futureYear);

        PaymentInfo paymentInfo = new PaymentInfo(
            "4532015112830366",
            "John Doe",
            expiryDate,
            "123"
        );

        assertEquals("John Doe", paymentInfo.getCardHolder());
        assertEquals("123", paymentInfo.getCvv());
        assertNotNull(paymentInfo.getCardNumber());
        assertNotNull(paymentInfo.getExpiryDate());
    }

    @Test
    void givenInvalidCardNumber_whenCreatingPaymentInfo_thenThrowsException() {
        int futureYear = (Year.now().getValue() + 1) % 100;
        String expiryDate = String.format("12/%02d", futureYear);

        assertThrows(IllegalArgumentException.class, () ->
            new PaymentInfo("1234567890123456", "John Doe", expiryDate, "123")
        );
    }

    @Test
    void givenEmptyCardHolder_whenCreatingPaymentInfo_thenThrowsException() {
        int futureYear = (Year.now().getValue() + 1) % 100;
        String expiryDate = String.format("12/%02d", futureYear);

        assertThrows(IllegalArgumentException.class, () ->
            new PaymentInfo("4532015112830366", "", expiryDate, "123")
        );
        assertThrows(IllegalArgumentException.class, () ->
            new PaymentInfo("4532015112830366", "   ", expiryDate, "123")
        );
    }

    @Test
    void givenNullCardHolder_whenCreatingPaymentInfo_thenThrowsException() {
        int futureYear = (Year.now().getValue() + 1) % 100;
        String expiryDate = String.format("12/%02d", futureYear);

        assertThrows(IllegalArgumentException.class, () ->
            new PaymentInfo("4532015112830366", null, expiryDate, "123")
        );
    }

    @Test
    void givenInvalidExpiryDate_whenCreatingPaymentInfo_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            new PaymentInfo("4532015112830366", "John Doe", "invalid", "123")
        );
    }

    @Test
    void givenEmptyCvv_whenCreatingPaymentInfo_thenThrowsException() {
        int futureYear = (Year.now().getValue() + 1) % 100;
        String expiryDate = String.format("12/%02d", futureYear);

        assertThrows(IllegalArgumentException.class, () ->
            new PaymentInfo("4532015112830366", "John Doe", expiryDate, "")
        );
    }

    @Test
    void givenNullCvv_whenCreatingPaymentInfo_thenThrowsException() {
        int futureYear = (Year.now().getValue() + 1) % 100;
        String expiryDate = String.format("12/%02d", futureYear);

        assertThrows(IllegalArgumentException.class, () ->
            new PaymentInfo("4532015112830366", "John Doe", expiryDate, null)
        );
    }

    @Test
    void givenInvalidCvvFormat_whenCreatingPaymentInfo_thenThrowsException() {
        int futureYear = (Year.now().getValue() + 1) % 100;
        String expiryDate = String.format("12/%02d", futureYear);

        assertThrows(IllegalArgumentException.class, () ->
            new PaymentInfo("4532015112830366", "John Doe", expiryDate, "12")
        );
        assertThrows(IllegalArgumentException.class, () ->
            new PaymentInfo("4532015112830366", "John Doe", expiryDate, "12345")
        );
        assertThrows(IllegalArgumentException.class, () ->
            new PaymentInfo("4532015112830366", "John Doe", expiryDate, "ABC")
        );
    }

    @Test
    void givenValid4DigitCvv_whenCreatingPaymentInfo_thenSucceeds() {
        int futureYear = (Year.now().getValue() + 1) % 100;
        String expiryDate = String.format("12/%02d", futureYear);

        PaymentInfo paymentInfo = new PaymentInfo(
            "4532015112830366",
            "John Doe",
            expiryDate,
            "1234"
        );

        assertEquals("1234", paymentInfo.getCvv());
    }

    @Test
    void givenTwoPaymentInfosWithSameData_whenComparingEquality_thenAreEqual() {
        int futureYear = (Year.now().getValue() + 1) % 100;
        String expiryDate = String.format("12/%02d", futureYear);

        PaymentInfo info1 = new PaymentInfo("4532015112830366", "John Doe", expiryDate, "123");
        PaymentInfo info2 = new PaymentInfo("4532015112830366", "John Doe", expiryDate, "123");

        assertEquals(info1, info2);
        assertEquals(info1.hashCode(), info2.hashCode());
    }

    @Test
    void givenTwoPaymentInfosWithDifferentData_whenComparingEquality_thenAreNotEqual() {
        int futureYear = (Year.now().getValue() + 1) % 100;
        String expiryDate = String.format("12/%02d", futureYear);

        PaymentInfo info1 = new PaymentInfo("4532015112830366", "John Doe", expiryDate, "123");
        PaymentInfo info2 = new PaymentInfo("5425233430109903", "Jane Doe", expiryDate, "456");

        assertNotEquals(info1, info2);
    }
}
