package com.ya;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

public class OrderCreationTest {
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

    @DisplayName("Make Order With Correct Ingredients by Authorized User")
    @Description("Checking order creation returns name, number of order and Ok status code with existed ingredients with authorized user")
    @Test
    public void makeOrderWithCorrectIngredientsAuthorizedUser() {
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(user.getLogin(), user.getPassword()));
        ValidatableResponse makeOrderResponse = orderClient.makeOrder(new OrderIngredientsGenerator().getCorrectIngredients(),
                loginResponse.extract().path("accessToken"));
        int statusCode = makeOrderResponse.extract().statusCode();
        assertThat(makeOrderResponse.extract().path("success"), equalTo(true));
        assertNotNull(makeOrderResponse.extract().path("name"));
        assertNotNull(makeOrderResponse.extract().path("order.number"));
        assertThat(statusCode, equalTo(SC_OK));
    }

    @DisplayName("Cannot Make Order With Incorrect Ingredients by Authorized User")
    @Description("Checking order creation returns Internal Server Error with nonexistent ingredients")
    @Test
    public void cannotMakeOrderWithIncorrectIngredientsAuthorizedUser() {
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(user.getLogin(), user.getPassword()));
        ValidatableResponse makeOrderResponse = orderClient.makeOrder(new OrderIngredientsGenerator().getIncorrectIngredients(),
                loginResponse.extract().path("accessToken"));
        int statusCode = makeOrderResponse.extract().statusCode();
        assertThat(statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
    }

    @DisplayName("Cannot Make Order With Zero Ingredients by Authorized User")
    @Description("Checking order creation returns Bad Request and error message with empty field instead of ingredients")
    @Test
    public void cannotMakeOrderWithZeroIngredientsAuthorizedUser() {
        ValidatableResponse loginResponse = userClient.login(new UserCredentials(user.getLogin(), user.getPassword()));
        ValidatableResponse makeOrderResponse = orderClient.makeOrder(null, loginResponse.extract().path("accessToken"));
        int statusCode = makeOrderResponse.extract().statusCode();
        assertThat(makeOrderResponse.extract().path("success"), equalTo(false));
        assertThat(statusCode, equalTo(SC_BAD_REQUEST));
        assertThat(makeOrderResponse.extract().path("message"), equalTo("Ingredient ids must be provided"));
    }

    //В api документации указано, что неавторизованный пользователь не может сделать заказ. Однако на деле отправка запроса
    //на ручку создания заказа с пустым accessToken возвращает статус ОК и номер заказа

    @DisplayName("cannot Make Order With Correct Ingredients by Unauthorized User")
    @Description("Checking order creation returns status code 401- Unauthorised, parameter success is false when user isn't authorised")
    @Test
    public void cannotMakeOrderWithCorrectIngredientsUnauthorizedUser() {
        ValidatableResponse makeOrderResponse = orderClient.makeOrder(new OrderIngredientsGenerator().getCorrectIngredients(), "");
        int statusCode = makeOrderResponse.extract().statusCode();
        assertThat(makeOrderResponse.extract().path("success"), equalTo(false));
        assertThat(statusCode, equalTo(SC_UNAUTHORIZED));
    }
}
