package com.trelloiii.cibot.dto.message.events;

import com.trelloiii.cibot.dto.message.branch.ActivationBranch;
import com.trelloiii.cibot.dto.message.branch.FactoryBranch;
import com.trelloiii.cibot.dto.message.branch.MessageBranch;
import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
import com.trelloiii.cibot.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.PostConstruct;
import java.util.function.Function;

public class MessageEventListener implements EventListener {
    private final Function<Object, Message> sendMessageFunction;
    @Getter
    private final String type="message";
    @Autowired
    private UserService userService;
    @Autowired
    private FactoryBranch factoryBranch;
    @Autowired
    private MessageBranch messageBranch;
    @Autowired
    private ActivationBranch activationBranch;
    public MessageEventListener(Function<Object, Message> sendMessageFunction) {
        this.sendMessageFunction = sendMessageFunction;
    }
    @PostConstruct
    public void initBranches(){
        factoryBranch.setSendMessageFunction(sendMessageFunction);
        messageBranch.setSendMessageFunction(sendMessageFunction);
        activationBranch.setSendMessageFunction(sendMessageFunction);
    }

    @Override
    public void listen(Update update) {
        Message message=update.getMessage();
        User from = update.getMessage().getFrom();
        if (userService.checkIfExists(from)) {
            if (!PipelineFactory.haveInstance()) {
                factoryBranch.process(message);
            } else {
                messageBranch.process(message);
            }
        } else {//либо пользователь не существует либо рут не активен
            activationBranch.process(message);
        }
    }
}
