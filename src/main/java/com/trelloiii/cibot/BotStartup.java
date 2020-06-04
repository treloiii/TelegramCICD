package com.trelloiii.cibot;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

public class BotStartup {
    @Autowired
    private Bot bot;
    public void startup(){
        TelegramBotsApi telegramBotsApi=new TelegramBotsApi();
        try{
            telegramBotsApi.registerBot(bot);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
