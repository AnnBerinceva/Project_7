import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.Before;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class StepCourier {
    private Courier courier;
    private static final String BASE_CREATE_COURIER = "/api/v1/courier";
    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";
    public void setUp() { RestAssured.baseURI = BASE_URL; }

    public Courier newCourier(String login, String password, String firstName) {
        return new Courier(login, password, firstName);
    }

    public void creatingCourier(String login, String password, String firstName) {
        Courier courier = new Courier(login, password, firstName);

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .post(BASE_CREATE_COURIER)
                .then()
                .extract()
                .response();
    }

    public Response courierLogin(String login, String password) {
        LoginCourierInSystem loginCourierInSystem = new LoginCourierInSystem(login, password);

        return given()
                .header("Content-type", "application/json")
                .body(loginCourierInSystem)
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .response();
    }

    public Integer courierIdNumber(Courier courier) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .response();
        if (response.statusCode() == 200) {
            return response.path("id");
        } else {
            return null;
        }
    }

    public void courierDelete(Integer courierId) {
        if (courierId != null) {
            given()
                    .delete(BASE_CREATE_COURIER + courierId)
                    .then()
                    .statusCode(200);
        }
    }
    public void requestSuccessfully(Response response) {
        response.then().statusCode(201);
        Boolean ok = response.path("ok");
        MatcherAssert.assertThat(ok, equalTo(true));
    }

    public void error400(Response response, int expectedStatusCode, String expectedMessage) {
        response.then().statusCode(expectedStatusCode);
        String message = response.path("message");
        MatcherAssert.assertThat(message, equalTo(expectedMessage));
    }

    public Response requestNewCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post(BASE_CREATE_COURIER)
                .then()
                .extract()
                .response();
    }
}
