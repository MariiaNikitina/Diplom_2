package com.ya;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends StellarBurgersClient {
    private static final String ORDER_PATH = "api/orders/";
    String orderBody;

    @Step("Make an order")
    public ValidatableResponse makeOrder(String[] ingredients, String accessToken) {
        if (ingredients == null) {
            orderBody = "{\"ingredients\": []}";
        } else {
            orderBody = "{\"ingredients\": [\"" + String.join("\", \"", ingredients);
            orderBody += "\"]}";
        }
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(orderBody)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Get order using user access token")
    public ValidatableResponse getUserOrder(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .get(ORDER_PATH)
                .then();
    }

}
