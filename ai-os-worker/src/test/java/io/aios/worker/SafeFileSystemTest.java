package io.aios.worker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SafeFileSystemTest {

    @TempDir
    Path tempDir;

    private SafeFileSystem fs;

    @BeforeEach
    public void setup() {
        fs = new SafeFileSystem(tempDir.toString());
    }

    @Test
    public void testWriteAndRead() {
        fs.write("test.txt", "Hello World");
        assertTrue(fs.exists("test.txt"));
        assertEquals("Hello World", fs.read("test.txt"));
    }

    @Test
    public void testSandboxEscapeFails() {
        assertThrows(SecurityException.class, () -> {
            fs.read("../outside.txt");
        });

        assertThrows(SecurityException.class, () -> {
            fs.write("/etc/passwd", "evil");
        });
    }

    @Test
    public void testSubdirectoryAccess() {
        fs.write("subdir/test.txt", "Nested");
        assertTrue(fs.exists("subdir/test.txt"));
        assertEquals("Nested", fs.read("subdir/test.txt"));
        assertTrue(fs.list("").contains("subdir"));
    }
}
