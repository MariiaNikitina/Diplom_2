package com.ya;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserDataChangeTest {
    User user;
    UserClient userClient;
    String accessToken;
    String updatedData;
    String expected;
    ValidatableResponse createResponse;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserDataGenerator.getRandomData();
        createResponse = userClient.create(user);
    }

    @DisplayName("authorised User Can Update Name")
    @Description("Check update name returns new name and status code 200-OK when user is authorised")
    @Test
    public void authorisedUserCanUpdateName() {
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(user.getLogin(), user.getPassword()));
        accessToken = loginResponse.extract().path("accessToken");
        expected = "name" + user.getName();
        user.setName(expected);
        ValidatableResponse updateResponse = userClient.update(user, accessToken);
        int statusCode = updateResponse.extract().statusCode();
        boolean isSuccess = updateResponse.extract().path("success");
        updatedData = updateResponse.extract().path("user.name");
        assertThat("User cannot update name. Credentials are valid", statusCode, equalTo(SC_OK));
        assertThat("Returned success is false", isSuccess, equalTo(true));
        assertThat("Returned name isn't equal to updated", updatedData, equalTo(expected));
    }

    @DisplayName("authorised User Can Update Email")
    @Description("Check update email returns new email and status code 200-OK when user is authorised")
    @Test
    public void authorisedUserCanUpdateEmail() {
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(user.getLogin(), user.getPassword()));
        accessToken = loginResponse.extract().path("accessToken");
        expected = "email" + user.getLogin();
        user.setLogin(expected);
        ValidatableResponse updateResponse = userClient.update(user, accessToken);
        boolean isSuccess = updateResponse.extract().path("success");
        int statusCode = updateResponse.extract().statusCode();
        updatedData = updateResponse.extract().body().path("user.email");
        assertThat("User cannot update name. Credentials are valid", statusCode, equalTo(SC_OK));
        assertThat("Returned success is false", isSuccess, equalTo(true));
        assertThat("Returned email isn't equal to updated", updatedData, equalTo(expected));
    }

    @DisplayName("unauthorised User Cannot Update Email")
    @Description("Check update email returns status code 401-unauthorized and error message when user is unauthorised")
    @Test
    public void unauthorisedUserCannotUpdateEmail() {
        accessToken = createResponse.extract().path("accessToken");
        user.setLogin("email" + user.getLogin());
        ValidatableResponse updateResponse = userClient.update(user, "");
        int statusCode = updateResponse.extract().statusCode();
        boolean isSuccess = updateResponse.extract().path("success");
        String errorUpdateMessage = updateResponse.extract().body().path("message");
        assertThat("User can update email without authorization.", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Returned success is false", isSuccess, equalTo(false));
        assertThat("Returned error message isn't equal to expected", errorUpdateMessage, equalTo("You should be authorised"));
    }

    @DisplayName("unauthorised User Cannot Update Name")
    @Description("Check update name returns status code 401-unauthorized and error message when user is unauthorised")
    @Test
    public void unauthorisedUserCannotUpdateName() {
        accessToken = createResponse.extract().path("accessToken");
        user.setLogin("name" + user.getLogin());
        ValidatableResponse updateResponse = userClient.update(user, "");
        int statusCode = updateResponse.extract().statusCode();
        boolean isSuccess = updateResponse.extract().path("success");
        String errorUpdateMessage = updateResponse.extract().body().path("message");
        assertThat("User can update name without authorization.", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Returned success is false", isSuccess, equalTo(false));
        assertThat("Returned error message isn't equal to expected", errorUpdateMessage, equalTo("You should be authorised"));
    }

    @After
    public void tearDown() {
        try {
            userClient.delete(accessToken);
        } catch (NullPointerException ex) {
            System.out.println("Cannot delete user because accessToken isn't found");
        }
    }

}
