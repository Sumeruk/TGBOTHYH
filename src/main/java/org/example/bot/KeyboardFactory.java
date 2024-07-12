package org.example.bot;

import org.example.ReadConfig;
import org.example.entity.Order;
import org.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public abstract class KeyboardFactory {

    @Autowired
    private OrderRepository orderRepository;

    private List<Integer> allTables;

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
        row.add(Constants.HOME);

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

    public ReplyKeyboard getKeyboardWithActivities(){
        KeyboardRow row = new KeyboardRow();
        row.add(Constants.NEW_ORDER);
        row.add(Constants.ADD_TO_ORDER);
        row.add(Constants.CALCULATE_THE_TABLE);

        return new ReplyKeyboardMarkup(List.of(row));
    }

    public ReplyKeyboard getNotCompletedTables(){
        KeyboardRow row = new KeyboardRow();
        List<Order> notCompletedOrders = orderRepository.getNotCompletedOrders();

        notCompletedOrders.stream()
                .map(Order::getDesk)
                .map(String::valueOf)
                .forEach(row::add);
        row.add(Constants.RETURN);

        return new ReplyKeyboardMarkup(List.of(row));
    }

    public ReplyKeyboard getKeyboardFreeTables(){
        KeyboardRow row = new KeyboardRow();
        List<Integer> freeTables = getFreeTables();
        for (Integer freeTable: freeTables) {
            row.add(String.valueOf(freeTable));
        }
        row.add(Constants.RETURN);

        return new ReplyKeyboardMarkup(List.of(row));
    }

    private List<Integer> getFreeTables(){
        if (allTables == null) {
            allTables = new ArrayList<>();
            for (int i = 1; i < ReadConfig.getNumberOfTables(); i++) {
                allTables.add(i);
            }
        }

        List<Integer> result = new ArrayList<>(allTables);
        List<Order> notCompletedOrders = orderRepository.getNotCompletedOrders();
        result.removeAll(notCompletedOrders.stream()
                .map(Order::getDesk)
                .toList());

        return result;
    }
}
