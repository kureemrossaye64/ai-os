package io.aios.worker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.mockito.Mockito.*;

public class GroovyExecutionServiceTest {

    @TempDir
    Path tempDir;

    private GroovyExecutionService executionService;
    private SafeFileSystem fs;
    private BrowserService browserService;

    @BeforeEach
    public void setup() {
        fs = new SafeFileSystem(tempDir.toString());
        browserService = mock(BrowserService.class);
        executionService = new GroovyExecutionService(fs, browserService);
    }

    @Test
    public void testSimpleAddition() {
        String code = "return 1 + 1";
        Object result = executionService.execute(code, "run", Collections.emptyMap());
        assertEquals(2, result);
    }

    @Test
    public void testMethodWithArgs() {
        String code = "return 'Hello ' + name";
        Object result = executionService.execute(code, "run", Collections.singletonMap("name", "World"));
        assertEquals("Hello World", result);
    }
}
