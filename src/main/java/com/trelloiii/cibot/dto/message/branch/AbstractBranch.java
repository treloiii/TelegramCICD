package com.trelloiii.cibot.dto.message.branch;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.K;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractBranch implements Branch {
    public static final String HELP="Help";
    public static final String CREATE_PIPELINE="Create pipeline";
    public static final String SHOW_PIPELINES="Show pipelines";
    @Getter
    @Setter
    private Consumer<SendMessage> sendMessageConsumer;
    public void send(SendMessage sendMessage){
        sendMessageConsumer.accept(sendMessage);
    }

    public SendMessage mainProcess(Long chatId, String message){
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
        if(names.length<4) {
            keyboard.add(keyboardFirstRow);
        }else{
            twoLines(keyboard,keyboardFirstRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private void twoLines(List<KeyboardRow> keyboard, KeyboardRow keyboardFirstRow) {
        KeyboardRow row1=new KeyboardRow();
        row1.addAll(keyboardFirstRow.subList(0,keyboardFirstRow.size()/2));
        keyboard.add(row1);

        KeyboardRow row2=new KeyboardRow();
        row2.addAll(keyboardFirstRow.subList(keyboardFirstRow.size()/2,keyboardFirstRow.size()));
        keyboard.add(row2);
    }

}
