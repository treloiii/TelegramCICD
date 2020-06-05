package com.trelloiii.cibot.dto.message;

import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
import com.trelloiii.cibot.model.Pipeline;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class FactoryFork extends AbstractFork {
    private PipelineFactory pipelineFactory = PipelineFactory.getInstance();

    @Override
    public SendMessage process(String message, String chatId) {
        switch (message) {
            case "One stage back <--":
                return stageBack(chatId);
            case "main":
                PipelineFactory.nullFactory();
                return this.mainProcess(chatId, "What can I help???");
            default:
                return queueProcess(message, chatId);
        }
    }

    private SendMessage stageBack(String chatId) {
        if (pipelineFactory.backStep()) {
            PipelineFactory.nullFactory();
            return this.mainProcess(chatId, "Something else?");
        }
        return factoryStep(chatId);
    }

    private SendMessage queueProcess(String message, String chatId) {
        this.pipelineFactory = PipelineFactory.getInstance();
        boolean result = pipelineFactory.addStep(message);
        if (result) {
            Pipeline pipeline = pipelineFactory.buildPipeline();
            PipelineFactory.nullFactory();
            //TODO save pipeline into DB
            return mainProcess(
                    chatId,
                    String.format(
                            "Your pipeline: [name: %s , repository name: %s , token: %s",
                            pipeline.getName(),
                            pipeline.getRepositoryName(),
                            pipeline.getOauthToken()
                    )
            );
        } else {
            return factoryStep(chatId);
        }
    }

    private SendMessage factoryStep(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, pipelineFactory.size());
        setOneRowButtons(sendMessage,"Main","One stage back <--");
        return sendMessage;
    }
}
