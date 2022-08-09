package com.ya;

public class OrderIngredientsGenerator {
    String ingredient;

    public String[] getCorrectIngredients() {
        String[] ingredients = new String[]{"61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa73"};
        return ingredients;
    }

    public String[] getAlternativeCorrectIngredients() {
        String[] ingredients = new String[]{"61c0c5a71d1f82001bdaaa77", "61c0c5a71d1f82001bdaaa7a", "61c0c5a71d1f82001bdaaa74"};
        return ingredients;
    }

    public String[] getIncorrectIngredients() {
        String[] wrongIngredients = new String[]{"61c0c5a71d1f82001bdaaa7", "61c0c5a71d1f82001bdaaa6"};
        return wrongIngredients;
    }
}
