package org.example.bot;

import org.example.ReadConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.BiConsumer;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;


@Component
public class OrderBot extends AbilityBot {
    private ResponseHandler responseHandler;

    public OrderBot() {
        super(ReadConfig.getBotToken(), "HYHbot");
    }

    @Autowired
    public void setResponseHandler(@Lazy  ResponseHandler responseHandler){
        this.responseHandler = responseHandler;
    }


    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .info(Constants.START_DESCRIPTION)
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> responseHandler.replyToStart(ctx.chatId()))
                .build();
    }

    public Reply replyToButtons(){
        BiConsumer<BaseAbilityBot, Update> action = (
                (baseAbilityBot, update) -> responseHandler.replyToButtons(getChatId(update), update.getMessage()));

        return Reply.of(action, Flag.TEXT, upd -> responseHandler.userIsActive(getChatId(upd)));

    }
    @Override
    public long creatorId() {
        return 1L;
    }
}
