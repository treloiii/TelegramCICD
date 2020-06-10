package com.trelloiii.cibot.dto.message.events;

import com.trelloiii.cibot.dto.message.branch.MessageBranch;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.function.Consumer;


public class CallbackEventEventListener implements EventListener {
    private final Consumer<SendMessage> sendMessageConsumer;
    @Getter
    private final String type="callback";
    @Autowired
    private MessageBranch messageBranch;

    @Autowired
    public CallbackEventEventListener(Consumer<SendMessage> sendMessageConsumer) {
        this.sendMessageConsumer = sendMessageConsumer;
    }
    @PostConstruct
    public void initBranches(){
        messageBranch.setSendMessageConsumer(sendMessageConsumer);
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
