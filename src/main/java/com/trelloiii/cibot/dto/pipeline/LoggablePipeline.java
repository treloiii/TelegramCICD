package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.dto.logger.AbstractLogger;
import com.trelloiii.cibot.dto.logger.Logger;
import com.trelloiii.cibot.model.Pipeline;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.function.Function;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggablePipeline implements ExecutablePipeline {
    private Pipeline pipeline;
    private Function<Object, Message> sendMessageFunction;
    private String id;
    private AbstractLogger logger;
    public void initLogger(){
        logger=new Logger(sendMessageFunction,id,pipeline);
    }
}
