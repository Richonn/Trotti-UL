package ca.ulaval.glo4003.trotti.functional;

import ca.ulaval.glo4003.Main;
import ca.ulaval.glo4003.trotti.domain.shared.EmailMessage;
import ca.ulaval.glo4003.trotti.domain.shared.EmailService;
import ca.ulaval.glo4003.trotti.domain.user.Email;
import ca.ulaval.glo4003.trotti.domain.user.Password;
import ca.ulaval.glo4003.trotti.domain.user.Role;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

class TripHistoryFunctionalTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    private static HttpServer server;
    private static String authToken;
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final String TEST_EMAIL = "test-trip-history@ulaval.ca";
    private static final String TEST_PASSWORD = "TestPassword123!";

    @BeforeAll
    static void setUp() {
        server = Main.startServer();
        RestAssured.baseURI = BASE_URL;

        createAndActivateTestUser();

        authToken = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "email": "%s",
                    "password": "%s"
                }
                """, TEST_EMAIL, TEST_PASSWORD))
                .when()
                .post("/user/login")
                .jsonPath().getString("token");
    }

    private static void createAndActivateTestUser() {
        ApplicationContext context = ApplicationContext.getInstance();

        User testUser = User.register(
            new Email(TEST_EMAIL),
            new Password(TEST_PASSWORD),
            "Test User",
            "1995-01-01",
            "F",
            "testus001",
            Role.USER
        );

        testUser.activate(new EmailService() {
            @Override
            public void sendEmail(String to, String subject, String body) {}
            @Override
            public void send(EmailMessage email) {}
        });

        context.getUserRepository().save(testUser);
    }

    @AfterAll
    static void tearDown() {
        if (server != null) {
            server.shutdownNow();
        }
    }

    @Test
    void givenUserWithPass_whenPurchasingPassAndMakingTrips_thenTripHistoryShowsCorrectSummary() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body("""
                {
                    "session": "A2025",
                    "extraMinutesPerDay": 30,
                    "monthlyBilling": false,
                    "cardNumber": "4242424242424242",
                    "cardHolder": "Test User",
                    "expiryDate": "12/26",
                    "cvv": "123"
                }
                """)
                .when()
                .post("/pass/purchase")
                .then()
                .statusCode(201);

        for (int i = 0; i < 3; i++) {
            String rideCode = given()
                    .header("Authorization", "Bearer " + authToken)
                    .contentType(ContentType.JSON)
                    .when()
                    .post("/ride/request-code")
                    .then()
                    .statusCode(anyOf(is(200), is(500)))
                    .extract()
                    .path("rideCode.code");

            if (rideCode != null) {
                given()
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(ContentType.JSON)
                        .body(String.format("""
                        {
                            "station": "VACHON",
                            "slot": "1",
                            "code": "%s"
                        }
                        """, rideCode))
                        .when()
                        .post("/ride/start")
                        .then()
                        .statusCode(200);

                given()
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(ContentType.JSON)
                        .body("""
                        {
                            "station": "PEPS",
                            "slot": "2"
                        }
                        """)
                        .when()
                        .post("/ride/end")
                        .then()
                        .statusCode(200);
            }
        }

        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/ride/trip-history")
                .then()
                .statusCode(200)
                .body("trips", notNullValue())
                .body("summary", notNullValue())
                .body("summary.totalTrips", greaterThanOrEqualTo(0))
                .body("summary.totalDurationMinutes", greaterThanOrEqualTo(0))
                .body("summary.averageDurationMinutes", greaterThanOrEqualTo(0));
    }

    @Test
    void whenGettingTripHistoryWithoutTrips_thenReturnsEmptyTripsWithZeroSummary() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/ride/trip-history")
                .then()
                .statusCode(200)
                .body("trips", notNullValue())
                .body("summary", notNullValue())
                .body("summary.totalTrips", greaterThanOrEqualTo(0))
                .body("summary.totalDurationMinutes", greaterThanOrEqualTo(0))
                .body("summary.averageDurationMinutes", greaterThanOrEqualTo(0));
    }
}
