package com.trelloiii.cibot;

import com.trelloiii.cibot.dto.message.events.CallbackEventEventListener;
import com.trelloiii.cibot.dto.message.MessageDistributor;
import com.trelloiii.cibot.dto.message.events.MessageEventListener;
import lombok.SneakyThrows;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.*;
import java.util.stream.Stream;

@Component
public class Bot extends TelegramLongPollingBot {
    @Value("${bot.token}")
    private String botToken;
    private final MessageDistributor messageDistributor;

    @Autowired
    public Bot(MessageDistributor messageDistributor, BeanFactory beanFactory) {
        this.messageDistributor = messageDistributor;
        Consumer<SendMessage> consumer = sendMessage -> {
            try {
                execute(sendMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Stream.of(
                beanFactory.getBean(MessageEventListener.class,consumer),
                beanFactory.getBean(CallbackEventEventListener.class,consumer)
        ).forEach(eventListener -> messageDistributor.subscribe(eventListener.getType(),eventListener));
    }
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("new message!");
        if(update.hasCallbackQuery())
            messageDistributor.processCallback(update);
        else
            messageDistributor.processMessage(update);
    }

    @Override
    public String getBotUsername() {
        return "project_ci_bot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
