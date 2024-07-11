package org.example.bot;

import org.example.ReadConfig;
import org.example.models.ProductModel;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class KeyboardFactoryFromFile extends KeyboardFactory{
    private List<ProductModel> foods;
    private List<ProductModel> drinks;
    @Override
    public  ReplyKeyboard getFoodsKeyboard(){
        KeyboardRow row = new KeyboardRow();
        this.foods = ReadConfig.getFood();
        for (ProductModel food: foods) {
            row.add(food.getName());
        }
        row.add(Constants.RETURN);
        return new ReplyKeyboardMarkup(List.of(row));
    }

    @Override
    public ReplyKeyboard getDrinksKeyboard(){
        KeyboardRow row = new KeyboardRow();
        this.drinks = ReadConfig.getDrinks();
        for (ProductModel drink: drinks) {
            row.add(drink.getName());
        }
        row.add(Constants.RETURN);
        return new ReplyKeyboardMarkup(List.of(row));
    }


}
