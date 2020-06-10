package com.trelloiii.cibot.dto.message.branch;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.function.Consumer;

public interface Branch {
    void process(Message message);
    default void processCallback(Message message, String[] data){
        throw new UnsupportedOperationException("This operation is unsupported");
    }
}
