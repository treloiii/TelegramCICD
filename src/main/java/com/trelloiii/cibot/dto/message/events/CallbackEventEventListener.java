package com.trelloiii.cibot.dto.message.events;

import com.trelloiii.cibot.dto.message.branch.MessageBranch;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.function.Function;


public class CallbackEventEventListener implements EventListener {
    private final Function<Object, Message> sendMessageFunction;
    @Getter
    private final String type="callback";
    @Autowired
    private MessageBranch messageBranch;

    @Autowired
    public CallbackEventEventListener(Function<Object, Message> sendMessageFunction) {
        this.sendMessageFunction = sendMessageFunction;
    }
    @PostConstruct
    public void initBranches(){
        messageBranch.setSendMessageFunction(sendMessageFunction);
    }
    @Override
    public void listen(Update entity) {
        CallbackQuery callbackQuery=entity.getCallbackQuery();
        String[] dataAndPath=callbackQuery.getData().split("&");
        if (dataAndPath[0].equals("msg")) {
            messageBranch.processCallback(callbackQuery.getMessage(), Arrays.copyOfRange(dataAndPath,1,3));
        }
    }
}
