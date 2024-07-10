package org.example;

import lombok.RequiredArgsConstructor;
import org.example.bot.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final AbilityBot bot;

    @Bean
    public SilentSender getSilentSenderBean(){
        return bot.silent();
    }

    @Bean
    public DBContext getDBContextBean(){
        return bot.db();
    }

    @Bean
    public Map<Long, UserState> getChatStatesBean(){
        return bot.db().getMap(Constants.CHAT_STATES);
    }

    @Bean
    public ResponseHandler getResponseHandlerBean(){
        return new ResponseHandler();
    }

    @Bean("fromFile")
    public KeyboardFactory getKeyboardFactoryBean(){
        return new KeyboardFactoryFromFile();
    }
}
