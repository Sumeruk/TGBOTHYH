package org.example.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public interface KeyboardFactory {
    ReplyKeyboard getFoodsOrDrinkKeyboardForNewOrder();
    ReplyKeyboard getFoodsOrDrinkKeyboardForOldOrder();
    ReplyKeyboard getFoodsKeyboard();
    ReplyKeyboard getDrinksKeyboard();
    ReplyKeyboard getAmounts();
    ReplyKeyboard getStart();
}
