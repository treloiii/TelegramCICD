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
        String message=update.getMessage().getText();
        String chatId=update.getMessage().getChatId().toString();
        SendMessage sendMessage=forkProcessor.processMessage(message,chatId);
        execute(sendMessage);
//        switch (message){
//            case "start":
//            case "main":
//                sendMessage=new SendMessage(chatId,"What can I help???");
//                setStartupButtons(sendMessage);
//                execute(sendMessage);
//                break;
//            case "Create pipeline":
//                pipelineFactory=new PipelineFactory();
//                sendMessage=new SendMessage(chatId,"Lets define name of a pipeline");
//                setPipelineButtons(sendMessage);
//                execute(sendMessage);
//                break;
//            default:
//                if(pipelineFactory!=null){
//                    if(message.equals("One stage back <--")){
//                        pipelineFactory.backStep();
//                        sendMessage=new SendMessage(chatId,pipelineFactory.size());
//                        setPipelineButtons(sendMessage);
//                        execute(sendMessage);
//                    }
//                    else {
//                        boolean result = pipelineFactory.addStep(message);
//                        if (result) {
//                            Pipeline pipeline = pipelineFactory.buildPipeline();
//                            pipelineFactory = null;
//                            sendMessage = new SendMessage(chatId, "Your pipeline successfully created!");
//                            setStartupButtons(sendMessage);
//                            execute(sendMessage);
//                            //SAVE PIPELINE IN DB
//                        } else {
//                            sendMessage = new SendMessage(chatId, pipelineFactory.size());
//                            setPipelineButtons(sendMessage);
//                            execute(sendMessage);
//                        }
//                    }
//                }
//                else{
//                    sendMessage=new SendMessage(chatId,"What can I help???");
//                    setStartupButtons(sendMessage);
//                    execute(sendMessage);
//                }
//        }
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

    private ReplyKeyboardMarkup getKeyboardMarkup(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return  replyKeyboardMarkup;
    }
    private synchronized void setStartupButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup=getKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Help"));
        keyboardFirstRow.add(new KeyboardButton("Create pipeline"));

        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
    }
    private synchronized void setPipelineButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup=getKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Main"));
        keyboardFirstRow.add(new KeyboardButton("One stage back <--"));

        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
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
