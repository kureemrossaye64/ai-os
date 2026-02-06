package io.aios.kernel.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class AgentState {
    private String userGoal;
    
    @Builder.Default
    private List<String> executionHistory = new ArrayList<>();
    
    private String currentSourceCode;
    private String currentClassName;
    private String lastError;
    
    @Builder.Default
    private int attemptCount = 0;
    
    @Builder.Default
    private boolean isSolved = false;
    
    private Object finalResult;

    public void addHistory(String entry) {
        this.executionHistory.add(entry);
    }
}
