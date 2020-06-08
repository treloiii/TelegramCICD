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
        new Thread(()-> this.eventManager.call("message", update)).start();
    }

    public void processCallback(Update update) {
        new Thread(()->this.eventManager.call("callback", update)).start();
    }
}
