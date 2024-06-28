import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.hamcrest.MatcherAssert;

import java.util.Arrays;
import java.util.Collection;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
@RunWith(Parameterized.class)
public class OrderTest {
    @Parameterized.Parameters
    public static Collection<Object[]> list(){
        return Arrays.asList(new Object[][]{
                {new CreatingOrder("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", new String[]{"BLACK"})},
                {new CreatingOrder("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", new String[]{"GREY"})},
                {new CreatingOrder("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", new String[]{"BLACK", "GREY"})},
                {new CreatingOrder("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", new String[]{})}
        });
    }
    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";
    private final CreatingOrder creatingOrder;
    private OrderSteps orderSteps;

    public OrderTest(CreatingOrder creatingOrder) { this.creatingOrder = creatingOrder; }
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        orderSteps = new OrderSteps();   }
    @Test
    public void createAnOrder() {
        Response response = orderSteps.creatingOrder(creatingOrder);
        response.then().statusCode(201);
        Integer track = response.path("track");
        MatcherAssert.assertThat(track, notNullValue());  }
}