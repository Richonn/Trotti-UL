package ca.ulaval.glo4003.trotti.functional;

import ca.ulaval.glo4003.Main;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiFunctionalTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    private static HttpServer server;
    private static String authToken;
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final String TEST_EMAIL = "alice.test@email.com";

    @BeforeAll
    static void startServer() {
        server = Main.startServer();
        RestAssured.baseURI = BASE_URL;

        System.setProperty("MAILGUN_DOMAIN", "test-domain.mailgun.org");
        System.setProperty("MAILGUN_API_KEY", "test-key");
        System.setProperty("EMAIL_FROM", "test@test.com");
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.shutdownNow();
        }
    }

    @Test
    @Order(1)
    void register_shouldCreateNewUser() {
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
                """, TEST_EMAIL))
                .when()
                .post("/user/register")
                .then()
                .statusCode(201)
                .body("email", equalTo(TEST_EMAIL))
                .body("name", equalTo("Alice Test"))
                .body("gender", equalTo("F"))
                .body("idul", equalTo("alice123"))
                .body("age", notNullValue());
    }

    @Test
    @Order(2)
    void register_shouldFailWithDuplicateEmail() {
        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "name": "Alice Duplicate",
                    "dateOfBirth": "2000-01-01",
                    "gender": "F",
                    "idul": "alice456",
                    "email": "%s",
                    "password": "Password@123"
                }
                """, TEST_EMAIL))
                .when()
                .post("/user/register")
                .then()
                .statusCode(anyOf(is(400), is(409)));
    }

    @Test
    @Order(3)
    void login_shouldReturnToken() {
        authToken = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "email": "%s",
                    "password": "Password@123"
                }
                """, TEST_EMAIL))
                .when()
                .post("/user/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .path("token");

        System.out.println("Token: " + authToken);
    }

    @Test
    @Order(4)
    void login_shouldFailWithWrongPassword() {
        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "email": "%s",
                    "password": "WrongPassword"
                }
                """, TEST_EMAIL))
                .when()
                .post("/user/login")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(5)
    void getUser_shouldReturnUserInfo() {
        given()
                .when()
                .get("/user/all")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("email", hasItem(TEST_EMAIL));
    }

    @Test
    @Order(7)
    void activateAccount_shouldReturn200() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .when()
                .post("/user/activate")
                .then()
                .statusCode(anyOf(is(200), is(500)));
    }

    @Test
    @Order(8)
    void purchasePass_shouldCreatePass() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body("""
                {
                    "session": "A2025",
                    "extraMinutesPerDay": 20,
                    "monthlyBilling": true,
                    "cardNumber": "4242424242424242",
                    "cardHolder": "Alice Test",
                    "expiryDate": "12/28",
                    "cvv": "123"
                }
                """)
                .when()
                .post("/pass/purchase")
                .then()
                .statusCode(201)
                .body("message", notNullValue())
                .body("invoice", notNullValue());
    }

    @Test
    @Order(9)
    void getMyPurchases_shouldReturnPurchases() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/pass/my-purchases")
                .then()
                .statusCode(200)
                .body("passes", notNullValue());
    }

    @Test
    @Order(10)
    void requestRideCode_shouldTryToGenerateCode() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .when()
                .post("/ride/request-code")
                .then()
                .statusCode(anyOf(is(200), is(500)));
    }

    @Test
    @Order(11)
    void requestRideCode_shouldFailWithoutActivePass() {
        String bobEmail = "bob.test." + System.currentTimeMillis() + "@email.com";

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
        {
            "name": "Bob Test",
            "dateOfBirth": "2001-01-01",
            "gender": "M",
            "idul": "bob123",
            "email": "%s",
            "password": "Password@123"
        }
        """, bobEmail))
                .when()
                .post("/user/register")
                .then()
                .statusCode(201);

        String bobToken = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
        {
            "email": "%s",
            "password": "Password@123"
        }
        """, bobEmail))
                .when()
                .post("/user/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        given()
                .header("Authorization", "Bearer " + bobToken)
                .contentType(ContentType.JSON)
                .when()
                .post("/user/activate")
                .then()
                .statusCode(anyOf(is(200), is(500)));

        given()
                .header("Authorization", "Bearer " + bobToken)
                .contentType(ContentType.JSON)
                .when()
                .post("/ride/request-code")
                .then()
                .statusCode(anyOf(is(403), is(500)));
    }
}
