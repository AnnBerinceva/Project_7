import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GettingListOrders {
    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";
    private StepCourier stepCourier;
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        stepCourier = new StepCourier();
    }
    @Step("Получение списка заказов.")
    private Response gettingListOrders(){
        return given()
                .header("Content-type", "application/json")
                .get("/api/v1/orders")
                .then()
                .extract()
                .response(); }
    @Test
    @Step("Получение списка заказов.")
    public void checkOrderList() {
        Response response = gettingListOrders();
        response.then().statusCode(200);
        String orders = response.path("orders").toString();
        MatcherAssert.assertThat(orders, notNullValue()); }
}
