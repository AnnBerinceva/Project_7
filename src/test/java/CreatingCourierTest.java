import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreatingCourierTest {
    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";
    private static final String BASE_CREATE_COURIER = "/api/v1/courier";
    private Courier courier;
    private StepCourier stepCourier;
    @Before
    public void setUp() { RestAssured.baseURI = BASE_URL;
        stepCourier = new StepCourier(); }

    @Test
    @Description("курьера можно создать")
    public void successfulLogin() {
        String random = UUID.randomUUID().toString();
        stepCourier.creatingCourier("ninja" + random, "1234", "test");

        Response response = stepCourier.courierLogin("ninja" + random, "1234");
        response.then().statusCode(200);

        Integer id = response.path("id");
        MatcherAssert.assertThat(id, notNullValue()); }

    @Test
    @DisplayName("Запрос без логина или пароля")
    @Description("если одного из полей нет, запрос возвращает ошибку")
    public void requestWithoutLogin() {
        Response response = stepCourier.courierLogin(null, "1234");
        response.then().statusCode(400);

        String message = response.path("message");
        MatcherAssert.assertThat(message, equalTo("Недостаточно данных для входа")); }

    @Test
    @Description("Запрос с несуществующей парой логин-пароль")
    public void requestWithNonLoginPassword() {
        String random = UUID.randomUUID().toString();
        stepCourier.creatingCourier("ninja" + random, "1234", "test");

        Response response = stepCourier.courierLogin("non-existent login", "non-existent password");
        response.then().statusCode(404);

        String message = response.path("message");
        MatcherAssert.assertThat(message, equalTo("Учетная запись не найдена"));    }
    @After
    public void tearDown() {
        if (courier != null) {
            try {
                given()
                        .delete(BASE_CREATE_COURIER + courier)
                        .then()
                        .statusCode(404);
            } catch (Exception exception) {
                System.out.println("Ошибка при удалении курьера - " + exception.getMessage());  } } }
}