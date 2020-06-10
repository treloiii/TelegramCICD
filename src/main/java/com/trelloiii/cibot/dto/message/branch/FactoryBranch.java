package com.trelloiii.cibot.dto.message.branch;

import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.service.PipelineService;
import com.trelloiii.cibot.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.function.Consumer;

@Component
public class FactoryBranch extends AbstractBranch {
    private PipelineFactory pipelineFactory = PipelineFactory.getInstance();
    private final PipelineService pipelineService;
    private final UserService userService;

    public FactoryBranch(PipelineService pipelineService, UserService userService) {
        this.pipelineService = pipelineService;
        this.userService = userService;
    }

    @Override
    public void process(Message message) {
        String messageText=message.getText();
        Long chatId=message.getChatId();
        switch (messageText) {
            case "One stage back <--":
                stageBack(chatId);
                break;
            case "Cancel":
                PipelineFactory.nullFactory();
                send(mainProcess(chatId, "What can I help???"));
                break;
            default:
                queueProcess(message);
        }
    }

    private void stageBack(Long chatId) {
        if (pipelineFactory.backStep()) {
            PipelineFactory.nullFactory();
            send(mainProcess(chatId, "Something else?"));
        }
        send(factoryStep(chatId));
    }

    private void queueProcess(Message tmMessage) {
        this.pipelineFactory = PipelineFactory.getInstance();
        boolean result = pipelineFactory.addStep(tmMessage.getText());
        Long chatId=tmMessage.getChatId();
        SendMessage message;
        if (result) {
            Pipeline pipeline = pipelineFactory.buildPipeline();
            pipeline.setOwner(userService.mapFromTelegram(tmMessage.getFrom()));
            PipelineFactory.nullFactory();
            try {
                pipelineService.savePipeline(pipeline);
                message = mainProcess(
                        chatId,
                        String.format(
                                "Your pipeline: [name: %s , repository name: %s , token: %s]",
                                pipeline.getName(),
                                pipeline.getRepositoryName(),
                                pipeline.getOauthToken()
                        )
                );
            }
            catch (DataIntegrityViolationException e){
                message = mainProcess(chatId,
                        String.format(
                                "Duplicate repository name '%s' in pipeline, pipeline was not created",
                                pipeline.getRepositoryName()
                        )
                );
            }
        } else {
            message=factoryStep(chatId);
        }
        send(message);
    }

    private SendMessage factoryStep(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, pipelineFactory.size());
        setOneRowButtons(sendMessage,"Cancel","One stage back <--");
        return sendMessage;
    }
}
