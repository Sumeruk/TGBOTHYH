package org.example.bot;

import org.example.ReadConfig;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

import static org.example.bot.Constants.START_TEXT;
import static org.example.bot.Constants.START_TEXT_FOR_OLD_ORDER;
import static org.example.bot.UserState.*;

public class ReplyServiceImpl implements ReplyService {

    private final SilentSender sender;
    private final Map<Long, UserState> chatStates;

    public ReplyServiceImpl(SilentSender sender, Map<Long, UserState> chatStates) {
        this.sender = sender;
        this.chatStates = chatStates;
    }


    @Override
    public SendMessage replyToStart(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_TEXT);
        message.setReplyMarkup(KeyboardFactory.getFoodsOrDrinkKeyboardForNewOrder());
        chatStates.put(chatId, UserState.NEW_ORDER_FOOD_DRINK_SELECTION);
        return message;
    }

    @Override
    public SendMessage replyToStop(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("спасибо за общение");
        sendMessage.setReplyMarkup(KeyboardFactory.getStart());
        chatStates.clear();
        chatStates.put(chatId, AWAITING_START);
        return sendMessage;
    }

    @Override
    public SendMessage processingReplyAfterStop() {
        return null;
    }

    @Override
    public SendMessage replyForDrinkFoodSelection(long chatId, Message message) {
        if(isMessageCorrect(chatId, message)) {
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
            chatStates.put(chatId, CHOICE_POSITION);
            return sendMessage;
        } else
            return null;
    }

    @Override
    public SendMessage replyForPosition(long chatId, Message message) {
        if(isMessageCorrect(chatId, message)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Выбран " + message.getText());
            sendMessage.setReplyMarkup(KeyboardFactory.getAmounts());
            chatStates.put(chatId, AMOUNT_SELECTION);
            return sendMessage;
        } else
            return null;
    }

    @Override
    public SendMessage replyForAmount(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        try {
            short amount = Short.parseShort(message.getText());
            sendMessage.setText("Добавлено " + amount);
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
        message.setReplyMarkup(KeyboardFactory.getFoodsOrDrinkKeyboardForOldOrder());
        chatStates.put(chatId, UserState.OLD_ORDER_FOOD_DRINK_SELECTION);
        return message;
    }

    private SendMessage replyForDrinkFoodSelectionForOldOrder(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        switch (message.getText()) {
            case "Заказ совершен": {
                sendMessage.setText("Еще не дописан функционал");
                return sendMessage;
            }
            case "Еда": {
                sendMessage.setReplyMarkup(KeyboardFactory.getFoodsKeyboard());
                sendMessage.setText("Выберите позицию");
                chatStates.put(chatId, CHOICE_POSITION);
                return sendMessage;
            }
            case "Напитки": {
                sendMessage.setReplyMarkup(KeyboardFactory.getDrinksKeyboard());
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

    private boolean isMessageCorrect(long chatId, Message message){
        if(chatStates.get(chatId).equals(CHOICE_POSITION)){
            return isMessageContainsPosition(message);
        }
        return true;
    }

    private boolean isMessageContainsPosition(Message message){
        return ReadConfig.getDrinks().contains(message.getText()) || ReadConfig.getFood().contains(message.getText());
    }
}
