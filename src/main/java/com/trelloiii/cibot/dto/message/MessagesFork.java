package com.trelloiii.cibot.dto.message;

import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class MessagesFork extends AbstractFork {
    @Override
    public SendMessage process(String message, String chatId) {
        switch (message){
            case "Create pipeline":
                return createPipeline(chatId);
            case "main":
            case "start":
            default:
                return mainProcess(chatId,"What can I help you?");
        }
    }


    private SendMessage createPipeline(String chatId) {
        PipelineFactory.instance();
        SendMessage sendMessage=new SendMessage(chatId,"Lets define name of a pipeline");
        setOneRowButtons(sendMessage,"Main","One stage back <--");
        return sendMessage;
    }
}
