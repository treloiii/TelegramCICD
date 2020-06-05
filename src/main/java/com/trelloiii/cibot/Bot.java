package com.trelloiii.cibot;

import com.trelloiii.cibot.dto.message.ForkProcessor;
import com.trelloiii.cibot.dto.pipeline.LoggablePipeline;
import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
import com.trelloiii.cibot.service.PipelineService;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.service.UserService;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@Component
public class Bot extends TelegramLongPollingBot {
    private final UserService userService;
    @Value("${github.token}")
    private String token;
    @Value("${bot.token}")
    private String botToken;
    private final PipelineService pipelineService;
    private PipelineFactory pipelineFactory = null;
    private final ForkProcessor forkProcessor;

    @Autowired
    public Bot(UserService userService, PipelineService pipelineService, ForkProcessor forkProcessor) {
        this.userService = userService;
        this.pipelineService = pipelineService;
        this.forkProcessor = forkProcessor;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("new message!");
        List<SendMessage> sendMessage;
        if (update.hasCallbackQuery())
            sendMessage = forkProcessor.processCallBack(update.getCallbackQuery(),sm -> {
                try {
                    execute(sm);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            });
        else
            sendMessage = forkProcessor.processMessage(update.getMessage());
        for (SendMessage message : sendMessage) {
            execute(message);
        }


//        User user = update.getMessage().getFrom();
//        if (!userService.checkIfExists(user)) {
//            userService.saveUser(user);
//            //TODO переделать логгер сервис
//        } else {

//        }
    }




    @Override
    public String getBotUsername() {
        return "project_ci_bot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
