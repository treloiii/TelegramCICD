package com.trelloiii.cibot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@SpringBootApplication
public class CiBotApplication {

    public static void main(String[] args) {
        setEnv(args);
        ApiContextInitializer.init();
        SpringApplication.run(CiBotApplication.class, args);
    }

    private static void setEnv(String[] args){
        try{
            System.setProperty("MYSQL_HOST",args[0]);
            System.setProperty("MYSQL_USER",args[1]);
            System.setProperty("MYSQL_PASSWORD",args[2]);
            System.setProperty("BOT_TOKEN",args[3]);
            System.setProperty("PORT",args[4]);
        }
        catch (NullPointerException | ArrayIndexOutOfBoundsException e){
            System.out.println("environments not found");
        }
    }

}
