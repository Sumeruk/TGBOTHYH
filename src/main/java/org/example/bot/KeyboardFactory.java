package org.example.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public abstract class KeyboardFactory {


//    public ReplyKeyboard getFreeTables(){
//
//    }

    public ReplyKeyboard getFoodsOrDrinkKeyboardForNewOrder() {
        KeyboardRow row = new KeyboardRow();
        row.add("Еда");
        row.add("Напитки");
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public ReplyKeyboard getFoodsOrDrinkKeyboardForOldOrder(){
        KeyboardRow row = new KeyboardRow();
        row.add("Еда");
        row.add("Напитки");
        row.add("Подытожить заказ");
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public abstract ReplyKeyboard getFoodsKeyboard();

    public abstract ReplyKeyboard getDrinksKeyboard();

    public ReplyKeyboard getAmounts(){
        KeyboardRow row = new KeyboardRow();
        for (int i = 1; i <= 5; i++) {
            row.add(String.valueOf(i));
        }
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public ReplyKeyboard getStart(){
        KeyboardRow row = new KeyboardRow();
        row.add("Начать");
        return new ReplyKeyboardMarkup(List.of(row));
    }
}
