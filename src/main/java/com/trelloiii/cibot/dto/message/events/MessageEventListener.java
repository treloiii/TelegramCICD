package com.trelloiii.cibot.dto.message.events;

import com.trelloiii.cibot.dto.message.branch.ActivationBranch;
import com.trelloiii.cibot.dto.message.branch.FactoryBranch;
import com.trelloiii.cibot.dto.message.branch.MessageBranch;
import com.trelloiii.cibot.dto.message.events.EventListener;
import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
import com.trelloiii.cibot.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.function.Consumer;

public class MessageEventListener implements EventListener {
    private final Consumer<SendMessage> sendMessageConsumer;
    public MessageEventListener(Consumer<SendMessage> sendMessageConsumer) {
        this.sendMessageConsumer = sendMessageConsumer;
    }
    @Getter
    private final String type="message";
    @Autowired
    private UserService userService;
    @Autowired
    private FactoryBranch factoryFork;
    @Autowired
    private MessageBranch messagesFork;
    @Autowired
    private ActivationBranch activationFork;

    @Override
    public void listen(Update update) {
        Message message=update.getMessage();
        User from = update.getMessage().getFrom();
        if (userService.checkIfExists(from)) {
            if (!PipelineFactory.haveInstance()) {
                factoryFork.process(message,sendMessageConsumer);
            } else {
                messagesFork.process(message,sendMessageConsumer);
            }
        } else {//либо пользователь не существует либо рут не активен
            activationFork.process(message,sendMessageConsumer);
        }
    }
}
