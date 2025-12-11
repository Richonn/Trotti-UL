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

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

class ApiFunctionalTest {

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
    void register_shouldCreateNewUser() {
        String email = "alice" + System.currentTimeMillis() + "@test.com";

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "name": "Alice Test",
                    "dateOfBirth": "2000-01-01",
                    "gender": "F",
                    "idul": "alice123",
                    "email": "%s",
                    "password": "Password@123"
                }
                """, email))
                .when()
                .post("/user/register")
                .then()
                .statusCode(201)
                .body("email", equalTo(email))
                .body("name", equalTo("Alice Test"))
                .body("gender", equalTo("F"))
                .body("idul", equalTo("alice123"));
    }

    @Test
    void register_shouldFailWithDuplicateEmail() {
        String email = "duplicate" + System.currentTimeMillis() + "@test.com";

        fixtures.createUser(email, "Password@123", "User One", "user1");

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "name": "User Two",
                    "dateOfBirth": "2000-01-01",
                    "gender": "F",
                    "idul": "user2",
                    "email": "%s",
                    "password": "Password@123"
                }
                """, email))
                .when()
                .post("/user/register")
                .then()
                .statusCode(anyOf(is(400), is(409)));
    }


    @Test
    void login_shouldFailWithWrongPassword() {
        String email = "wrongpwd" + System.currentTimeMillis() + "@test.com";
        fixtures.createUser(email, "Password@123", "Wrong User", "wrong123");

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "email": "%s",
                    "password": "WrongPassword"
                }
                """, email))
                .when()
                .post("/user/login")
                .then()
                .statusCode(401);
    }

    @Test
    void getUser_shouldReturnUserInfo() {
        String email = "getuser" + System.currentTimeMillis() + "@test.com";
        fixtures.createUser(email, "Password@123", "Get User", "get123");

        given()
                .when()
                .get("/user/all")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("email", hasItem(email));
    }

    @Test
    void activateAccount_shouldReturn200() {
        String email = "activate" + System.currentTimeMillis() + "@test.com";
        User user = fixtures.createUser(email, "Password@123", "Activate User", "activate123");
        String token = fixtures.generateToken(email);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .post("/user/activate")
                .then()
                .statusCode(200);
    }

    @Test
    void purchasePass_shouldCreatePass() {
        String email = "purchase" + System.currentTimeMillis() + "@test.com";
        User user = fixtures.createActivatedUser(email, "Password@123", "Purchase User", "purchase123");
        String token = fixtures.generateToken(email);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                {
                    "session": "A2025",
                    "extraMinutesPerDay": 20,
                    "monthlyBilling": true,
                    "cardNumber": "4242424242424242",
                    "cardHolder": "Purchase User",
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
    void getMyPurchases_shouldReturnPurchases() {
        String email = "mypurchases" + System.currentTimeMillis() + "@test.com";
        User user = fixtures.createActivatedUserWithPass(email, "Password@123", "Purchases User", "purchases123", Session.A2025);
        String token = fixtures.generateToken(email);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/pass/my-purchases")
                .then()
                .statusCode(200)
                .body("passes", notNullValue());
    }

    @Test
    void requestRideCode_shouldGenerateCodeWithActivePass() {
        String email = "ridecode" + System.currentTimeMillis() + "@test.com";
        User user = fixtures.createActivatedUserWithPass(email, "Password@123", "Ride User", "ride123", Session.A2025);
        String token = fixtures.generateToken(email);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .post("/ride/request-code")
                .then()
                .statusCode(200)
                .body("code", notNullValue());
    }

    @Test
    void requestRideCode_shouldFailWithoutActivePass() {
        String email = "nopass" + System.currentTimeMillis() + "@test.com";
        User user = fixtures.createActivatedUser(email, "Password@123", "No Pass User", "nopass123");
        String token = fixtures.generateToken(email);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .post("/ride/request-code")
                .then()
                .statusCode(403);
    }
}
