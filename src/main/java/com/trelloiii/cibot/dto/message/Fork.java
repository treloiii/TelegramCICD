package com.trelloiii.cibot.dto.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.function.Consumer;

public interface Fork {
    List<SendMessage> process(String message, String chatId);
    default List<SendMessage> processCallback(String message, String data, String chatId, Consumer<SendMessage> sendMessageConsumer){
        throw new UnsupportedOperationException("This operation is unsupported");
    }
}
