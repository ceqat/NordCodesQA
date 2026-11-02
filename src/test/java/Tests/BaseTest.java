package Tests;

import Steps.EndpointSteps;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.config.HeaderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static Utils.Generator.generateRandomToken;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;

public class BaseTest {
    public static WireMockServer wireMock;
    private static RequestSpecification requestSpec;
    protected EndpointSteps steps;
    protected String token;

    @BeforeAll
    static void setupSpec() {
        RestAssuredConfig restAssuredConfig = RestAssured.config().headerConfig(HeaderConfig.headerConfig().overwriteHeadersWithName("X-Api-Key"));

        requestSpec =
                given()
                        .baseUri("http://localhost:8080")
                        .contentType("application/x-www-form-urlencoded")
                        .accept("application/json")
                        .header("X-Api-Key", "qazWSXedc")
                        .config(restAssuredConfig);
        wireMock = new WireMockServer(8888);

        wireMock.start();
    }

    @BeforeEach
    void setup() {
        steps = new EndpointSteps(requestSpec);

        wireMock.stubFor(post(urlEqualTo("/auth"))
                .willReturn(aResponse()
                        .withStatus(200)));


        wireMock.stubFor(post(urlEqualTo("/doAction"))
                .willReturn(aResponse()
                        .withStatus(200)));

        token = generateRandomToken();
    }

    @AfterAll
    static void tearDown() {
        if (wireMock != null && wireMock.isRunning()) {
            wireMock.stop();
        }
    }
}
