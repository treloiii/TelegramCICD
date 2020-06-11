package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.dto.logger.Logger;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.service.PipelineHistoryService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Log
@Component
public class PipelineExecutor {
    private final PipelineHistoryService pipelineHistoryService;
    private final BlockingQueue<ExecutablePipeline> pipelineQueue = new ArrayBlockingQueue<>(3);

    public PipelineExecutor(PipelineHistoryService pipelineHistoryService) {
        this.pipelineHistoryService = pipelineHistoryService;
    }

    public void addPipeline(LoggablePipeline pipeline) throws InterruptedException {
        pipelineQueue.put(pipeline);
    }

    public void execute() throws InterruptedException {
        while (true) {
            ExecutablePipeline executablePipeline = pipelineQueue.take();
            Pipeline pipeline = executablePipeline.getPipeline();
            List<Stage> stageList = pipeline.getStages();
            Stage failed = null;
            for (Stage stage : stageList) {
                int code = stage.execute(executablePipeline.getLogger());
                if (code != 0) {
                    if(!stage.getSystem()) { // if failed stage is system we need to run next stages bcz it system stages too!
                        failed = stage;
                        break;
                    }
                }
            }
            if(failed!=null){ // system go last, if has fail on user stages whe system stages must be running anyway
                stageList.stream()
                        .filter(Stage::getSystem)
                        .forEach(Stage::execute);
            }
            //TODO переделать финальный лог
//            SendMessage finalLog=new SendMessage(executablePipeline.getId(),"*BUILD COMPLETE*");
//            finalLog.enableMarkdown(true);
//            executablePipeline.getSendMessageConsumer().accept(finalLog);
            pipelineHistoryService.writePipelineHistory(pipeline, failed);
        }
    }
}
