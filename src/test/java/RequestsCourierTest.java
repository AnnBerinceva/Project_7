import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;

public class RequestsCourierTest {
    private Courier courier;
    private StepCourier stepCourier;
    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";
    private static final String BASE_CREATE_COURIER = "/api/v1/courier";

    @Before
    public void setUp() { RestAssured.baseURI = BASE_URL;
        stepCourier = new StepCourier(); }

    @Test
    @DisplayName("курьера можно создать")
    public void creatingCourier() {
        courier = stepCourier.newCourier("001Test15_ninja", "1234", "saske");
        Response response = stepCourier.requestNewCourier(courier);
        stepCourier.requestSuccessfully(response); }

    @Test
    @DisplayName("курьера нельзя создать без логина")
    public void creatingWithoutLogin() {
        courier = stepCourier.newCourier(null, "1234", "saske");
        Response response = stepCourier.requestNewCourier(courier);
        stepCourier.error400(response, 400, "Недостаточно данных для создания учетной записи"); }

    @Test
    @DisplayName("если какого-то поля нет, запрос возвращает ошибку")
    public void creatingWithoutPassword() {
        courier = stepCourier.newCourier("ninja" + System.currentTimeMillis(), null, "test");
        Response response = stepCourier.requestNewCourier(courier);
        stepCourier.error400(response, 400, "Недостаточно данных для создания учетной записи"); }

    @Test
    @Step("если создать пользователя с логином, который уже есть, возвращается ошибка")
    public void createCourierWithDuplicateLogin() {
        String login = "ninja" + System.currentTimeMillis();
        courier = stepCourier.newCourier(login, "1234", "test");
        stepCourier.requestNewCourier(courier);

        Courier duplicateCourier = stepCourier.newCourier(login, "1234", "test");
        Response response = stepCourier.requestNewCourier(duplicateCourier);
        stepCourier.error400(response, 409, "Этот логин уже используется. Попробуйте другой."); }

    @Test
    public void deleteCourier() {
        if (courier != null) {
            try {
                Integer courierId = stepCourier.courierIdNumber(courier);
                stepCourier.courierDelete(courierId);
            } catch (Exception exception) {
                System.out.println("Ошибка удаления курьера: " + exception.getMessage());  }  } }

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
