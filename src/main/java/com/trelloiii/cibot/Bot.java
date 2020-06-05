package com.trelloiii.cibot;

import com.trelloiii.cibot.dto.message.ForkProcessor;
import com.trelloiii.cibot.dto.pipeline.LoggablePipeline;
import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
import com.trelloiii.cibot.dto.pipeline.PipelineService;
import com.trelloiii.cibot.dto.pipeline.PipelineYamlParser;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.service.UserService;
import lombok.SneakyThrows;
import lombok.val;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {
    private final UserService userService;
    @Value("${github.token}")
    private String token;
    @Value("${bot.token}")
    private String botToken;
    private final PipelineService pipelineService;
    private PipelineFactory pipelineFactory=null;
    private final ForkProcessor forkProcessor;

    @Autowired
    public Bot(UserService userService, PipelineService pipelineService, ForkProcessor forkProcessor) {
        this.userService = userService;
        this.pipelineService=pipelineService;
        this.forkProcessor = forkProcessor;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("new message!");
        SendMessage sendMessage=forkProcessor.processMessage(update.getMessage());
        execute(sendMessage);

//        User user = update.getMessage().getFrom();
//        if (!userService.checkIfExists(user)) {
//            userService.saveUser(user);
//            //TODO переделать логгер сервис
//        } else {
//            String repoName = "test-ci";
//            try {
//                System.out.println("User exists!");
//                GitHub gitHub = GitHub.connectUsingOAuth(token);
//                val repos = gitHub.getMyself().getAllRepositories();
//                GHRepository repository = repos.get(repoName);
//                Process process = Runtime.getRuntime().exec(new String[]{"git", "clone", String.format("%s.git", repository.getHtmlUrl().toString())});
//                int res = process.waitFor();
//
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    System.out.println(line);
//                }
//                System.out.println(res);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            //^parse vcs
//
//            PipelineYamlParser parser = new PipelineYamlParser("/Users/trelloiii/Desktop/JavaProjects/ci-bot/" + repoName + "/build.yaml",repoName);
//            Pipeline pipeline = parser.parse();
//            pipeline.setName(repoName);
//            pipelineService.execute(generateLoggable(update, pipeline));
//        }
    }

    private LoggablePipeline generateLoggable(Update update, Pipeline pipeline) {
        LoggablePipeline loggablePipeline = new LoggablePipeline();
        loggablePipeline.setId(update.getMessage().getChatId().toString());
        loggablePipeline.setPipeline(pipeline);
        loggablePipeline.setSendMessageConsumer(sendMessage -> {
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
        return loggablePipeline;
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
