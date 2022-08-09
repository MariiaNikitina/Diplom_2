package com.ya;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends StellarBurgersClient {
    private static final String USER_PATH = "api/auth/";

    @Step("Login with credentials{credentials}")
    public ValidatableResponse login(UserCredentials credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .post(USER_PATH + "login")
                .then();
    }

    @Step("Create user {user}")
    public ValidatableResponse create(User user) {
        String registerRequestBody = "{\"email\":\"" + user.getLogin() + "\","
                + "\"password\":\"" + user.getPassword() + "\","
                + "\"name\":\"" + user.getName() + "\"}";
        return given()
                .spec(getBaseSpec())
                .body(registerRequestBody)
                .when()
                .post(USER_PATH + "register")
                .then();
    }

    @Step("Refresh user {user}")
    public ValidatableResponse update(User user, String accessToken) {
        String requestBody = "{\"email\":\"" + user.getLogin() + "\","
                + "\"password\":\"" + user.getPassword() + "\","
                + "\"name\":\"" + user.getName() + "\"}";
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(requestBody)
                .when()
                .patch(USER_PATH + "user")
                .then();

    }

    @Step("Get user data {user}")
    public ValidatableResponse getUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .get(USER_PATH + "user")
                .then();

    }

    @Step("Delete user using token {refreshToken}")
    public ValidatableResponse delete(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .body(accessToken)
                .when()
                .delete(USER_PATH + "user")
                .then();
    }

    @Step("Delete user using token {refreshToken}")
    public ValidatableResponse logout(String refreshToken) {
        return given()
                .spec(getBaseSpec())
                .body(refreshToken)
                .when()
                .post(USER_PATH + "logout")
                .then();
    }
}
