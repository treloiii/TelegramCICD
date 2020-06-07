package com.trelloiii.cibot;

import com.trelloiii.cibot.model.Root;
import com.trelloiii.cibot.repository.RootRepository;
import com.trelloiii.cibot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class BotStartup {
//    @Autowired
//    private Bot bot;
//    @Autowired
//    private RootRepository rootRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    public void startup(){
//        TelegramBotsApi telegramBotsApi=new TelegramBotsApi();
//        try{
//            telegramBotsApi.registerBot(bot);
//            firstStartup();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//    public void firstStartup(){
//        if(rootRepository.findAll().isEmpty()){
//            Root root=new Root();
//            root.setId(UUID.randomUUID().toString());
//            root.setPassword(UUID.randomUUID().toString());
//            root.setActivated(false);
//            rootRepository.save(root);
//            File file=new File("./root_password");
//            try(FileWriter fileWriter=new FileWriter(file)){
//                fileWriter.write(root.getPassword());
//            }
//            catch (IOException e){
//                e.printStackTrace();
//            }
//        }
//    }


}
