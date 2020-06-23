package com.trelloiii.cibot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class CiBotApplication {

    public static void main(String[] args) {
        setEnv2(args);
        ApiContextInitializer.init();
        SpringApplication.run(CiBotApplication.class, args);
    }

    @Deprecated
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
    private static void setEnv2(String[] args){
        try {
            System.setProperty("BOT_TOKEN", args[0]);
            System.setProperty("BOT_NAME", args[1]);
            System.setProperty("PORT", args[2]);
        }
        catch (NullPointerException | ArrayIndexOutOfBoundsException e){
            System.out.println("environments not found");
        }

    }

}
