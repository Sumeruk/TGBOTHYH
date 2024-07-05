package org.example;

import org.example.bot.PizzaBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Main.class, args);

        try{
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(ctx.getBean(PizzaBot.class));
        } catch (TelegramApiException e){
            throw new RuntimeException(e);
        }

    }
}