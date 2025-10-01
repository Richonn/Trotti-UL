//package ca.ulaval.glo4003.ws;
//
//import ca.ulaval.glo4003.TelephonyWsMain;
//import io.restassured.RestAssured;
//import io.restassured.filter.log.RequestLoggingFilter;
//import io.restassured.filter.log.ResponseLoggingFilter;
//import org.hamcrest.Matchers;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import static io.restassured.RestAssured.get;
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.hasItem;
//
//public class ContactResourceIT {
//  private static Thread t;
//
//  @BeforeAll
//  public static void setUp() {
//    RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
//
//    t = new Thread(() -> {
//      try {
//        TelephonyWsMain.main(new String[]{});
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    });
//    t.setDaemon(true);
//    t.start();
//  }
//
//  @AfterAll
//  public static void tearDown() {
//    t.interrupt();
//  }
//
//  @Test
//  public void givenContacts_whenGetAllContacts_thenContactsReturned() {
//    given()
//      .get("/api/telephony/contacts")
//      .then()
//      .body("name", hasItem("Steve Jobs"));
//  }
//}
