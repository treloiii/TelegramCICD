package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.model.Pipeline;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class PipelineFactory {
    Deque<String> steps=new LinkedList<>();
    private static PipelineFactory pipelineFactory=null;
    private PipelineFactory(){}
    public static boolean haveInstance(){
        return pipelineFactory==null;
    }
    public static PipelineFactory getInstance(){
        return pipelineFactory;
    }
    public static void instance(){
        if(pipelineFactory==null)
            pipelineFactory=new PipelineFactory();
    }
    public static void nullFactory(){
        pipelineFactory=null;
    }
    public boolean addStep(String param){
        steps.addLast(param);
        if(steps.size()>=3){
            return true;
        }
        return false;
    }
    public boolean backStep(){
        if (steps.size()==0)
            return true;
        steps.pollLast();
        return false;
    }
    public String size(){
        switch (steps.size()){
            case 0:
                return "Enter pipeline name";
            case 1:
                return "Enter repository name";
            case 2:
                return "Enter oauth token";
            default:
                return "Error";
        }
    }
    public Pipeline buildPipeline(){
        String token=steps.pollLast();
        String repositoryName=steps.pollLast();
        String name=steps.pollLast();
        return new Pipeline(name,repositoryName,token);
    }

}
