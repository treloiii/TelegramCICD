package com.trelloiii.cibot.dto.message.branch;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBranch implements Branch {
    public static final String HELP="Help";
    public static final String CREATE_PIPELINE="Create pipeline";
    public static final String SHOW_PIPELINES="Show pipelines";
    public SendMessage mainProcess(Long chatId,String message){
        SendMessage sendMessage=new SendMessage(chatId,message);
        setOneRowButtons(sendMessage,HELP,CREATE_PIPELINE,SHOW_PIPELINES);
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
