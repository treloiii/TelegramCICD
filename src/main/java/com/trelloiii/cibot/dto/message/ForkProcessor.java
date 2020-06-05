package com.trelloiii.cibot.dto.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
import com.trelloiii.cibot.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.function.Consumer;

@Component
public class ForkProcessor {
    private final FactoryFork factoryFork;
    private final MessagesFork messagesFork;
    private final ActivationFork activationFork;
    private final UserService userService;

    public ForkProcessor(FactoryFork factoryFork, MessagesFork messagesFork, UserService userService, ActivationFork activationFork) {
        this.factoryFork = factoryFork;
        this.messagesFork = messagesFork;
        this.userService = userService;
        this.activationFork = activationFork;
    }

    public List<SendMessage> processMessage(Message tmMessage) {
        String chatId = tmMessage.getChatId().toString();
        String message = tmMessage.getText();
        User from = tmMessage.getFrom();
        if (userService.checkIfExists(from)) {
            if (!PipelineFactory.haveInstance()) {
                return factoryFork.process(message, chatId);
            } else {
                return messagesFork.process(message, chatId);
            }
        } else {//либо пользователь не существует либо рут не активен
            activationFork.setFrom(from);
            return activationFork.process(message, chatId);
        }
    }

    @SneakyThrows
    public List<SendMessage> processCallBack(CallbackQuery callbackQuery, Consumer<SendMessage> sendMessageConsumer) {
        String chatId=callbackQuery.getMessage().getChatId().toString();
        String[] dataAndPath=callbackQuery.getData().split("&");
        if (dataAndPath[0].equals("msg")) {
            return messagesFork.processCallback(dataAndPath[1],dataAndPath[2],chatId,sendMessageConsumer);
        }
        else{
            return null;
        }
    }
}
