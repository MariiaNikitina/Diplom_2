package com.ya;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertNotNull;

public class GetUserOrderTest {
    UserClient userClient;
    User user;
    ValidatableResponse createResponse;
    OrderClient orderClient = new OrderClient();
    String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserDataGenerator.getRandomData();
        createResponse = userClient.create(user);
        accessToken = createResponse.extract().path("accessToken");
    }

    @After
    public void tearDown() {
        try {
            userClient.delete(accessToken);
        } catch (NullPointerException ex) {
            System.out.println("Cannot delete user because accessToken isn't found");
        }
    }

    @DisplayName("get Order by Authorized User One Order")
    @Description("Checking status code is 200-OK and success parameter is true, getUserOrder returns some id and name and ingredients that were sent when user is authorised")
    @Test
    public void getOrderAuthorizedUserOneOrder() {
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(user.getLogin(), user.getPassword()));
        String[] ingredients = new OrderIngredientsGenerator().getCorrectIngredients();
        orderClient.makeOrder(ingredients,
                loginResponse.extract().path("accessToken"));
        ValidatableResponse getUserOrderResponse = orderClient.getUserOrder(loginResponse.extract().path("accessToken"));
        int statusCode = getUserOrderResponse.extract().statusCode();
        assertThat("Status code isn't equal to 200-OK", statusCode, equalTo(SC_OK));
        assertThat("getUserOrder returns success=false", getUserOrderResponse.extract().path("success"), equalTo(true));
        assertThat("getUserOrder returns incorrect ingredients of order", getUserOrderResponse.extract().path("orders.ingredients[0]"), equalTo(new ArrayList<>(Arrays.asList(ingredients))));
        assertNotNull("The order's id is empty", getUserOrderResponse.extract().path("orders.id"));
        assertNotNull("The order's name is empty", getUserOrderResponse.extract().path("orders.name"));
    }

    @DisplayName("get Order by Authorized User Without Order")
    @Description("Checking status code is 200-OK and success parameter is true, getUserOrder returns empty orders")
    @Test
    public void getOrderAuthorizedUserWithoutOrder() {
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(user.getLogin(), user.getPassword()));
        ValidatableResponse getUserOrderResponse = orderClient.getUserOrder(loginResponse.extract().path("accessToken"));
        int statusCode = getUserOrderResponse.extract().statusCode();
        assertThat("Status code isn't equal to 200-OK", statusCode, equalTo(SC_OK));
        assertThat("getUserOrder returns success=false", getUserOrderResponse.extract().path("success"), equalTo(true));
        assertThat("GetUserOrder returns order to user who hasn't made the order", getUserOrderResponse.extract().path("orders"), (empty()));
    }

    @DisplayName("get Order by Unauthorized User One Order")
    @Description("Checking status code is 401-Unauthorised and success parameter is false. The returned message is equal to expected ")
    @Test
    public void getOrderUnauthorizedUserOneOrder() {
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(user.getLogin(), user.getPassword()));
        String[] ingredients = new OrderIngredientsGenerator().getCorrectIngredients();
        orderClient.makeOrder(ingredients,
                loginResponse.extract().path("accessToken"));
        userClient.logout(loginResponse.extract().path("refreshToken"));
        ValidatableResponse getUserOrderResponse = orderClient.getUserOrder("");
        int statusCode = getUserOrderResponse.extract().statusCode();
        assertThat("Status code isn't equal to 401-Unauthorized", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("getUserOrder returns success=true", getUserOrderResponse.extract().path("success"), equalTo(false));
        String expectedMessage = "You should be authorised";
        assertThat("Returned message isn't equal to expected", getUserOrderResponse.extract().path("message"), equalTo(expectedMessage));
    }

    @DisplayName("get Order by Authorized User Few Orders")
    @Description("Checking status code is 200-OK and success parameter is true, getUserOrder returns correct number of orders and correct sets of ingredients when user is authorised")
    @Test
    public void getOrderAuthorizedUserFewOrders() {
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(user.getLogin(), user.getPassword()));
        String[] ingredients = new OrderIngredientsGenerator().getCorrectIngredients();
        String[] alternativeIngredients = new OrderIngredientsGenerator().getAlternativeCorrectIngredients();
        orderClient.makeOrder(ingredients,
                loginResponse.extract().path("accessToken"));
        orderClient.makeOrder(alternativeIngredients,
                loginResponse.extract().path("accessToken"));
        ValidatableResponse getUserOrderResponse = orderClient.getUserOrder(loginResponse.extract().path("accessToken"));
        int statusCode = getUserOrderResponse.extract().statusCode();
        assertThat("Status code isn't equal to 200-OK", statusCode, equalTo(SC_OK));
        assertThat("getUserOrder returns success=false", getUserOrderResponse.extract().path("success"), equalTo(true));
        assertThat("getUserOrder returns incorrect ingredients of order", getUserOrderResponse.extract().path("orders.ingredients[0]"), equalTo(new ArrayList<>(Arrays.asList(ingredients))));
        assertThat("getUserOrder returns incorrect ingredients of order", getUserOrderResponse.extract().path("orders.ingredients[1]"), equalTo(new ArrayList<>(Arrays.asList(alternativeIngredients))));
    }
}
