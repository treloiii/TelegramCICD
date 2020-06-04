package com.trelloiii.cibot.dto;

import com.trelloiii.cibot.model.pipeline.Pipeline;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.function.Consumer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggablePipeline {
    private Pipeline pipeline;
    private Consumer<SendMessage> sendMessageConsumer;
    private String id;
}
