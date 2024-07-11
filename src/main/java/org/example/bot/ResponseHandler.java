package org.example.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

public class ResponseHandler {
    private SilentSender sender;
    private Map<Long, UserState> chatStates;
    private ReplyService replyService;

    public ResponseHandler() {
    }

    @Autowired
    public void setSender(SilentSender sender) {
        this.sender = sender;
    }

    @Autowired
    public void setChatStates(Map<Long, UserState> chatStates) {
        this.chatStates = chatStates;
    }

    @Autowired
    public void setReplyService(ReplyService replyService) {
        this.replyService = replyService;
    }


    public ResponseHandler(SilentSender sender, DBContext db) {
        this.sender = sender;
        chatStates = db.getMap(Constants.CHAT_STATES);
        replyService = new ReplyServiceImpl(chatStates);

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

        if (message.getText().equals("Новый заказ")){
            // логика нового заказа
        }

        switch (chatStates.get(chatId)) {
            case ORDER_SELECTION -> replyForOrderSelection(chatId, message);
            case OLD_ORDER_FOOD_DRINK_SELECTION -> replyForDrinkFoodSelectionForOldOrder(chatId, message);
            case CHOICE_POSITION -> replyForPosition(chatId, message);
            case AMOUNT_SELECTION -> replyForAmount(chatId, message);
            default -> unexpectedMessage(chatId);
        }

    }

    private void replyForOrderSelection(long chatId, Message message) {
        SendMessage sendMessage = replyService.replyForOrderSelection(chatId, message);
        sender.execute(sendMessage);
    }

    private void replyForPosition(long chatId, Message message) {
        SendMessage sendMessage = replyService.replyForPosition(chatId, message);

        if (sendMessage != null) {
            sender.execute(sendMessage);
        } else {
            unexpectedMessage(chatId);
        }
    }

    private void replyForAmount(long chatId, Message message) {
        SendMessage sendMessage = replyService.replyForAmount(chatId, message);
        sender.execute(sendMessage);
        if (!sendMessage.getText().equals(Constants.START_TEXT) &&
                !sendMessage.getText().equals(Constants.START_TEXT_FOR_OLD_ORDER)) {
            replyStartForOldOrder(chatId);
        }
    }

    private void replyStartForOldOrder(long chatId) {
        SendMessage sendMessage = replyService.replyStartForOldOrder(chatId);
        sender.execute(sendMessage);
    }

    private void replyForDrinkFoodSelectionForOldOrder(long chatId, Message message) {
        SendMessage sendMessage = replyService.replyForDrinkFoodSelectionForOldOrder(chatId, message);
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
