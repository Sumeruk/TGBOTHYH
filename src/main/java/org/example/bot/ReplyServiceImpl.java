package org.example.bot;

import org.example.ReadConfig;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.example.bot.Constants.START_TEXT;
import static org.example.bot.Constants.START_TEXT_FOR_OLD_ORDER;
import static org.example.bot.UserState.*;

public class ReplyServiceImpl implements ReplyService {

    private final Map<Long, UserState> chatStates;
    private final KeyboardFactory keyboardFactory;

    private List<String> order = new ArrayList<>();


    public ReplyServiceImpl(Map<Long, UserState> chatStates) {
        this.chatStates = chatStates;
        keyboardFactory = new KeyboardFactoryFromFile();
    }

    @Override
    public SendMessage replyToStart(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_TEXT);
        message.setReplyMarkup(keyboardFactory.getFoodsOrDrinkKeyboardForNewOrder());
        chatStates.put(chatId, UserState.NEW_ORDER_FOOD_DRINK_SELECTION);
        return message;
    }

    @Override
    public SendMessage replyToStop(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("спасибо за общение");
        sendMessage.setReplyMarkup(keyboardFactory.getStart());
        chatStates.clear();
        chatStates.put(chatId, AWAITING_START);
        return sendMessage;
    }


    @Override
    public SendMessage replyForDrinkFoodSelection(long chatId, Message message) {
        if (isMessageCorrect(chatId, message)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            if (message.getText().equals("Еда")) {
                sendMessage.setReplyMarkup(keyboardFactory.getFoodsKeyboard());
            } else {
                if (message.getText().equals("Напитки")) {
                    sendMessage.setReplyMarkup(keyboardFactory.getDrinksKeyboard());
                }
            }
            sendMessage.setText("Выберите позицию");
            chatStates.put(chatId, CHOICE_POSITION);
            return sendMessage;
        } else
            return null;
    }

    @Override
    public SendMessage replyForPosition(long chatId, Message message) {
        if (isMessageCorrect(chatId, message)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Выбран " + message.getText());
            sendMessage.setReplyMarkup(keyboardFactory.getAmounts());
            chatStates.put(chatId, AMOUNT_SELECTION);
            setInfoToOrder(message);
            return sendMessage;
        } else
            return null;
    }

    private void setInfoToOrder(Message message){
        try {
            Integer.parseInt(message.getText());
            String position = order.get(order.size() - 1) + message.getText();
            order.set(order.size() - 1, position);
        } catch (NumberFormatException nfe){
            order.add(message.getText());
        }
    }

    @Override
    public SendMessage replyForAmount(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        try {
            short amount = Short.parseShort(message.getText());
            sendMessage.setText("Добавлено " + amount);
            setInfoToOrder(message);
            return sendMessage;
        } catch (NumberFormatException nfe) {
            sendMessage.setText("сообщение не понято, напишите число");
            return sendMessage;
        }
    }

    @Override
    public SendMessage replyStartForOldOrder(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_TEXT_FOR_OLD_ORDER);
        message.setReplyMarkup(keyboardFactory.getFoodsOrDrinkKeyboardForOldOrder());
        chatStates.put(chatId, UserState.OLD_ORDER_FOOD_DRINK_SELECTION);
        return message;
    }

    public SendMessage replyForDrinkFoodSelectionForOldOrder(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        switch (message.getText()) {
            case "Подытожить заказ": {
                sendMessage.setText(order.toString());
                return sendMessage;
            }
            case "Еда": {
                sendMessage.setReplyMarkup(keyboardFactory.getFoodsKeyboard());
                sendMessage.setText("Выберите позицию");
                chatStates.put(chatId, CHOICE_POSITION);
                return sendMessage;
            }
            case "Напитки": {
                sendMessage.setReplyMarkup(keyboardFactory.getDrinksKeyboard());
                sendMessage.setText("Выберите позицию");
                chatStates.put(chatId, CHOICE_POSITION);
                return sendMessage;
            }
            default: {
                return unexpectedMessage(chatId);
            }
        }
    }

    @Override
    public SendMessage unexpectedMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("не знаю даже что и сказать на такое...");
        return sendMessage;
    }

    private boolean isMessageCorrect(long chatId, Message message) {
        if (chatStates.get(chatId).equals(CHOICE_POSITION)) {
            return isMessageContainsPosition(message);
        }
        return true;
    }

    private boolean isMessageContainsPosition(Message message) {
        return ReadConfig.getDrinks().contains(message.getText()) || ReadConfig.getFood().contains(message.getText());
    }
}
