package util;

import service.FileBackedTaskManager;
import service.InMemoryHistoryManager;
import service.interfaces.HistoryManager;
import service.interfaces.TaskManager;

import java.nio.file.Path;

public final class Managers {


    private Managers() {
    }

    public static TaskManager getDefault(Path file) {
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
