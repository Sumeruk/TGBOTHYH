package org.example.bot;


import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

import static org.example.bot.Constants.START_TEXT;
import static org.example.bot.Constants.START_TEXT_FOR_OLD_ORDER;
import static org.example.bot.UserState.*;

public class ResponseHandler {
    private final SilentSender sender;
    private final Map<Long, UserState> chatStates;

    public ResponseHandler(SilentSender sender, DBContext db) {
        this.sender = sender;
        chatStates = db.getMap(Constants.CHAT_STATES);
    }

    public void replyToStart(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_TEXT);
        message.setReplyMarkup(KeyboardFactory.getFoodsOrDrinkKeyboardForNewOrder());
        sender.execute(message);
        chatStates.put(chatId, UserState.NEW_ORDER_FOOD_DRINK_SELECTION);
    }

    public void replyToStop(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("спасибо за общение");
        sendMessage.setReplyMarkup(KeyboardFactory.getStart());
        chatStates.clear();
        chatStates.put(chatId, AWAITING_START);
        sender.execute(sendMessage);
    }

    public void replyToButtons(long chatId, Message message) {
        if (message.getText().equals("/stop")) {
            replyToStop(chatId);
            return;
        }

        if (message.getText().equals("Начать")) {
            replyToStart(chatId);
            return;
        }

        switch (chatStates.get(chatId)) {
            case AWAITING_START -> replyToAwaitingStart(chatId, message);
            case AWAITING_MESSAGE -> replyToAwaitingName(chatId, message);
            case NEW_ORDER_FOOD_DRINK_SELECTION -> replyForDrinkFoodSelection(chatId, message);
            case OLD_ORDER_FOOD_DRINK_SELECTION -> replyForDrinkFoodSelectionForOldOrder(chatId, message);
            case CHOICE_POSITION -> replyForPosition(chatId, message);
            case AMOUNT_SELECTION -> replyForAmount(chatId, message);
            default -> unexpectedMessage(chatId);
        }

    }

    private void replyToAwaitingStart(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("ты " + message.getText() + " не пиши, ты /start пиши");
        sender.send(sendMessage.getText(), chatId);
    }

    private void replyToAwaitingName(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Мы получили текст " + message.getText());
        sender.send(sendMessage.getText(), chatId);
        chatStates.put(chatId, UserState.AWAITING_MESSAGE);
    }

    //TODO сохранение в общий заказ
    private void replyForDrinkFoodSelection(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (message.getText().equals("Еда")) {
            sendMessage.setReplyMarkup(KeyboardFactory.getFoodsKeyboard());
        } else {
            if (message.getText().equals("Напитки")) {
                sendMessage.setReplyMarkup(KeyboardFactory.getDrinksKeyboard());
            }
        }
        sendMessage.setText("Выберите позицию");
        sender.execute(sendMessage);
        chatStates.put(chatId, CHOICE_POSITION);
    }

    private void replyForPosition(long chatId, Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выбран " + message.getText());
        sendMessage.setReplyMarkup(KeyboardFactory.getAmounts());
        sender.execute(sendMessage);
        chatStates.put(chatId, AMOUNT_SELECTION);
    }

    //TODO сохранение в общий заказ
    private void replyForAmount(long chatId, Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        try{
            short amount = Short.parseShort(message.getText());
            sendMessage.setText("Добавлено " + amount);
            sender.execute(sendMessage);
            replyStartForOldOrder(chatId);
        } catch (NumberFormatException nfe){
            sendMessage.setText("сообщение не понято, напишите число");
            sender.execute(sendMessage);
        }
    }

    private void replyStartForOldOrder(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_TEXT_FOR_OLD_ORDER);
        message.setReplyMarkup(KeyboardFactory.getFoodsOrDrinkKeyboardForOldOrder());
        sender.execute(message);
        chatStates.put(chatId, UserState.OLD_ORDER_FOOD_DRINK_SELECTION);
    }

    private void replyForDrinkFoodSelectionForOldOrder(long chatId, Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        switch (message.getText()){
            case "Заказ совершен": {
                sendMessage.setText("Еще не дописан функционал");
                sender.execute(sendMessage);
                break;
            }
            case "Еда":{
                sendMessage.setReplyMarkup(KeyboardFactory.getFoodsKeyboard());
                sendMessage.setText("Выберите позицию");
                sender.execute(sendMessage);
                chatStates.put(chatId, CHOICE_POSITION);
                break;
            }
            case "Напитки":{
                sendMessage.setReplyMarkup(KeyboardFactory.getDrinksKeyboard());
                sendMessage.setText("Выберите позицию");
                sender.execute(sendMessage);
                chatStates.put(chatId, CHOICE_POSITION);
                break;
            }
        }
    }

    private void unexpectedMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("не знаю даже что и сказать на такое...");
        sender.send(sendMessage.getText(), chatId);
    }

    public boolean userIsActive(Long chatId) {
        return chatStates.containsKey(chatId);
    }


}
