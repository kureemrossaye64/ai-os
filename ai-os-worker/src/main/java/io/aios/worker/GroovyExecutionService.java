package io.aios.worker;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroovyExecutionService {

    private final SafeFileSystem safeFileSystem;
    private final BrowserService browserService;

    public Object execute(String code, String methodName, Map<String, String> args) {
        Binding binding = new Binding();
        // 1. Inject Standard Tools
        binding.setVariable("fs", safeFileSystem);

        // 2. Inject Browser (Create a fresh context for this execution)
        try (BrowserTool browser = browserService.createContext()) {
            binding.setVariable("browser", browser);
            
            if (args != null) {
                args.forEach(binding::setVariable);
            }

            // 3. Parse and Run Script
            GroovyShell shell = new GroovyShell(binding);
            Script script = shell.parse(code);
            
            // Note: If code defines a class and methodName is provided, 
            // the standard script.run() won't call that method.
            // For Phase 2, we primarily support script-style execution.
            
            return script.run();
        } catch (Exception e) {
            log.error("Failed to execute Groovy script", e);
            throw new RuntimeException("Failed to execute Groovy script", e);
        }
    }
}
