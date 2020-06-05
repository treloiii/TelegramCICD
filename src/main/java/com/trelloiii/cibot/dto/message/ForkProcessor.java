package com.trelloiii.cibot.dto.message;

import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
import com.trelloiii.cibot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

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

    public SendMessage processMessage(Message tmMessage) {
        String chatId=tmMessage.getChatId().toString();
        String message= tmMessage.getText();
        User from =tmMessage.getFrom();
        if(userService.checkIfExists(from)) {
            if (!PipelineFactory.haveInstance()) {
                return factoryFork.process(message, chatId);
            } else {
                return messagesFork.process(message, chatId);
            }
        }
        else{//либо пользователь не существует либо рут не активен
            activationFork.setFrom(from);
            return activationFork.process(message,chatId);
        }
    }
}
