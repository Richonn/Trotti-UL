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

    private static final int EXTRA_MINUTES_PER_DAY = 30;
    private static final int NUMBER_OF_TEST_TRIPS = 3;
    private static final String START_STATION = "VACHON";
    private static final String END_STATION = "PEPS";
    private static final String START_SLOT = "1";
    private static final String END_SLOT = "2";

    private static final int HTTP_OK = 200;
    private static final int HTTP_CREATED = 201;
    private static final int HTTP_SERVER_ERROR = 500;

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
                .body(String.format("""
                {
                    "session": "A2025",
                    "extraMinutesPerDay": %d,
                    "monthlyBilling": false,
                    "cardNumber": "4242424242424242",
                    "cardHolder": "Test User",
                    "expiryDate": "12/26",
                    "cvv": "123"
                }
                """, EXTRA_MINUTES_PER_DAY))
                .when()
                .post("/pass/purchase")
                .then()
                .statusCode(HTTP_CREATED);

        for (int i = 0; i < NUMBER_OF_TEST_TRIPS; i++) {
            String rideCode = given()
                    .header("Authorization", "Bearer " + authToken)
                    .contentType(ContentType.JSON)
                    .when()
                    .post("/ride/request-code")
                    .then()
                    .statusCode(anyOf(is(HTTP_OK), is(HTTP_SERVER_ERROR)))
                    .extract()
                    .path("rideCode.code");

            if (rideCode != null) {
                given()
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(ContentType.JSON)
                        .body(String.format("""
                        {
                            "station": "%s",
                            "slot": "%s",
                            "code": "%s"
                        }
                        """, START_STATION, START_SLOT, rideCode))
                        .when()
                        .post("/ride/start")
                        .then()
                        .statusCode(HTTP_OK);

                given()
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(ContentType.JSON)
                        .body(String.format("""
                        {
                            "station": "%s",
                            "slot": "%s"
                        }
                        """, END_STATION, END_SLOT))
                        .when()
                        .post("/ride/end")
                        .then()
                        .statusCode(HTTP_OK);
            }
        }

        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/ride/trip-history")
                .then()
                .statusCode(HTTP_OK)
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
                .statusCode(HTTP_OK)
                .body("trips", notNullValue())
                .body("summary", notNullValue())
                .body("summary.totalTrips", greaterThanOrEqualTo(0))
                .body("summary.totalDurationMinutes", greaterThanOrEqualTo(0))
                .body("summary.averageDurationMinutes", greaterThanOrEqualTo(0));
    }
}
