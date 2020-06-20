package com.trelloiii.cibot;

import com.trelloiii.cibot.dto.message.events.CallbackEventEventListener;
import com.trelloiii.cibot.dto.message.MessageDistributor;
import com.trelloiii.cibot.dto.message.events.MessageEventListener;
import lombok.SneakyThrows;
import org.eclipse.jgit.diff.Edit;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.stickers.SetStickerSetThumb;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import reactor.core.publisher.Flux;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.Duration;
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
        Function<Object,Message> consumer = sendMessage -> {
            try {
                if(sendMessage instanceof SendMessage)
                    return execute((SendMessage)sendMessage);
                else if(sendMessage instanceof EditMessageText)
                    execute((EditMessageText)sendMessage);
                else
                    throw new RuntimeException("Bad sendmessage type");
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
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
        System.out.println("new message2!");
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
