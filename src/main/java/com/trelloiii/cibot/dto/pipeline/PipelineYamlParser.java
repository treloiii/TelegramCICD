package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.dto.pipeline.instruction.CopyJavaInstruction;
import com.trelloiii.cibot.dto.pipeline.instruction.Instruction;
import com.trelloiii.cibot.dto.pipeline.instruction.NativeUnixInstruction;
import com.trelloiii.cibot.dto.pipeline.instruction.RemoveJavaInstruction;
import com.trelloiii.cibot.exceptions.BuildFileNotFoundException;
import com.trelloiii.cibot.exceptions.EnvironmentNotFoundException;
import com.trelloiii.cibot.model.Pipeline;
import lombok.SneakyThrows;
import lombok.val;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Pipeline parse() throws EnvironmentNotFoundException {
        try {
            parseEnv(path);
            Map<String, Object> map = yaml.load(new FileInputStream(path));
            Map<String, Object> pipelineConfiguration = configurationParser(map);
            pipeline.setConfiguration(pipelineConfiguration);

            List<Stage> stages = new ArrayList<>(parseStages(map, pipelineConfiguration));

            if ((Boolean) pipelineConfiguration.get("delete_after"))
                stages.add(systemAfterBuild());
            pipeline.setStages(stages);

            return pipeline;
        } catch (IOException e) {
            throw new BuildFileNotFoundException();
        }
    }


    public void parseEnv(String path) throws IOException, EnvironmentNotFoundException {
        File yaml=new File(path);
        List<String> lines= Files.lines(yaml.toPath()).collect(Collectors.toList());
        String content1=String.join("\n",lines);
        Pattern pattern= Pattern.compile("%%(.*?)%%",Pattern.DOTALL);
        Matcher matcher=pattern.matcher(content1);
        StringBuffer result=new StringBuffer();
        while (matcher.find()){
            String env=matcher.group(1);
            String replacement=System.getenv(env);
            try {
                matcher.appendReplacement(result, replacement);
            }
            catch (NullPointerException e){
                throw new EnvironmentNotFoundException(String.format("Environment %s not found",env));
            }
        }
        matcher.appendTail(result);
        try(PrintWriter printWriter=new PrintWriter(new FileWriter(yaml))){
            printWriter.print(result.toString());
        }
    }

    public List<Stage> parseStages(Map<String, Object> map, Map<String, Object> pipelineConfiguration) {
        List<Stage> stages = new LinkedList<>();
        Map<String, Object> parsedStages = (Map<String, Object>) map.get("stages");

        for (Map.Entry<String, Object> entry : parsedStages.entrySet()) {
            Stage stage = new Stage();
            stage.setName(entry.getKey());

            Map<String, Object> namedInstructions = (Map<String, Object>) entry.getValue();
            List<Object> instructions = (List<Object>) namedInstructions.get("instructions");
            List<Instruction> instructionList = new ArrayList<>();
            for (Object inst : instructions) {
                val instructionPair = (Map<String, Object>) inst;
                for (Map.Entry<String, Object> instructionEntry : instructionPair.entrySet()) {
                    String key = instructionEntry.getKey();
                    switch (key) {
                        case "sh":
                            instructionList.add(new NativeUnixInstruction((String) instructionEntry.getValue(),name));
                            break;
                        case "ish":
                            instructionList.add(new NativeUnixInstruction((String) instructionEntry.getValue(),name,true));
                            break;
                        case "copy":
                            val copyBlock = (Map<String, Object>) instructionEntry.getValue();
                            instructionList.add(
                                    new CopyJavaInstruction(name, (String) copyBlock.get("target"), (String) copyBlock.get("dist"))
                            );
                            break;
                    }
                }
            }
            stage.setInstructions(instructionList);
            stages.add(stage);
        }
        return stages;
    }

    public Map<String, Object> configurationParser(Map<String, Object> map) {
        Map<String, Object> res = (LinkedHashMap<String, Object>) map.get("configuration");
        if (res == null) {
            res = new LinkedHashMap<>();
        }
        res.putIfAbsent("delete_after", true);
        return res;
    }

    private Stage systemAfterBuild() {
        Stage afterStage = new Stage();
        afterStage.setSystem(true);
        afterStage.setName("sys_after");
        afterStage.setInstructions(Collections.singletonList(new RemoveJavaInstruction(name)));
        return afterStage;
    }
}
