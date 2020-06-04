package com.trelloiii.cibot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@SpringBootApplication
public class CiBotApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(CiBotApplication.class, args);
    }

}
