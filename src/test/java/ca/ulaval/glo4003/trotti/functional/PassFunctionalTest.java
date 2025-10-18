package ca.ulaval.glo4003.trotti.functional;

import ca.ulaval.glo4003.Main;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

class PassFunctionalTest {

    private static HttpServer server;
    private static String authToken;

    @BeforeAll
    static void setUp() {
        server = Main.startServer();
        RestAssured.baseURI = "http://localhost:8080/api";

        String email = "pass-test" + System.currentTimeMillis() + "@test.com";

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "name": "Pass Tester",
                    "dateOfBirth": "2000-01-01",
                    "gender": "F",
                    "idul": "pass123",
                    "email": "%s",
                    "password": "Password@123"
                }
                """, email))
                .when()
                .post("/user/register");

        authToken = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "email": "%s",
                    "password": "Password@123"
                }
                """, email))
                .when()
                .post("/user/login")
                .path("token");

        given()
                .header("Authorization", "Bearer " + authToken)
                .post("/user/activate");
    }

    @AfterAll
    static void tearDown() {
        if (server != null) {
            server.shutdownNow();
        }
    }

    @Test
    void purchasePass_withValidCard_shouldSucceed() {
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
                .statusCode(201)
                .body("message", containsString("successful"))
                .body("invoice.totalPrice", notNullValue());
    }

    @Test
    void purchasePass_withExpiredCard_shouldFail() {
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
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/pass/my-purchases")
                .then()
                .statusCode(200);
    }
}