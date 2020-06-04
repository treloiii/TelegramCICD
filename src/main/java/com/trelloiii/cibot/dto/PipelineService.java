package com.trelloiii.cibot.dto;

public class PipelineService {
    private final PipelineExecutor pipelineExecutor;
    private static PipelineService pipelineService;

    public static PipelineService getPipelineService(){
        if(pipelineService==null)
            return new PipelineService();
        return pipelineService;
    }
    private PipelineService() {
        pipelineExecutor=new PipelineExecutor();
        Thread consumer=new Thread(()->{
            try {
                pipelineExecutor.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        consumer.start();
    }

    public void execute(LoggablePipeline pipeline){
        new Thread(()-> {
            try {
                pipelineExecutor.addPipeline(pipeline);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
