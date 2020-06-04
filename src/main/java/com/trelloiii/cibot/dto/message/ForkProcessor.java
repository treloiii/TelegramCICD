package com.trelloiii.cibot.dto.message;

import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class ForkProcessor {
    private final FactoryFork factoryFork;
    private final MessagesFork messagesFork;
    public ForkProcessor(FactoryFork factoryFork, MessagesFork messagesFork) {
        this.factoryFork = factoryFork;
        this.messagesFork = messagesFork;
    }
    public SendMessage processMessage(String message,String chatId){
       if (!PipelineFactory.haveInstance()){
           return factoryFork.process(message,chatId);
       }
       else{
           return messagesFork.process(message,chatId);
       }
    }
}
