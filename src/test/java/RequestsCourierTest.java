import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

import static io.restassured.RestAssured.given;

public class RequestsCourierTest {
    private Courier courier;
    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Step("Новый курьер с введенными данными")
    private Courier newCourier(String login, String password, String firstName) {
        return new Courier(login, password, firstName);
    }

    @Step("1. /api/v1/courier")
    private Response requestNewCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier")
                .then()
                .extract()
                .response();
    }

    @Step("успешный запрос возвращает ok: true")
    private void requestSuccessfully(Response response) {
        response.then().statusCode(201);
        Boolean ok = response.path("ok");
        MatcherAssert.assertThat(ok, equalTo(true));
    }

    @Step("проверка ошибки 400")
    private void error400(Response response, int expectedStatusCode, String expectedMessage) {
        response.then().statusCode(expectedStatusCode);
        String message = response.path("message");
        MatcherAssert.assertThat(message, equalTo(expectedMessage));
    }


    @Test
    @DisplayName("курьера можно создать")
    public void creatingCourier() {
        courier = newCourier("08_Test15_ninja", "1234", "saske");
        Response response = requestNewCourier(courier);
        requestSuccessfully(response);
    }

    @Test
    @DisplayName("курьера нельзя создать без логина")
    public void creatingWithoutLogin() {
        courier = newCourier(null, "1234", "saske");
        Response response = requestNewCourier(courier);
        error400(response, 400, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @Step("если какого-то поля нет, запрос возвращает ошибку")
    public void creatingWithoutPassword() {
        courier = newCourier("ninja" + System.currentTimeMillis(), null, "test");
        Response response = requestNewCourier(courier);
        error400(response, 400, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @Step("если создать пользователя с логином, который уже есть, возвращается ошибка")
    public void createCourierWithDuplicateLogin() {
        String login = "ninja" + System.currentTimeMillis();
        courier = newCourier(login, "1234", "test");
        requestNewCourier(courier);

        Courier duplicateCourier = newCourier(login, "1234", "test");
        Response response = requestNewCourier(duplicateCourier);
        error400(response, 409, "Этот логин уже используется. Попробуйте другой.");
    }

    @Step("апи возвращает айди номер курьера")
    private Integer courierIdNumber(Courier courier) {
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

    @Step("удаление курьера")
    private void courierDelete(Integer courierId) {
        if (courierId != null) {
            given()
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200);
        }
    }

    @Test
    public void deleteCourier() {
        if (courier != null) {
            try {
                Integer courierId = courierIdNumber(courier);
                courierDelete(courierId);
            } catch (Exception exception) {
                System.out.println("Ошибка удаления курьера: " + exception.getMessage());
            }
        }
    }
}