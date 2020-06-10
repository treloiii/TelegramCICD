package com.trelloiii.cibot.dto.message.events;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EventManager {
    Map<String, List<EventListener>> eventListeners =new HashMap<>();

    public EventManager(String...operations) {
        for(String operation:operations){
            eventListeners.put(operation,new ArrayList<>());
        }
    }

    public void subscribe(String type,EventListener eventListener){
        List<EventListener> listeners=eventListeners.get(type);
        listeners.add(eventListener);
    }
    public void unsubscribe(String type,EventListener eventListener){
        List<EventListener> listeners=eventListeners.get(type);
        listeners.remove(eventListener);
    }
    public  void call(String type, Update update){
        List<EventListener> listeners=eventListeners.get(type);
        listeners.forEach(el->el.listen(update));
    }
}
