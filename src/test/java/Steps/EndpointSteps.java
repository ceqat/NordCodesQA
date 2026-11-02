package Steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class EndpointSteps {
    private RequestSpecification requestSpec;
    private Response response;

    public EndpointSteps(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    @Step("Отправка запроса на авторизацию LOGIN")
    public EndpointSteps doLogin(String token) {
        response = given().spec(requestSpec)
                .header("token", token)
                .header("action", "LOGIN")
                .log().all()
                .when()
                .post("/endpoint");
        return this;
    }

    @Step("Отправка запроса на действие ACTION")
    public EndpointSteps doAction(String token) {
        response = given().spec(requestSpec)
                .header("token", token)
                .header("action", "ACTION")
                .log().all()
                .when()
                .post("/endpoint");
        return this;
    }

    @Step("Отправка запроса на выход из системы LOGOUT")
    public EndpointSteps doLogout(String token) {
        response = given().spec(requestSpec)
                .header("token", token)
                .header("action", "LOGOUT")
                .log().all()
                .when()
                .post("/endpoint");
        return this;
    }

    @Step("Отправка запроса без токена")
    public EndpointSteps postWithoutToken(String action) {
        response =
                given().spec(requestSpec)
                        .header("action", action)
                        .log().all()
                        .when()
                        .post("/endpoint");
        return this;
    }

    @Step("Отправка запроса с методом POST")
    public EndpointSteps postMethod(String token, String action) {
        response = given().spec(requestSpec)
                .header("token", token)
                .header("action", action)
                .log().all()
                .when()
                .post("/endpoint");
        return this;
    }

    @Step("Отправка запроса с методом GET")
    public EndpointSteps getMethod(String token, String action) {
        response = given().spec(requestSpec)
                .header("token", token)
                .header("action", action)
                .log().all()
                .when()
                .get("/endpoint");
        return this;
    }

    @Step("Отправка запроса с методом PUT")
    public EndpointSteps putMethod(String token, String action) {
        response = given().spec(requestSpec)
                .header("token", token)
                .header("action", action)
                .log().all()
                .when()
                .put("/endpoint");
        return this;
    }

    @Step("Отправка запроса с методом DELETE")
    public EndpointSteps deleteMethod(String token, String action) {
        response = given().spec(requestSpec)
                .header("token", token)
                .header("action", action)
                .log().all()
                .when()
                .delete("/endpoint");
        return this;
    }

    @Step("Отправка запроса с невалидным X-Api-Key")
    public EndpointSteps postInvalidXApiKey(String xApiKey, String token, String action) {
        response = given().spec(requestSpec)
                .header("X-Api-Key", xApiKey)
                .header("token", token)
                .header("action", action)
                .log().all()
                .when()
                .post("/endpoint");
        return this;
    }

    @Step("Проверка ответа с кодом 200")
    public EndpointSteps verifySuccess200() {
        response
                .then()
                .log().all()
                .body("result", equalTo("OK"))
                .statusCode(200);
        return this;
    }

    @Step("Проверка ответа с кодом 500")
    public EndpointSteps verifyInternalServerError500() {
        response
                .then()
                .log().all()
                .body("result", equalTo("ERROR"))
                .body("message", equalTo("Internal Server Error"))
                .statusCode(500);
        return this;
    }

    @Step("Проверка ответа с кодом 400")
    public EndpointSteps verifyClientError400(String message) {
        response
                .then()
                .log().all()
                .body("result", equalTo("ERROR"))
                .body("message", equalTo(message))
                .statusCode(400);
        return this;
    }

    @Step("Проверка ответа с кодом 404")
    public EndpointSteps verifyTokenNotFound403(String token) {
        response
                .then()
                .log().all()
                .body("result", equalTo("ERROR"))
                .body("message", equalTo("Token '" + token + "' not found"))
                .statusCode(403);
        return this;
    }

    @Step("Проверка ответа с кодом 401")
    public EndpointSteps verifyInvalidApiKey401() {
        response
                .then()
                .log().all()
                .body("result", equalTo("ERROR"))
                .body("message", equalTo("Missing or invalid API Key"))
                .statusCode(401);
        return this;
    }
}
