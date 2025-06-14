package utill;

import org.junit.jupiter.api.Test;
import service.interfaces.HistoryManager;
import service.interfaces.TaskManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager aDefault = Managers.getDefault();
        assertNotNull(aDefault);
    }

    @Test
    void getDefaultHistory() {
        HistoryManager defaultHistory = Managers.getDefaultHistory();
        assertNotNull(defaultHistory);
    }
}