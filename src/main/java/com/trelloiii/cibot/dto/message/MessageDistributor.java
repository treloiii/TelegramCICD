package com.trelloiii.cibot.dto.message;

import com.trelloiii.cibot.dto.message.events.EventListener;
import com.trelloiii.cibot.dto.message.events.EventManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageDistributor {
    private final EventManager eventManager;

    public MessageDistributor() {
        this.eventManager = new EventManager("message", "callback");
    }

    public void subscribe(String type, EventListener eventListener) {
        eventManager.subscribe(type, eventListener);
    }

    public void processMessage(Update update) {
        this.eventManager.call("message", update);
    }

    public void processCallback(Update update) {
        this.eventManager.call("callback", update);
    }
}
