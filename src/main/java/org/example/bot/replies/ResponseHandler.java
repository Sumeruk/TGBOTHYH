package org.example.bot.replies;


import org.example.bot.constants.Constants;
import org.example.bot.constants.UserState;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

public class ResponseHandler {
    private final SilentSender sender;
    private final Map<Long, UserState> chatStates;
    private final ReplyService replyService;

    public ResponseHandler(SilentSender sender, DBContext db) {
        this.sender = sender;
        chatStates = db.getMap(Constants.CHAT_STATES);
        replyService = new ReplyServiceImpl(sender, chatStates);
    }


    public void replyToStart(long chatId) {
        SendMessage message = replyService.replyToStart(chatId);
        sender.execute(message);
    }

    public void replyToStop(long chatId) {
        SendMessage message = replyService.replyToStop(chatId);
        sender.execute(message);
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

    //TODO сохранение в общий заказ в сервисе
    private void replyForDrinkFoodSelection(long chatId, Message message) {
        SendMessage sendMessage = replyService.replyForDrinkFoodSelection(chatId, message);
        sender.execute(sendMessage);
    }

    private void replyForPosition(long chatId, Message message) {
        SendMessage sendMessage = replyService.replyForPosition(chatId, message);
        sender.execute(sendMessage);
    }

    //TODO сохранение в общий заказ в сервисе
    private void replyForAmount(long chatId, Message message) {
        SendMessage sendMessage = replyService.replyForAmount(chatId, message);
        sender.execute(sendMessage);
        replyStartForOldOrder(chatId);
    }

    private void replyStartForOldOrder(long chatId) {
        SendMessage sendMessage = replyService.replyStartForOldOrder(chatId);
        sender.execute(sendMessage);
    }

    private void replyForDrinkFoodSelectionForOldOrder(long chatId, Message message) {
        SendMessage sendMessage = replyService.replyForDrinkFoodSelection(chatId, message);
        sender.execute(sendMessage);
    }

    private void unexpectedMessage(long chatId) {
        SendMessage sendMessage = replyService.unexpectedMessage(chatId);
        sender.execute(sendMessage);
    }

    public boolean userIsActive(Long chatId) {
        return chatStates.containsKey(chatId);
    }


}
