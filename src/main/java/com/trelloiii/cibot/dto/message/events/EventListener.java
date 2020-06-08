package com.trelloiii.cibot.dto.message.events;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface EventListener {
    void listen(Update entity);
    String getType();
}
