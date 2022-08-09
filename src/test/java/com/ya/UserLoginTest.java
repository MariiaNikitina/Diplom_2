package com.ya;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

public class UserLoginTest {
    User user;
    UserClient userClient;
    String refreshToken;
    String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserDataGenerator.getRandomData();
        ValidatableResponse createResponse =userClient.create(user);
        accessToken=createResponse.extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (refreshToken != null) userClient.logout(refreshToken);
        if (accessToken!=null) userClient.delete(accessToken);
    }

    @Test
    @DisplayName("User Can Login With Valid Credentials")
    @Description("Checking user's refresh token and status code of response corresponds to successful login of user")
    public void userCanLoginWithValidCredentials() {
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(user.getLogin(), user.getPassword()));
        int statusCode = loginResponse.extract().statusCode();
        refreshToken = loginResponse.extract().path("refreshToken");
        assertThat("User cannot login. Credentials are valid", statusCode, equalTo(SC_OK));
        assertNotNull("User refresh token is incorrect", refreshToken);
        assertThat("accessToken doesn't start with \"Bearer\"",loginResponse.extract().path("accessToken"), startsWith("Bearer"));
        assertThat("Returned login isn't equal to user login",loginResponse.extract().path("user.email"), equalTo(user.getLogin()));
        assertThat("Returned name isn't equal to user name",loginResponse.extract().path("user.name"), equalTo(user.getName()));
    }

    @Test
    @DisplayName("User Cannot Login With Incorrect Password")
    @Description("Checking User login returns Not Found status code when pair of login and password isn't exist")
    public void userCannotLoginWithIncorrectPassword() {
        String incorrectPassword = "incorrectPassword";
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(user.getLogin(), incorrectPassword));
        int statusCode = loginResponse.extract().statusCode();
        assertThat("User can login with non exist pair login-password ", statusCode, equalTo(SC_UNAUTHORIZED));
        String message = loginResponse.extract().path("message");
        assertThat("Warning message isn't as expected", message, equalTo("email or password are incorrect"));
    }
    @Test
    @DisplayName("User Cannot Login With Incorrect Login")
    @Description("Checking User login returns Not Found status code when pair of login and password isn't exist")
    public void userCannotLoginWithIncorrectLogin() {
        String incorrectLogin = "incorrectLogin@mail.ru";
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(user.getPassword(),incorrectLogin));
        int statusCode = loginResponse.extract().statusCode();
        assertThat("User can login with non exist pair login-password ", statusCode, equalTo(SC_UNAUTHORIZED));
        String message = loginResponse.extract().path("message");
        assertThat("Warning message isn't as expected", message, equalTo("email or password are incorrect"));
    }
}
