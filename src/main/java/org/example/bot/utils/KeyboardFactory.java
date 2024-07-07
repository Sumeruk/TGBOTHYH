package org.example.bot.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class KeyboardFactory {

    public static ReplyKeyboard getFoodsOrDrinkKeyboardForNewOrder(){
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
        row.add("Курица Сыр");
        row.add("Пепперони");
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public static ReplyKeyboard getDrinksKeyboard(){
        KeyboardRow row = new KeyboardRow();
        row.add("Морс");
        row.add("Крыжовник грейпфрут");
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
