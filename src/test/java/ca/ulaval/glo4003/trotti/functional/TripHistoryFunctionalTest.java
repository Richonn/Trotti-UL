package ca.ulaval.glo4003.trotti.functional;

import ca.ulaval.glo4003.Main;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class TripHistoryFunctionalTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    private static HttpServer server;
    private static final String BASE_URL = "http://localhost:8080/api";

    @BeforeAll
    static void setUp() {
        server = Main.startServer();
        RestAssured.baseURI = BASE_URL;
    }

    @AfterAll
    static void tearDown() {
        if (server != null) {
            server.shutdownNow();
        }
    }

    @Test
    void givenCompleteUserJourney_whenGettingTripHistory_thenReturnsTripsWithSummary() {
        String email = "alexandra" + System.currentTimeMillis() + "@ulaval.ca";
        String password = "Alexandra123!";

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "name": "Alexandra Tremblay",
                    "dateOfBirth": "1995-08-15",
                    "gender": "F",
                    "idul": "altrm001",
                    "email": "%s",
                    "password": "%s"
                }
                """, email, password))
                .when()
                .post("/user/register")
                .then()
                .statusCode(201);

        String token = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "email": "%s",
                    "password": "%s"
                }
                """, email, password))
                .when()
                .post("/user/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/user/activate")
                .then()
                .statusCode(200);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                {
                    "session": "A2025",
                    "extraMinutesPerDay": 30,
                    "monthlyBilling": false,
                    "cardNumber": "1234567812345678",
                    "cvv": "123",
                    "expiryDate": "12/26"
                }
                """)
                .when()
                .post("/pass/purchase")
                .then()
                .statusCode(200);

        for (int i = 0; i < 3; i++) {
            String rideCode = given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .post("/ride/request-code")
                    .then()
                    .statusCode(200)
                    .extract()
                    .path("rideCode.code");

            given()
                    .header("Authorization", "Bearer " + token)
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
                    .header("Authorization", "Bearer " + token)
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

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/ride/trip-history")
                .then()
                .statusCode(200)
                .body("trips", hasSize(3))
                .body("trips[0].rideId", notNullValue())
                .body("trips[0].startStation", notNullValue())
                .body("trips[0].endStation", notNullValue())
                .body("trips[0].startTime", notNullValue())
                .body("trips[0].endTime", notNullValue())
                .body("trips[0].durationMinutes", greaterThanOrEqualTo(0))
                .body("trips[0].durationSeconds", greaterThanOrEqualTo(0))
                .body("summary.totalTrips", equalTo(3))
                .body("summary.totalDurationMinutes", greaterThanOrEqualTo(0))
                .body("summary.averageDurationMinutes", greaterThanOrEqualTo(0))
                .body("summary.favoriteStation", anyOf(equalTo("VACHON"), equalTo("PEPS")));
    }
}
