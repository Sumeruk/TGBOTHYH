package org.example.bot;


import lombok.Getter;
import org.example.ReadConfig;
import org.example.entity.Product;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Getter
public class KeyboardFactoryFromFile extends KeyboardFactory{
    private List<Product> foods;
    private List<Product> drinks;

    public void setFoods(List<Product> foods) {
        this.foods = foods;
    }

    public void setDrinks(List<Product> drinks) {
        this.drinks = drinks;
    }

    @Override
    public  ReplyKeyboard getFoodsKeyboard(){
        KeyboardRow row = new KeyboardRow();
        this.foods = ReadConfig.getFoodsFromRepository();
        for (Product food: foods) {
            row.add(food.getName());
        }
        row.add(Constants.RETURN);
        return new ReplyKeyboardMarkup(List.of(row));
    }

    @Override
    public ReplyKeyboard getDrinksKeyboard(){
        KeyboardRow row = new KeyboardRow();

        this.drinks = ReadConfig.getDrinksFromRepository();
        for (Product drink: drinks) {
            row.add(drink.getName());
        }

        row.add(Constants.RETURN);
        return new ReplyKeyboardMarkup(List.of(row));
    }


}
