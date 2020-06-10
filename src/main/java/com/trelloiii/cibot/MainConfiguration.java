package com.trelloiii.cibot;

import com.trelloiii.cibot.dto.message.events.CallbackEventEventListener;
import com.trelloiii.cibot.dto.message.events.MessageEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.function.Consumer;

@Configuration
public class MainConfiguration {
    @Value("${proxy.host}")
    private String proxyHost;
    @Value("${proxy.port}")
    private String proxyPort;
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public MessageEventListener messageEventListener(Consumer<SendMessage> sendMessageConsumer){
        return new MessageEventListener(sendMessageConsumer);
    }
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public CallbackEventEventListener callbackEventEventListener(Consumer<SendMessage> sendMessageConsumer){
        return new CallbackEventEventListener(sendMessageConsumer);
    }
}
