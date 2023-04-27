package cart.controller;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HomeControllerTest {

    @DisplayName("GET / 요청 시 Status OK 및 HTML 반환")
    @Test
    void shouldResponseHtmlWithStatusOkWhenRequestGetHome() {
        given().log().all()
                .when()
                .get("/")
                .then().log().all()
                .contentType(ContentType.HTML)
                .statusCode(HttpStatus.SC_OK);
    }
}
