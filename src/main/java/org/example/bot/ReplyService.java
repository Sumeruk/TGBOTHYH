package org.example.bot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public interface ReplyService {
    SendMessage replyToStart(long chatId);
    SendMessage replyToStop(long chatId);
    SendMessage processingReplyAfterStop();
    SendMessage replyForDrinkFoodSelection(long chatId, Message message);
    SendMessage replyForPosition(long chatId, Message message);
    SendMessage replyForAmount(long chatId, Message message);
    SendMessage replyStartForOldOrder(long chatId);
    SendMessage unexpectedMessage(long chatId);

}
