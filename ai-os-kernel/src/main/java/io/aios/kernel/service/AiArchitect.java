package io.aios.kernel.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

public interface AiArchitect {

    @SystemMessage("You are a senior code reviewer. Analyze the given Groovy code and return a concise JSON summary of what it does, its inputs, and its outputs.")
    String explainCode(String sourceCode);
}
