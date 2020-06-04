package com.trelloiii.cibot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfiguration {

    @Bean(initMethod = "startup")
    public BotStartup botStartup(){
        return new BotStartup();
    }
}
