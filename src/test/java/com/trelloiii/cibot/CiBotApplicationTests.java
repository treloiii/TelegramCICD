package com.trelloiii.cibot;

import com.trelloiii.cibot.dto.pipeline.instruction.CopyJavaInstruction;
import com.trelloiii.cibot.model.PipelineHistory;
import com.trelloiii.cibot.service.PipelineHistoryService;
import lombok.SneakyThrows;
import org.assertj.core.util.Files;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.ApiContextInitializer;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
class CiBotApplicationTests {
    @Autowired
    private PipelineHistoryService pipelineHistoryService;
    @BeforeAll
    public static void context(){
        ApiContextInitializer.init();
    }
    @Test
    public void checkCopyDirs(){
        CopyJavaInstruction copy=new CopyJavaInstruction(Files.currentFolder().getAbsolutePath(),"src/main/resources/templates","/Users/trelloiii/test-ci");
        copy.execute();
    }
}
class SpringOutTests{
    @Test
    public void matchingEnvFromBuildFileTest(){
        Pattern pattern= Pattern.compile("%%(.*?)%%",Pattern.DOTALL);
        Matcher matcher=pattern.matcher("test_prop: %%SOSI_JOPU%%");
        String res=matcher.replaceAll("SECRET");
        Assert.assertEquals("test_prop: SECRET",res);
    }
    @Test
    public void matchingByGroupsTest(){
        String text="test_prop: %%ENV_1%%\n" +
                "test_prop2: %%ENV_2%%\n" +
                "again_first: %%ENV_1%%\n";
        Pattern pattern= Pattern.compile("%%(.*?)%%",Pattern.DOTALL);
        Matcher matcher=pattern.matcher(text);
        StringBuffer result=new StringBuffer();
        while (matcher.find()){
            String env=matcher.group(1);
            String replacement="";
            switch (env){
                case "ENV_1":
                    replacement="password";
                    break;
                case "ENV_2":
                    replacement="root";
                    break;
            }
            matcher.appendReplacement(result,replacement);
        }
        matcher.appendTail(result);
        Assert.assertEquals("test_prop: password\n" +
                "test_prop2: root\n" +
                "again_first: password\n",result.toString());
    }
    @SneakyThrows
    @Test
    public void replaceInFileTest(){
        File actual=new File("src/main/resources/actual.yaml");
        File expected=new File("src/main/resources/expected.yaml");
        List<String> lines= java.nio.file.Files.lines(actual.toPath()).collect(Collectors.toList());
        String content1=String.join("\n",lines);
        Pattern pattern= Pattern.compile("%%(.*?)%%",Pattern.DOTALL);
        Matcher matcher=pattern.matcher(content1);
        StringBuffer result=new StringBuffer();
        while (matcher.find()){
            String env=matcher.group(1);
            String replacement="";
            switch (env){
                case "ROOT_PASS":
                    replacement="1234";
                    break;
                case "GITHUB_TOKEN":
                    replacement="token";
                    break;
                case "DIR_1":
                    replacement="dir";
                    break;
                case "DIR_2":
                    replacement="dir2";
                    break;
            }
            matcher.appendReplacement(result,replacement);
        }
        matcher.appendTail(result);
        try(PrintWriter printWriter=new PrintWriter(new FileWriter(actual))){
            printWriter.print(result.toString());
        }
        Assert.assertEquals(Files.contentOf(actual,"UTF-8"),Files.contentOf(expected,"UTF-8"));
        try(PrintWriter printWriter=new PrintWriter(new FileWriter(actual))){
            printWriter.print(content1);
        }
    }
}
