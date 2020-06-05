package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.dto.logger.LogExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stage {
    private String name;
    private List<Instruction> instructions;
    private Boolean status;
    private Boolean system = false;

    @SneakyThrows
    public int execute(LogExecutor logExecutor) {
        if (!system)
            logExecutor.sendLog(String.format("Starting stage: %s", this.name.toUpperCase()));
        for (Instruction instruction : instructions) {
            int code = instruction.execute(logExecutor);
            status = code == 0;
            if(code!=0)
                return code;
        }
        return 0;
    }
}
