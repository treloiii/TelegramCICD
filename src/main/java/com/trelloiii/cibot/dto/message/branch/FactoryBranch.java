package com.trelloiii.cibot.dto.message.branch;

import com.trelloiii.cibot.dto.message.branch.AbstractBranch;
import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.service.PipelineService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.function.Consumer;

@Component
public class FactoryBranch extends AbstractBranch {
    private PipelineFactory pipelineFactory = PipelineFactory.getInstance();
    private final PipelineService pipelineService;

//    {
//        Map<String, Function<String, List<SendMessage>>> variants = new HashMap<>();
//        variants.put("one stage back", this::stageBack);
//        variants.put("main",mainProcess())
//    }
    public FactoryBranch(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @Override
    public void process(Message message, Consumer<SendMessage> sendMessage) {
        String messageText=message.getText();
        Long chatId=message.getChatId();
        switch (messageText) {
            case "One stage back <--":
                stageBack(chatId,sendMessage);
                break;
            case "main":
                PipelineFactory.nullFactory();
                mainProcess(chatId, "What can I help???");
                break;
            default:
                queueProcess(messageText, chatId,sendMessage);
        }
    }

    private void stageBack(Long chatId,Consumer<SendMessage> sendMessage) {
        if (pipelineFactory.backStep()) {
            PipelineFactory.nullFactory();
            sendMessage.accept(mainProcess(chatId, "Something else?"));
        }
        sendMessage.accept(factoryStep(chatId));
    }

    private void queueProcess(String text, Long chatId,Consumer<SendMessage> sendMessage) {
        this.pipelineFactory = PipelineFactory.getInstance();
        boolean result = pipelineFactory.addStep(text);
        SendMessage message;
        if (result) {
            Pipeline pipeline = pipelineFactory.buildPipeline();
            PipelineFactory.nullFactory();
            pipelineService.savePipeline(pipeline);
            message=mainProcess(
                    chatId,
                    String.format(
                            "Your pipeline: [name: %s , repository name: %s , token: %s]",
                            pipeline.getName(),
                            pipeline.getRepositoryName(),
                            pipeline.getOauthToken()
                    )
            );
        } else {
            message=factoryStep(chatId);
        }
        sendMessage.accept(message);
    }

    private SendMessage factoryStep(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, pipelineFactory.size());
        setOneRowButtons(sendMessage,"Main","One stage back <--");
        return sendMessage;
    }
}
