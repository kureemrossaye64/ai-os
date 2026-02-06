package io.aios.worker;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GroovyExecutionService {

    private final GroovyClassLoader classLoader = new GroovyClassLoader();

    public Object execute(String code, String methodName, Map<String, String> args) {
        try {
            Class<?> groovyClass = classLoader.parseClass(code);
            GroovyObject instance = (GroovyObject) groovyClass.getDeclaredConstructor().newInstance();

            if (methodName == null || methodName.isEmpty()) {
                methodName = "run";
            }

            // For now, we just pass the arguments map as a single argument if it's not empty,
            // or no arguments if it is. This is a simple implementation for Phase 1.
            if (args == null || args.isEmpty()) {
                return instance.invokeMethod(methodName, null);
            } else {
                return instance.invokeMethod(methodName, new Object[]{args});
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Groovy script", e);
        }
    }
}
