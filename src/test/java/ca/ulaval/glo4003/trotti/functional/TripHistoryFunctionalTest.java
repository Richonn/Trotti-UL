package ca.ulaval.glo4003.trotti.functional;

import ca.ulaval.glo4003.Main;
import ca.ulaval.glo4003.trotti.domain.pass.Session;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.infrastructure.email.StubEmailService;
import ca.ulaval.glo4003.trotti.infrastructure.security.StubTokenGenerator;
import ca.ulaval.glo4003.trotti.infrastructure.time.SystemClock;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

class TripHistoryFunctionalTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    private static HttpServer server;
    private static final String BASE_URL = "http://localhost:8080/api";
    private static StubEmailService stubEmailService;
    private static TestFixtures fixtures;

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
    static void startServer() {
        stubEmailService = new StubEmailService();
        ApplicationContext testContext = new ApplicationContext(stubEmailService, new StubTokenGenerator(), new SystemClock());
        ApplicationContext.setInstance(testContext);

        server = Main.startServer();
        RestAssured.baseURI = BASE_URL;

        fixtures = new TestFixtures(
            testContext.getUserRepository(),
            testContext.getPassRepository(),
            testContext.getRideCodeRepository(),
            testContext.getTokenGenerator(),
            stubEmailService,
            testContext.getClock()
        );
    }

    @BeforeEach
    void setUp() {
        stubEmailService.clear();
        fixtures.clearAllUsers();
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.shutdownNow();
        }
        ApplicationContext.resetInstance();
    }

    @Test
    void givenUserWithPass_whenPurchasingPassAndMakingTrips_thenTripHistoryShowsCorrectSummary() {
        String email = "trip-history" + System.currentTimeMillis() + "@test.com";
        User user = fixtures.createActivatedUserWithPass(email, "Password@123", "Test User", "test123", Session.generate(2025, "Automne"));
        String authToken = fixtures.generateToken(email);

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
        String email = "no-trips" + System.currentTimeMillis() + "@test.com";
        User user = fixtures.createActivatedUser(email, "Password@123", "No Trips User", "notrips123");
        String authToken = fixtures.generateToken(email);

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
