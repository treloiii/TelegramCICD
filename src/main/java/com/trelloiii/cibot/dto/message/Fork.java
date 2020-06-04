package com.trelloiii.cibot.dto.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface Fork {
    SendMessage process(String message,String chatId);
}
