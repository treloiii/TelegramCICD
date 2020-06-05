package com.trelloiii.cibot.dto.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFork implements Fork {
    public SendMessage mainProcess(String chatId,String message){
        SendMessage sendMessage=new SendMessage(chatId,message);
        setOneRowButtons(sendMessage,"Help","Create pipeline");
        return sendMessage;
    }
    private ReplyKeyboardMarkup getKeyboardMarkup(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return  replyKeyboardMarkup;
    }
    public synchronized void setOneRowButtons(SendMessage sendMessage,String... names) {
        ReplyKeyboardMarkup replyKeyboardMarkup=getKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        for(String name:names)
            keyboardFirstRow.add(new KeyboardButton(name));
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

}
