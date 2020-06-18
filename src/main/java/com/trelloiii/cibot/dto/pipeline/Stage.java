package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.dto.logger.AbstractLogger;
import com.trelloiii.cibot.dto.logger.Logger;
import com.trelloiii.cibot.dto.pipeline.instruction.Instruction;
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

    public void execute(){
        instructions.forEach(Instruction::execute);
    }
    @SneakyThrows
    public int execute(AbstractLogger logger) {
        for (Instruction instruction : instructions) {
            if(system){
                instruction.execute();
                return 0;
            }
            int code = instruction.execute(logger);
            status = code == 0;

            if(code!=0)
                return code;
        }
        return 0;
    }
}
