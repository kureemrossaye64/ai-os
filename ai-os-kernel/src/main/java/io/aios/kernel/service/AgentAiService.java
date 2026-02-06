package io.aios.kernel.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

public interface AgentAiService {

    @SystemMessage("You are a system planner. Decide the next action for the given goal.")
    String plan(@UserMessage String userGoal);

    @SystemMessage("You are an expert Groovy developer. Write code to achieve the requirements.")
    String generateCode(@UserMessage String requirements);

    @SystemMessage("You are a debugger. Fix the provided code based on the error.")
    String fixCode(@V("ERROR") String error, @V("CODE") String code);
}
