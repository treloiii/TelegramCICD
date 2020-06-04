package com.trelloiii.cibot.dto.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFork implements Fork {
    public SendMessage mainProcess(String chatId,String message){
        SendMessage sendMessage=new SendMessage(chatId,message);
        setStartupButtons(sendMessage);
        return sendMessage;
    }
    public ReplyKeyboardMarkup getKeyboardMarkup(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return  replyKeyboardMarkup;
    }
    public synchronized void setStartupButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup=getKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Help"));
        keyboardFirstRow.add(new KeyboardButton("Create pipeline"));

        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    public synchronized void setPipelineButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup=getKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Main"));
        keyboardFirstRow.add(new KeyboardButton("One stage back <--"));

        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
    }
}
