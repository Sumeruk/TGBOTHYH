package org.example.bot;

import org.example.ReadConfig;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.IOException;
import java.util.List;

public class KeyboardFactory {

    public static ReplyKeyboard getFoodsOrDrinkKeyboardForNewOrder() {
        System.out.println("DEBUG------KeyboardFactory");
        KeyboardRow row = new KeyboardRow();
        row.add("Еда");
        row.add("Напитки");
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public static ReplyKeyboard getFoodsOrDrinkKeyboardForOldOrder(){
        KeyboardRow row = new KeyboardRow();
        row.add("Еда");
        row.add("Напитки");
        row.add("Подытожить заказ");
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public static ReplyKeyboard getFoodsKeyboard(){
        KeyboardRow row = new KeyboardRow();
        List<String> foods = ReadConfig.getFood();
        for (String food: foods) {
            row.add(food);
        }
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public static ReplyKeyboard getDrinksKeyboard(){
        KeyboardRow row = new KeyboardRow();
        List<String> drinks = ReadConfig.getDrinks();
        for (String drink: drinks) {
            row.add(drink);
        }
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public static ReplyKeyboard getAmounts(){
        KeyboardRow row = new KeyboardRow();
        for (int i = 1; i <= 5; i++) {
            row.add(String.valueOf(i));
        }
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public static ReplyKeyboard getStart(){
        KeyboardRow row = new KeyboardRow();
        row.add("Начать");
        return new ReplyKeyboardMarkup(List.of(row));
    }
}
