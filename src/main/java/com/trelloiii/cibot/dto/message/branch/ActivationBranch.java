package com.trelloiii.cibot.dto.message.branch;

import com.trelloiii.cibot.dto.message.branch.AbstractBranch;
import com.trelloiii.cibot.model.Root;
import com.trelloiii.cibot.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.function.Consumer;

@Component
public class ActivationBranch extends AbstractBranch {
    private final UserService userService;
    public ActivationBranch(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void process(Message incoming) {
        String text=incoming.getText();
        Long chatId=incoming.getChatId();
        Root root = userService.getRoot();
        SendMessage message;
        if(root.isActivated()){//слать нахуй
            message=new SendMessage(chatId, "Sorry, you don't have permission to this bot :(");
        }else {//пытаться активировать
            if (text.equals(root.getPassword())) {
                userService.saveUser(incoming.getFrom());
                message = new SendMessage(chatId, "Your bot is active now!\nWhat can I help for u?");
                setOneRowButtons(message, HELP, CREATE_PIPELINE);
            }
            else if(text.equals("generate new password")){
                userService.generateNewRootPassword();
                message=new SendMessage(chatId, "New password generated.\n" +
                        "You can find it in working directory of bot on the server");
            }
            else {
                message=new SendMessage(chatId, "This bot needs to be activated.\n" +
                        "Root password for activation you can find in bot working directory on server\n" +
                        "Enter password to activate bot");
                setOneRowButtons(message, "generate new password");
            }
        }
        send(message);
    }
}
