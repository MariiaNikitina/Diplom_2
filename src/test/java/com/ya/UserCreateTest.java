package com.ya;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

public class UserCreateTest {
    UserClient userClient;
    User user;
    String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Create User With Valid Data")
    @Description("Checking user creation returns Ok status code when data is valid")
    public void createUserWithValidData() {
        user = UserDataGenerator.getRandomData();
        ValidatableResponse createResponse = userClient.create(user);
        accessToken = createResponse.extract().path("accessToken");
        int statusCode = createResponse.extract().statusCode();
        assertThat("The user creation with valid data is failed", statusCode, equalTo(SC_OK));
        assertNotNull("Returned accessToken is null", accessToken);
        assertNotNull("Returned refreshToken is null", createResponse.extract().path("refreshToken"));
        assertThat("Returned login isn't equal to user login", createResponse.extract().path("user.email"), equalTo(user.getLogin()));
        assertThat("Returned name isn't equal to user name", createResponse.extract().path("user.name"), equalTo(user.getName()));
    }

    @Test
    @DisplayName("cannot Create Two Users With the Same Data")
    @Description("Checking user creation returns conflict when data is the same for two users")
    public void cannotCreateTwoUsersWithSameData() {
        user = UserDataGenerator.getRandomData();
        ValidatableResponse createResponse = userClient.create(user);
        ValidatableResponse createSecondResponse = userClient.create(user);
        accessToken = createResponse.extract().path("accessToken");
        int statusCode = createSecondResponse.extract().statusCode();
        assertThat("The second user creation with valid data doesn't send forbidden code", statusCode, equalTo(SC_FORBIDDEN));
        String message = createResponse.extract().path("message");
        assertThat("The error message isn't equal to expected", message, equalTo("User already exists"));
    }

    //Согласно спеке имя является обязательным полем, если оно не введено при создании, должен вернуться код 403(forbidden)
    @Test
    @DisplayName("cannot Create User With Null Name")
    @Description("Checking user creation returns Forbidden when  name is empty")
    public void cannotCreateUserWithNullName() {
        user = UserDataGenerator.getDataWithNullName();
        ValidatableResponse createResponse = userClient.create(user);
        accessToken = createResponse.extract().path("accessToken");
        int statusCode = createResponse.extract().statusCode();
        assertThat("The user creation without name doesn't send forbidden request", statusCode, equalTo(SC_FORBIDDEN));
        String message = createResponse.extract().path("message");
        assertThat("The error message isn't equal to expected", message, equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("cannot Create User With Null Login")
    @Description("Checking user creation returns Forbidden when  login is empty")
    public void cannotCreateUserWithNullLogin() {
        user = UserDataGenerator.getDataWithNullLogin();
        ValidatableResponse createResponse = userClient.create(user);
        accessToken = createResponse.extract().path("accessToken");
        int statusCode = createResponse.extract().statusCode();
        assertThat("The user creation without login doesn't send forbidden request", statusCode, equalTo(SC_FORBIDDEN));
        String message = createResponse.extract().path("message");
        assertThat("The error message isn't equal to expected", message, equalTo("Email, password and name are required fields"));
    }

    //Согласно спеке пароль является обязательным полем, если он не введен при создании, должен вернуться код 403(forbidden)
    @Test
    @DisplayName("cannot Create User With Null Password")
    @Description("Checking user creation returns Forbidden when  password is empty")
    public void cannotCreateUserWithNullPassword() {
        user = UserDataGenerator.getDataWithNullPassword();
        ValidatableResponse createResponse = userClient.create(user);
        accessToken = createResponse.extract().path("accessToken");
        int statusCode = createResponse.extract().statusCode();
        assertThat("The user creation without password doesn't send forbidden request", statusCode, equalTo(SC_FORBIDDEN));
        String message = createResponse.extract().path("message");
        assertThat("The error message isn't equal to expected", message, equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        if (accessToken != null)
            userClient.delete(accessToken);
    }
}
