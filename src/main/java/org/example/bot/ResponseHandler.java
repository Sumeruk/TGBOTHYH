package org.example.bot;


import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

import static org.example.bot.Constants.START_TEXT;

public class ResponseHandler {
    private final SilentSender sender;
    private final Map<Long, UserState> chatStates;

    public ResponseHandler(SilentSender sender, DBContext db) {
        this.sender = sender;
        chatStates = db.getMap(Constants.CHAT_STATES);
    }

    public void replyToStart(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_TEXT);
        sender.send(message.getText(), chatId);
        chatStates.put(chatId, UserState.AWAITING_NAME);
    }

    public void replyToButtons(long chatId, Message message) {
        if (message.getText().equals("/stop")) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("спасибо за общение");
            chatStates.remove(chatId);
            sender.execute(sendMessage);
        }

        if (chatStates.containsKey(chatId)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Мы получили текст " + message.getText());
            sender.send(sendMessage.getText(), chatId);
            chatStates.put(chatId, UserState.FOOD_DRINK_SELECTION);
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("пиши /start");
            sender.send(sendMessage.getText(), chatId);
        }
    }

    public boolean userIsActive(Long chatId) {
        return chatStates.containsKey(chatId);
    }


}
