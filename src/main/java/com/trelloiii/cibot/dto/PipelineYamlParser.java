package com.trelloiii.cibot.dto;

import com.trelloiii.cibot.model.pipeline.Instruction;
import com.trelloiii.cibot.model.pipeline.Pipeline;
import com.trelloiii.cibot.model.pipeline.PipelineConfiguration;
import com.trelloiii.cibot.model.pipeline.Stage;
import lombok.val;
import org.yaml.snakeyaml.Yaml;
import sun.nio.cs.ext.IBM300;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PipelineYamlParser {
    private final Yaml yaml;
    private final String path;
    private final String name;
    public PipelineYamlParser(String path,String name) {
        this.yaml = new Yaml();
        this.path = path;
        this.name=name;
    }
    public Pipeline parse(){
        try {
            Map<String, Object> map = yaml.load(new FileInputStream(path));
            System.out.println(map);
            val pipeline = new Pipeline();
            val configuration = PipelineConfiguration.builder()
                    .dist((String) map.get("dist"))
                    .moveTo((String) map.get("moveTo"))
                    .name((String) map.get("name"))
                    .build();
            pipeline.setConfiguration(configuration);
            List<Stage> stages = new LinkedList<>();
            val stages1 = (LinkedHashMap<String, Object>) map.get("stages");
            for (Map.Entry<String, Object> entry : stages1.entrySet()) {
                Stage stage = new Stage();
                stage.setName(entry.getKey());
                val instructions = (LinkedHashMap<String, Object>) entry.getValue();
                val strInstructions = (List<String>) instructions.get("instructions");
                List<Instruction> instructionList = new ArrayList<>();
                for (val inst : strInstructions) {
                    instructionList.add(new Instruction(inst,String.format("./%s",name)));
                }
                stage.setInstructions(instructionList);
                stages.add(stage);
            }
            stages.add(addAfterUserStages());
            pipeline.setStages(stages);
            pipeline.setName(name);
            return pipeline;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Deprecated
    private Stage addBeforeUserStages() {
        Stage finalStage=new Stage();
        finalStage.setSystem(true);
        finalStage.setInstructions(Collections.singletonList(
                new Instruction(String.format("cd %s",name),"./")
        ));
        finalStage.setName("ss");
        return finalStage;
    }
    private Stage addAfterUserStages() {
        Stage afterStage=new Stage();
        afterStage.setSystem(true);
        afterStage.setName("ss1");
        afterStage.setInstructions(Collections.singletonList(
                new Instruction(String.format("rm -r %s",name),"./"))
        );
        return afterStage;
    }
}
