package com.trelloiii.cibot;

import com.trelloiii.cibot.dto.message.events.CallbackEventEventListener;
import com.trelloiii.cibot.dto.message.events.MessageEventListener;
import com.trelloiii.cibot.dto.vcs.VCSWatcher;
import com.trelloiii.cibot.model.Pipeline;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
public class MainConfiguration {
    @Value("${proxy.host}")
    private String proxyHost;
    @Value("${proxy.port}")
    private String proxyPort;
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public MessageEventListener messageEventListener(Function<Object, Message> sendMessageConsumer){
        return new MessageEventListener(sendMessageConsumer);
    }
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public CallbackEventEventListener callbackEventEventListener(Function<Object, Message> sendMessageConsumer){
        return new CallbackEventEventListener(sendMessageConsumer);
    }
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public VCSWatcher watcher(Pipeline pipeline){
        return new VCSWatcher(pipeline);
    }
}
