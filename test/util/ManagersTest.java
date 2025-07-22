package util;

import org.junit.jupiter.api.Test;
import service.interfaces.HistoryManager;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void getDefault() throws IOException {
        Path tempFile = Files.createTempFile("test", ".txt");
        TaskManager aDefault = Managers.getDefault(tempFile);
        assertNotNull(aDefault);
    }

    @Test
    void getDefaultHistory() {
        HistoryManager defaultHistory = Managers.getDefaultHistory();
        assertNotNull(defaultHistory);
    }
}