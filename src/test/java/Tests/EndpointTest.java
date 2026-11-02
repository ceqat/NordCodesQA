package Tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@DisplayName("Тестирование основного эндпоинта приложения (/endpoint)")
public class EndpointTest extends BaseTest {

    // Тесты action=LOGIN и просто /endpoint

    @Test
    @DisplayName("Успешная авторизация пользователя с валидным токеном")
    void loginActionCode200Test() {
        steps
                .doLogin(token)
                .verifySuccess200();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123456789012345678901234567890123",
            "1234567890123456789012345678901",
            "GHIJK1234567890123456789012345",
            "!@#$%^&*!@#$%^&*!@#$%^&*!@#$%^&*",
            ""
    })
    @DisplayName("Ошибка 400 при авторизации с невалидным форматом токена")
    void loginActionCode400ParameterizedInvalidTokenTest(String invalidValue) {
        steps
                .doLogin(invalidValue)
                .verifyClientError400("token: должно соответствовать \"^[0-9A-F]{32}$\"");
    }

    @Test
    @DisplayName("Ошибка 400 при авторизации без передачи токена")
    void loginActionCode400WithoutTokenTest() {
        steps
                .postWithoutToken("LOGIN")
                .verifyClientError400("token: не должно равняться null");
    }

    @Test
    @DisplayName("Ошибка 400 при передаче невалидного action")
    void invalidActionValueTest() {
        steps
                .postMethod(token, "TEST")
                .verifyClientError400("action: invalid action 'TEST'. Allowed: LOGIN, LOGOUT, ACTION");
    }

    @Test
    @DisplayName("Ошибка 500 при попытке авторизации через некорректный HTTP-метод GET")
    void loginActionCode500InvalidGetMethodTest() {
        steps
                .getMethod(token, "LOGIN")
                .verifyInternalServerError500();
    }

    @Test
    @DisplayName("Ошибка 500 при попытке авторизации через некорректный HTTP-метод PUT")
    void loginActionCode500InvalidPutMethodTest() {
        steps
                .putMethod(token, "LOGIN")
                .verifyInternalServerError500();
    }

    @Test
    @DisplayName("Ошибка 500 при попытке авторизации через некорректный HTTP-метод DELETE")
    void loginActionCode500InvalidDeleteMethodTest() {
        steps
                .deleteMethod(token, "LOGIN")
                .verifyInternalServerError500();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "qazWSXedC",
            "qazwSXedc",
            ""
    })
    @DisplayName("Ошибка 401 при авторизации с некорректным X-Api-Key")
    void loginActionCode401XApiKeyInvalidTest(String invalidValue) {
        steps
                .postInvalidXApiKey(invalidValue, token, "LOGIN")
                .verifyInvalidApiKey401();
    }

    @Test
    @DisplayName("Ошибка 500 недоступности /auth")
    void loginActionCode500AuthIsDeadTest() {
        wireMock.stubFor(post(urlEqualTo("/auth"))
                .willReturn(aResponse()
                        .withStatus(500)));

        steps
                .doLogin(token)
                .verifyInternalServerError500();
    }

    // Тесты action=LOGOUT

    @Test
    @DisplayName("Е2Е авторизация -> действие -> логаут")
    void logoutActionCode200Test() {
        steps
                .doLogin(token)
                .verifySuccess200()
                .doAction(token)
                .verifySuccess200()
                .doLogout(token)
                .verifySuccess200();
    }

    @Test
    @DisplayName("Ошибка 403 при попытке выполнить действие после удаления токена (логаута)")
    void logoutActionCode403TokenDeletedTest() {
        steps
                .doLogin(token)
                .verifySuccess200()
                .doAction(token)
                .verifySuccess200()
                .doLogout(token)
                .verifySuccess200()
                .doAction(token)
                .verifyTokenNotFound403(token);
    }

    @Test
    @DisplayName("Ошибка 403 при попытке логаута с неохраненным токеном")
    void logoutActionCode403TokenNotFoundTest() {
        steps
                .doLogout(token)
                .verifyTokenNotFound403(token);
    }

    // Тесты action=ACTION

    @Test
    @DisplayName("Успешное выполнение действия авторизованным пользователем")
    void actionActionCode200Test() {
        steps
                .doLogin(token)
                .verifySuccess200()
                .doAction(token)
                .verifySuccess200();
    }

    @Test
    @DisplayName("Ошибка 403 при попытке выполнить действие без авторизации")
    void actionActionCode403TokenNotFoundTest() {
        steps
                .doAction(token)
                .verifyTokenNotFound403(token);
    }

    @Test
    @DisplayName("Ошибка 500 при недоступности /doAction")
    void actionActionCode500doActionIsDeadTest() {
        steps
                .doLogin(token)
                .verifySuccess200();

        wireMock.stubFor(post(urlEqualTo("/doAction"))
                .willReturn(aResponse()
                        .withStatus(500)));

        steps
                .doAction(token)
                .verifyInternalServerError500();
    }
}
