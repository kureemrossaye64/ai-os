package io.aios.worker;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroovyExecutionServiceTest {

    private final GroovyExecutionService executionService = new GroovyExecutionService();

    @Test
    public void testSimpleAddition() {
        String code = "class Calc { Object run() { return 1 + 1 } }";
        Object result = executionService.execute(code, "run", Collections.emptyMap());
        assertEquals(2, result);
    }

    @Test
    public void testMethodWithArgs() {
        String code = "class Greeter { String sayHello(Map args) { return 'Hello ' + args.name } }";
        Object result = executionService.execute(code, "sayHello", Collections.singletonMap("name", "World"));
        assertEquals("Hello World", result);
    }
}
