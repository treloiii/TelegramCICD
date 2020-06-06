package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.dto.pipeline.instruction.CopyJavaInstruction;
import com.trelloiii.cibot.dto.pipeline.instruction.Instruction;
import com.trelloiii.cibot.dto.pipeline.instruction.NativeUnixInstruction;
import com.trelloiii.cibot.dto.pipeline.instruction.RemoveJavaInstruction;
import com.trelloiii.cibot.exceptions.BuildFileNotFoundException;
import com.trelloiii.cibot.model.Pipeline;
import lombok.val;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class PipelineYamlParser {
    private final Yaml yaml;
    private final String path;
    private final String name;
    private final Pipeline pipeline;

    public PipelineYamlParser(Pipeline pipeline) {
        this.yaml = new Yaml();
        this.name = pipeline.getRepositoryName();
        this.path = name + "/build.yaml";
        this.pipeline = pipeline;
    }

    public Pipeline parse() {
        try {
            Map<String, Object> map = yaml.load(new FileInputStream(path));
            Map<String,Object> pipelineConfiguration = configurationParser(map);
            pipeline.setConfiguration(pipelineConfiguration);

            List<Stage> stages = new ArrayList<>(parseStages(map, pipelineConfiguration));

            if((Boolean)pipelineConfiguration.get("delete_after"))
                stages.add(deleteAfterBuild());
            pipeline.setStages(stages);

            return pipeline;
        } catch (FileNotFoundException e) {
            throw new BuildFileNotFoundException();
        }
    }
    public List<Stage> parseStages(Map<String, Object> map,Map<String,Object> pipelineConfiguration){
        List<Stage> stages = new LinkedList<>();
        Map<String, Object> parsedStages = (Map<String, Object>) map.get("stages");

        for (Map.Entry<String, Object> entry : parsedStages.entrySet()) {
            Stage stage = new Stage();
            stage.setName(entry.getKey());

            Map<String, Object> namedInstructions = (Map<String, Object>) entry.getValue();
            List<Object> instructions = (List<Object>) namedInstructions.get("instructions");
            List<Instruction> instructionList = new ArrayList<>();
            for (Object inst : instructions) {
                val instructionPair=(Map<String,Object>) inst;
                for(Map.Entry<String,Object> instructionEntry: instructionPair.entrySet()){
                    String key=instructionEntry.getKey();
                    if(key.equals("sh")){
                        instructionList.add(new NativeUnixInstruction((String)instructionEntry.getValue(), name));
                    }else if(key.equals("copy")){
                        val copyBlock=(Map<String,Object>) instructionEntry.getValue();
                        instructionList.add(
                                new CopyJavaInstruction(name,(String)copyBlock.get("target"),(String)copyBlock.get("dist"))
                        );
                    }
                }
            }
            stage.setInstructions(instructionList);
            stages.add(stage);
        }
        return stages;
    }
    public LinkedHashMap<String,Object> configurationParser(Map<String, Object> map) {
         return (LinkedHashMap<String, Object>) map.get("configuration");
    }
    private Stage deleteAfterBuild() {
        Stage afterStage = new Stage();
        afterStage.setSystem(true);
        afterStage.setName("remove_after");
        afterStage.setInstructions(Collections.singletonList(new RemoveJavaInstruction(name)));
        return afterStage;
    }
}
