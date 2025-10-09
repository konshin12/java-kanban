package test;

import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManagersTest {

    @Test
    public void managersShouldNotReturnNull() {
        assertNotNull(Managers.getDefault(), "TaskManager не инициализирован");
        assertNotNull(Managers.getDefaultHistory(), "HistoryManager не инициализирован");
    }

    @Test
    public void getDefaultWithFileShouldReturnFileBackedTaskManager() {
        java.io.File tempFile = new java.io.File("test.csv");
        TaskManager manager = Managers.getDefault(tempFile);
        assertTrue(manager instanceof FileBackedTaskManager,
                "Должен возвращаться FileBackedTaskManager при передаче файла");
        tempFile.delete();
    }
}