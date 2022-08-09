package com.ya;

import org.apache.commons.lang3.RandomStringUtils;

public class UserDataGenerator {

    public static User getRandomData() {
        String userLogin = (RandomStringUtils.randomAlphabetic(7) + "@mail.ru").toLowerCase();
        String userPassword = RandomStringUtils.randomAlphabetic(10).toLowerCase();
        String userName = RandomStringUtils.randomAlphabetic(10).toLowerCase();
        return new User(userLogin, userPassword, userName);
    }

    public static User getDataWithNullName() {
        String userLogin = RandomStringUtils.randomAlphabetic(7) + "@mail.ru";
        String userPassword = RandomStringUtils.randomAlphabetic(10);
        String userName = null;
        return new User(userLogin, userPassword, userName);
    }

    public static User getDataWithNullLogin() {
        String userLogin = null;
        String userPassword = RandomStringUtils.randomAlphabetic(10);
        String userName = RandomStringUtils.randomAlphabetic(10);
        return new User(userLogin, userPassword, userName);
    }

    public static User getDataWithNullPassword() {
        String userLogin = RandomStringUtils.randomAlphabetic(7) + "@mail.ru";
        String userPassword = null;
        String userName = RandomStringUtils.randomAlphabetic(10);
        return new User(userLogin, userPassword, userName);
    }
}
