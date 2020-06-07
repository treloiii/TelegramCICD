package com.trelloiii.cibot.dto.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trelloiii.cibot.dto.pipeline.BuildStarter;
import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.model.PipelineHistory;
import com.trelloiii.cibot.service.PipelineHistoryService;
import com.trelloiii.cibot.service.PipelineService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Component
public class MessagesFork extends AbstractFork {
    private final PipelineService pipelineService;
    final ObjectMapper objectMapper;
    private final PipelineHistoryService pipelineHistoryService;
    @Autowired
    public MessagesFork(PipelineService pipelineService, ObjectMapper objectMapper, PipelineHistoryService pipelineHistoryService) {
        this.pipelineService = pipelineService;
        this.objectMapper = objectMapper;
        this.pipelineHistoryService = pipelineHistoryService;
    }

    @Override
    public List<SendMessage> process(String message, String chatId) {
        switch (message){
            case "Create pipeline":
                return Collections.singletonList(createPipeline(chatId));
            case "show my pipelines":
                return showPipelines(chatId);
            case "main":
            case "start":
            default:
                return mainProcess(chatId,"What can I help you?");
        }
    }
    private String fixedString(String s,String joiner){
        StringBuilder sb=new StringBuilder();
        sb.append(s);
        int len=joiner.equals(" ")?50:150;
        if (s.length()<len) {
            for (int i = 0; i < len-s.length(); i++) {
                sb.append(joiner);
            }
        }
        return sb.toString();
    }
    @Override
    public List<SendMessage> processCallback(String message, String data, String chatId, Consumer<SendMessage> sendMessageConsumer) {
        switch (message){
            case "start":
                return BuildStarter.start(pipelineService, data, chatId, sendMessageConsumer);
            case "history":
                Pipeline pipeline=pipelineService.getPipeline(data);
                List<PipelineHistory> pipelineHistory=pipelineHistoryService.getHistoryByPipeline(pipeline);
                SendMessage sendMessage=new SendMessage();
                sendMessage.enableMarkdown(true);
                sendMessage.setChatId(chatId);
                String head=String.format("_history of %s_",pipeline.getName());
                if(pipelineHistory.size()>0) {
                    String tableHat=String.join(" | ",
                            fixedString("Executed at"," "),
                            fixedString("Status"," "),
                            fixedString("Failed stage"," "),
                            fixedString("Failed command"," "));
                    String tableDelimeter=fixedString("","-");
                    StringBuilder stringBuilder=new StringBuilder();
                    stringBuilder.append(head).append("\n");
                    stringBuilder.append(tableHat).append("\n").append(tableDelimeter).append("\n");
                    for(PipelineHistory history:pipelineHistory){
                        stringBuilder.append(String.join(
                                " | ",
                                fixedString(history.getExecutedAt().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm"))," "),
                                fixedString(!history.getStatus() ? "success" : "failed"," "),
                                fixedString(history.getFailed_stage()==null?"":history.getFailed_stage()," "),
                                fixedString(history.getFailed_instruction()==null?"":history.getFailed_instruction()," ")
                        ))
                        .append("\n");
                    }
                    sendMessage.setText(stringBuilder.toString());
                }
                else{
                    sendMessage.setText(head+"\n"+"*EMPTY*");
                }
                return Collections.singletonList(sendMessage);
            case "delete":
            default:
                return Collections.singletonList(
                        new SendMessage(
                                chatId,
                                "delete"
                        )
                );
        }
    }

    private List<SendMessage> showPipelines(String chatId) {
        List<Pipeline> pipelineList=pipelineService.getPipelines();
        List<SendMessage> result=new ArrayList<>();
        for(Pipeline pipeline:pipelineList){
            SendMessage sendMessage=
                    new SendMessage(
                            chatId,
                            String.format(
                                    "%s: [repository: %s , token: %s]",
                                    pipeline.getName(),
                                    pipeline.getRepositoryName(),
                                    pipeline.getOauthToken()
                            )
                    );
            pipelineInline(sendMessage,pipeline);
            result.add(sendMessage);
        }
        return result;
    }

    @SneakyThrows
    private void pipelineInline(SendMessage sendMessage, Pipeline pipeline) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        buttons1.add(new InlineKeyboardButton().setText("start")
                .setCallbackData("msg&start&"+pipeline.getId().toString()));
        buttons1.add(new InlineKeyboardButton().setText("delete")
                .setCallbackData("msg&delete&"+pipeline.getId().toString()));
        buttons1.add(new InlineKeyboardButton().setText("history")
                .setCallbackData("msg&history&"+pipeline.getId().toString()));
        buttons.add(buttons1);
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);
        sendMessage.setReplyMarkup(markupKeyboard);
    }


    private SendMessage createPipeline(String chatId) {
        PipelineFactory.instance();
        SendMessage sendMessage=new SendMessage(chatId,"Lets define name of a pipeline");
        setOneRowButtons(sendMessage,"Main","One stage back <--");
        return sendMessage;
    }
}
