package org.example.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface ReplyService {
    SendMessage replyToStart(long chatId);
    SendMessage replyToStop(long chatId);
    SendMessage replyForDrinkFoodSelectionForOldOrder(long chatId, Message message);
    SendMessage replyForOrderSelection(long chatId, Message message);
    SendMessage replyForPosition(long chatId, Message message);
    SendMessage replyForAmount(long chatId, Message message);
    SendMessage replyStartForOldOrder(long chatId);
    SendMessage unexpectedMessage(long chatId);

}
