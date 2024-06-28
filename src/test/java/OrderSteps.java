import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;

import static io.restassured.RestAssured.given;

public class OrderSteps {
    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";
    @Before
    public void setUp() { RestAssured.baseURI = BASE_URL; }

    public Response gettingListOrders(){
        return given()
                .header("Content-type", "application/json")
                .get("/api/v1/orders")
                .then()
                .extract()
                .response(); }

    public Response creatingOrder(CreatingOrder creatingOrder) {
        return given()
                .header("Content-type", "application/json")
                .body(creatingOrder)
                .post("/api/v1/orders")
                .then()
                .extract()
                .response(); }
}
