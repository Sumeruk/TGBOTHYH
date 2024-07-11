package org.example.bot;

import org.example.entity.Order;
import org.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public abstract class KeyboardFactory {

    @Autowired
    private OrderRepository orderRepository;

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
        row.add(Constants.RETURN);
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public abstract ReplyKeyboard getFoodsKeyboard();

    public abstract ReplyKeyboard getDrinksKeyboard();

    public ReplyKeyboard getAmounts(){
        KeyboardRow row = new KeyboardRow();
        for (int i = 1; i <= 5; i++) {
            row.add(String.valueOf(i));
        }
        row.add(Constants.RETURN);
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public ReplyKeyboard getStart(){
        KeyboardRow row = new KeyboardRow();
        row.add("Начать");
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public ReplyKeyboard getKeyboardWithOrders(){
        KeyboardRow row = new KeyboardRow();
        row.add("Новый заказ");
        List<Order> notCompletedOrders = orderRepository.getNotCompletedOrders();
        System.out.println(notCompletedOrders);
        notCompletedOrders.stream()
                .map(Order::getDesk)
                .map(String::valueOf)
                .forEach(row::add);
        return new ReplyKeyboardMarkup(List.of(row));
    }
}
