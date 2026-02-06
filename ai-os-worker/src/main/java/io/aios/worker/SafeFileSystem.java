package io.aios.worker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class SafeFileSystem {

    private final Path rootPath;

    public SafeFileSystem(@Value("${ai.worker.workspace-path:./workspace}") String rootPathStr) {
        this.rootPath = Paths.get(rootPathStr).toAbsolutePath().normalize();
        File rootDir = rootPath.toFile();
        if (!rootDir.exists()) {
            if (rootDir.mkdirs()) {
                log.info("Created workspace directory: {}", rootPath);
            } else {
                log.error("Failed to create workspace directory: {}", rootPath);
            }
        }
    }

    private Path validatePath(String pathStr) {
        Path target = rootPath.resolve(pathStr).normalize();
        if (!target.startsWith(rootPath)) {
            throw new SecurityException("Access Denied: Path escapes sandbox: " + pathStr);
        }
        return target;
    }

    public void write(String path, String content) {
        Path target = validatePath(path);
        try {
            FileUtils.writeStringToFile(target.toFile(), content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file: " + path, e);
        }
    }

    public String read(String path) {
        Path target = validatePath(path);
        try {
            return FileUtils.readFileToString(target.toFile(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }

    public List<String> list(String path) {
        Path target = validatePath(path);
        String[] files = target.toFile().list();
        return files != null ? Arrays.asList(files) : Collections.emptyList();
    }

    public boolean exists(String path) {
        Path target = validatePath(path);
        return target.toFile().exists();
    }
}
