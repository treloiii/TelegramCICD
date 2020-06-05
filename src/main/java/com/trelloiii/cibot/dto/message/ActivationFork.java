package com.trelloiii.cibot.dto.message;

import com.trelloiii.cibot.model.Root;
import com.trelloiii.cibot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class ActivationFork extends AbstractFork {
    private final UserService userService;
    private User user;
    public ActivationFork(UserService userService) {
        this.userService = userService;
    }

    @Override
    public SendMessage process(String message, String chatId) {
        Root root = userService.getRoot();
        if(root.isActivated()){//слать нахуй
            return new SendMessage(chatId, "Sorry, you don't have permission to this bot :(");
        }else {//пытаться активировать
            if (message.equals(root.getPassword())) {
                userService.saveUser(user);
                SendMessage sendMessage = new SendMessage(chatId, "Your bot is active now!\nWhat can I help for u?");
                setOneRowButtons(sendMessage, "Main", "CreatePipeline");
                return sendMessage;
            }
            else if(message.equals("generate new password")){
                userService.generateNewRootPassword();
                return new SendMessage(chatId,"New password generated.\n" +
                        "You can find it in working directory of bot on the server");
            }
            else {
                SendMessage sendMessage=new SendMessage(chatId, "This bot needs to be activated.\n" +
                        "Root password for activation you can find in bot working directory on server\n" +
                        "Enter password to activate bot");
                setOneRowButtons(sendMessage, "generate new password");
                return sendMessage;
            }
        }
    }

    public void setFrom(User from) {
        this.user=from;
    }
}
