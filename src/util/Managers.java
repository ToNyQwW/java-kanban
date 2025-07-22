package util;

import service.FileBackedTaskManager;
import service.InMemoryHistoryManager;
import service.interfaces.HistoryManager;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Managers {


    private Managers() {
    }

    public static TaskManager getDefault() {
        try {
            Path tempFile = Files.createTempFile("TaskManager", ".txt");
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
