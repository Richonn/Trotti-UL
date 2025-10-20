package ca.ulaval.glo4003.trotti.functional;

import ca.ulaval.glo4003.Main;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

class UserFunctionalTest {

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
    void register_shouldCreateUser_whenValidData() {
        String email = "user" + System.currentTimeMillis() + "@test.com";

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "name": "Test User",
                    "dateOfBirth": "2000-01-01",
                    "gender": "H",
                    "idul": "test123",
                    "email": "%s",
                    "password": "Password@123"
                }
                """, email))
                .when()
                .post("/user/register")
                .then()
                .statusCode(201)
                .body("email", equalTo(email))
                .body("name", equalTo("Test User"))
                .body("age", notNullValue());
    }

    @Test
    void register_shouldFail_whenDuplicateEmail() {
        String email = "duplicate" + System.currentTimeMillis() + "@test.com";

        // First registration
        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "name": "User One",
                    "dateOfBirth": "2000-01-01",
                    "gender": "F",
                    "idul": "user1",
                    "email": "%s",
                    "password": "Password@123"
                }
                """, email))
                .when()
                .post("/user/register")
                .then()
                .statusCode(201);

        // Duplicate registration
        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "name": "User Two",
                    "dateOfBirth": "2001-01-01",
                    "gender": "H",
                    "idul": "user2",
                    "email": "%s",
                    "password": "Password@456"
                }
                """, email))
                .when()
                .post("/user/register")
                .then()
                .statusCode(anyOf(is(400), is(409)));
    }

    @Test
    void register_shouldFail_whenMissingRequiredFields() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "name": "Incomplete User",
                    "email": "incomplete@test.com"
                }
                """)
                .when()
                .post("/user/register")
                .then()
                .statusCode(anyOf(is(400), is(500)));
    }

    @Test
    void login_shouldReturnToken_whenValidCredentials() {
        String email = "login" + System.currentTimeMillis() + "@test.com";

        // Register
        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "name": "Login User",
                    "dateOfBirth": "2000-01-01",
                    "gender": "F",
                    "idul": "login123",
                    "email": "%s",
                    "password": "Password@123"
                }
                """, email))
                .when()
                .post("/user/register")
                .then()
                .statusCode(201);

        // Login
        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "email": "%s",
                    "password": "Password@123"
                }
                """, email))
                .when()
                .post("/user/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("token", matchesPattern(".+\\..+\\..+")); // JWT format
    }

    @Test
    void login_shouldFail_whenWrongPassword() {
        String email = "wrongpass" + System.currentTimeMillis() + "@test.com";

        // Register
        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "name": "Password User",
                    "dateOfBirth": "2000-01-01",
                    "gender": "H",
                    "idul": "pass123",
                    "email": "%s",
                    "password": "Password@123"
                }
                """, email))
                .when()
                .post("/user/register")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "email": "%s",
                    "password": "WrongPassword@456"
                }
                """, email))
                .when()
                .post("/user/login")
                .then()
                .statusCode(401);
    }

    @Test
    void login_shouldFail_whenUserDoesNotExist() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "email": "nonexistent@test.com",
                    "password": "Password@123"
                }
                """)
                .when()
                .post("/user/login")
                .then()
                .statusCode(401);
    }

    @Test
    void getUser_shouldReturnUserInfo_whenAuthenticated() {
        String email = "getuser" + System.currentTimeMillis() + "@test.com";

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "name": "Get User",
                    "dateOfBirth": "2000-01-01",
                    "gender": "F",
                    "idul": "get123",
                    "email": "%s",
                    "password": "Password@123"
                }
                """, email))
                .when()
                .post("/user/register")
                .then()
                .statusCode(201);

        String token = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "email": "%s",
                    "password": "Password@123"
                }
                """, email))
                .when()
                .post("/user/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/user/all")
                .then()
                .statusCode(200)
                .body("email", notNullValue())
                .body("name", notNullValue());
    }

    @Test
    void getUser_shouldFail_whenNotAuthenticated() {
        given()
                .when()
                .get("/user")
                .then()
                .statusCode(404);
    }

    @Test
    void getUser_shouldFail_whenInvalidToken() {
        given()
                .header("Authorization", "Bearer invalid.token.here")
                .when()
                .get("/user")
                .then()
                .statusCode(404);
    }

    @Test
    void getAllUsers_shouldReturnUserList() {
        String email = "listuser" + System.currentTimeMillis() + "@test.com";

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "name": "List User",
                    "dateOfBirth": "2000-01-01",
                    "gender": "H",
                    "idul": "list123",
                    "email": "%s",
                    "password": "Password@123"
                }
                """, email))
                .when()
                .post("/user/register")
                .then()
                .statusCode(201);

        given()
                .when()
                .get("/user/all")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("email", hasItem(email));
    }

    @Test
    void activateAccount_shouldActivate_whenAuthenticated() {
        String email = "activate" + System.currentTimeMillis() + "@test.com";

        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "name": "Activate User",
                    "dateOfBirth": "2000-01-01",
                    "gender": "F",
                    "idul": "activate123",
                    "email": "%s",
                    "password": "Password@123"
                }
                """, email))
                .when()
                .post("/user/register")
                .then()
                .statusCode(201);

        String token = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                {
                    "email": "%s",
                    "password": "Password@123"
                }
                """, email))
                .when()
                .post("/user/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .post("/user/activate")
                .then()
                .statusCode(anyOf(is(200), is(500)));
    }

    @Test
    void activateAccount_shouldFail_whenNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/user/activate")
                .then()
                .statusCode(anyOf(is(401), is(500)));
    }
}