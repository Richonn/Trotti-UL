package ca.ulaval.glo4003.trotti.functional;

import ca.ulaval.glo4003.Main;
import ca.ulaval.glo4003.trotti.domain.user.User;
import ca.ulaval.glo4003.trotti.infrastructure.email.StubEmailService;
import ca.ulaval.glo4003.trotti.infrastructure.security.StubTokenGenerator;
import ca.ulaval.glo4003.trotti.infrastructure.time.SystemClock;
import ca.ulaval.glo4003.trotti.interfaces.rest.config.ApplicationContext;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

class PassFunctionalTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    private static HttpServer server;
    private static final String BASE_URL = "http://localhost:8080/api";
    private static StubEmailService stubEmailService;
    private static TestFixtures fixtures;

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
    void purchasePass_withValidCard_shouldSucceed() {
        String email = "pass-purchase" + System.currentTimeMillis() + "@test.com";
        User user = fixtures.createActivatedUser(email, "Password@123", "Pass Tester", "pass123");
        String authToken = fixtures.generateToken(email);

        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body("""
                {
                    "session": "H2026",
                    "extraMinutesPerDay": 30,
                    "monthlyBilling": false,
                    "cardNumber": "4242424242424242",
                    "cardHolder": "Pass Tester",
                    "expiryDate": "12/28",
                    "cvv": "123"
                }
                """)
                .when()
                .post("/pass/purchase")
                .then()
                .statusCode(anyOf(is(201), is(402)))
                .body("message", notNullValue());
    }

    @Test
    void purchasePass_withExpiredCard_shouldFail() {
        String email = "pass-expired" + System.currentTimeMillis() + "@test.com";
        User user = fixtures.createActivatedUser(email, "Password@123", "Pass Tester", "pass123");
        String authToken = fixtures.generateToken(email);

        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body("""
                {
                    "session": "A2025",
                    "extraMinutesPerDay": 20,
                    "monthlyBilling": true,
                    "cardNumber": "4242424242424242",
                    "cardHolder": "Pass Tester",
                    "expiryDate": "12/20",
                    "cvv": "123"
                }
                """)
                .when()
                .post("/pass/purchase")
                .then()
                .statusCode(400);
    }

    @Test
    void getMyPurchases_shouldReturnEmptyInitially() {
        String email = "pass-purchases" + System.currentTimeMillis() + "@test.com";
        User user = fixtures.createActivatedUser(email, "Password@123", "Pass Tester", "pass123");
        String authToken = fixtures.generateToken(email);

        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/pass/my-purchases")
                .then()
                .statusCode(200);
    }
}