package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.exceptions.BuildFileNotFoundException;
import com.trelloiii.cibot.model.Pipeline;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class PipelineYamlParser {
    private final Yaml yaml;
    private final String path;
    private final String name;
    private Pipeline pipeline;

    public PipelineYamlParser(Pipeline pipeline) {
        this.yaml = new Yaml();
        this.name = pipeline.getRepositoryName();
        this.path = "./" + name + "/build.yaml";
        this.pipeline = pipeline;
    }

    public Pipeline parse() {
        try {
            Map<String, Object> map = yaml.load(new FileInputStream(path));
            PipelineConfiguration pipelineConfiguration = configurationParser(map);
            pipeline.setConfiguration(pipelineConfiguration);

            List<Stage> stages = new ArrayList<>(parseStages(map, pipelineConfiguration));
            stages.addAll(notRequiredStages(pipelineConfiguration));
            stages.add(addAfterUserStages());
            pipeline.setStages(stages);

            return pipeline;
        } catch (FileNotFoundException e) {
            throw new BuildFileNotFoundException();
        }
    }
    public List<Stage> parseStages(Map<String, Object> map,PipelineConfiguration pipelineConfiguration){
        String dist = pipelineConfiguration.getDist();
        List<Stage> stages = new LinkedList<>();
        LinkedHashMap<String, Object> parsedStages = (LinkedHashMap<String, Object>) map.get("stages");

        for (Map.Entry<String, Object> entry : parsedStages.entrySet()) {
            Stage stage = new Stage();
            stage.setName(entry.getKey());
            LinkedHashMap<String, Object> instructions = (LinkedHashMap<String, Object>) entry.getValue();
            List<String> strInstructions = (List<String>) instructions.get("instructions");
            List<Instruction> instructionList = new ArrayList<>();
            for (String inst : strInstructions) {
                instructionList.add(new Instruction(inst, dist));
            }
            stage.setInstructions(instructionList);
            stages.add(stage);
        }
        return stages;
    }
    public PipelineConfiguration configurationParser(Map<String, Object> map) {
        String dist = (String) map.get("dist");
        String moveTo = (String) map.get("moveTo");
        String target = (String) map.get("target");
        return PipelineConfiguration.builder()
                .dist(dist == null ? name : dist)
                .moveTo(moveTo)
                .target(target)
                .build();
    }

    public List<Stage> notRequiredStages(PipelineConfiguration pipelineConfiguration) {
        List<Stage> result = new ArrayList<>();
        String moveTo = pipelineConfiguration.getMoveTo();
        String target = pipelineConfiguration.getTarget();
        String dist = pipelineConfiguration.getDist();
        if (moveTo!=null&&target!=null) {
            Stage stage = new Stage();
            stage.setName("moveTo");
            stage.setSystem(true);
            String flags="";
            File check=new File(dist);
            if(check.isDirectory())
                flags="-r";
            stage.setInstructions(Collections.singletonList(
                    new Instruction(String.format("cp -f %s %s/%s %s",flags, dist,target, moveTo), "./"))
            );
            result.add(stage);
        }
        return result;
    }

    @Deprecated
    private Stage addBeforeUserStages() {
        Stage finalStage = new Stage();
        finalStage.setSystem(true);
        finalStage.setInstructions(Collections.singletonList(
                new Instruction(String.format("cd %s", name), "./")
        ));
        finalStage.setName("ss");
        return finalStage;
    }

    private Stage addAfterUserStages() {
        Stage afterStage = new Stage();
        afterStage.setSystem(true);
        afterStage.setName("ss1");
        afterStage.setInstructions(Collections.singletonList(
                new Instruction(String.format("rm -r %s", name), "./"))
        );
        return afterStage;
    }
}
