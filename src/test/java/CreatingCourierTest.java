import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreatingCourierTest {
    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";

    @Before
    public void setUp() {  RestAssured.baseURI = BASE_URL; }

    @Step("1. Создание курьера")
    private void creatingCourier(String login, String password, String firstName) {
        Courier courier = new Courier(login, password, firstName);

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier")
                .then()
                .statusCode(201); }

    @Step("2. Логин курьера в системе")
    private Response courierLogin(String login, String password) {
        LoginCourierInSystem loginCourierInSystem = new LoginCourierInSystem(login, password);

        return given()
                .header("Content-type", "application/json")
                .body(loginCourierInSystem)
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .response();  }

    @Test
    @Description("курьера можно создать")
    @Step("Успешный логин")
    public void successfulLogin() {
        String random = UUID.randomUUID().toString();
        creatingCourier("ninja" + random, "1234", "test");

        Response response = courierLogin("ninja" + random, "1234");
        response.then().statusCode(200);

        Integer id = response.path("id");
        MatcherAssert.assertThat(id, notNullValue()); }


    @Test
    @DisplayName("Запрос без логина или пароля")
    @Description("если одного из полей нет, запрос возвращает ошибку")
    @Step("Логин null")
    public void requestWithoutLogin() {
        Response response = courierLogin(null, "1234");
        response.then().statusCode(400);

        String message = response.path("message");
        MatcherAssert.assertThat(message, equalTo("Недостаточно данных для входа")); }


    @Test
    @Description("Запрос с несуществующей парой логин-пароль")
    @Step("авторизация с несуществующей парой логин-пароль")
    public void requestWithNonLoginPassword() {
        String random = UUID.randomUUID().toString();
        creatingCourier("ninja" + random, "1234", "test");

        Response response = courierLogin("non-existent login", "non-existent password");
        response.then().statusCode(404);

        String message = response.path("message");
        MatcherAssert.assertThat(message, equalTo("Учетная запись не найдена")); }
}