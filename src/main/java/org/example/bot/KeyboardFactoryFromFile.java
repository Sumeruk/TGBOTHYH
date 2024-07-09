package org.example.bot;

import org.example.ReadConfig;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class KeyboardFactoryFromFile extends KeyboardFactory{

    @Override
    public  ReplyKeyboard getFoodsKeyboard(){
        KeyboardRow row = new KeyboardRow();
        List<String> foods = ReadConfig.getFood();
        for (String food: foods) {
            row.add(food);
        }
        row.add(Constants.RETURN);
        return new ReplyKeyboardMarkup(List.of(row));
    }

    @Override
    public ReplyKeyboard getDrinksKeyboard(){
        KeyboardRow row = new KeyboardRow();
        List<String> drinks = ReadConfig.getDrinks();
        for (String drink: drinks) {
            row.add(drink);
        }
        row.add(Constants.RETURN);
        return new ReplyKeyboardMarkup(List.of(row));
    }
}
