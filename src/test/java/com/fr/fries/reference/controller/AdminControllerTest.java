package com.fr.fries.reference.controller;

import com.fr.fries.reference.BaseTest;
import com.fr.fries.reference.common.Constants;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static com.fr.fries.reference.TestVariables.health_down;
import static com.fr.fries.reference.TestVariables.health_up;
import static org.hamcrest.Matchers.oneOf;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class AdminControllerTest extends BaseTest {

    @Test
    public void whenGetActuatorHealthCheck_thenSuccess() {
        request()
                .when().get("/actuator/health")
                .then().apply(print())
                .statusCode(oneOf(HttpStatus.OK.value(), HttpStatus.SERVICE_UNAVAILABLE.value()))
                .body(Constants.ATTR_STATUS, oneOf(health_up, health_down));
    }

    @Test
    public void whenGetHealthCheck_thenSuccess() {
        request()
                .when().get("/index.html")
                .then().apply(print())
                .apply(document("index"))
                .statusCode(oneOf(HttpStatus.OK.value(), HttpStatus.SERVICE_UNAVAILABLE.value()))
                .body(Constants.ATTR_STATUS, oneOf(health_up, health_down));
    }

    @Test
    public void whenPutHealthUp_thenSuccess() {
        request()
                .when().put("/health/up")
                .then().apply(print())
                .statusCode(HttpStatus.OK.value());

        request()
                .when().get("/index.html")
                .then().apply(print())
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void whenPutHealthDown_thenSuccess() {
        request()
                .when().put("/health/down")
                .then().apply(print())
                .statusCode(HttpStatus.OK.value());

        request()
                .when().get("/index.html")
                .then().apply(print())
                .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
    }

    @Test
    public void whenShutdown_thenSuccess() {
        request()
                .when().put("/health/shutdown")
                .then().apply(print())
                .statusCode(HttpStatus.OK.value());
    }
}
