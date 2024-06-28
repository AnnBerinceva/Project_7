import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;

public class GettingListOrders {
    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";
    private OrderSteps orderSteps;
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        orderSteps = new OrderSteps();
    }
    @Test
    @DisplayName("Получение списка заказов")
    public void checkOrderList() {
        Response response = orderSteps.gettingListOrders();
        response.then().statusCode(200);
        String orders = response.path("orders").toString();
        MatcherAssert.assertThat(orders, notNullValue()); }
}
