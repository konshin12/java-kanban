package test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import task.*;
import manager.*;

public class ManagersTest {

    @Test
    void managersShouldNotReturnNull() {
        assertNotNull(Managers.getDefault(), "TaskManager не инициализирован");
        assertNotNull(Managers.getDefaultHistory(), "HistoryManager не инициализирован");
    }

    @Test
    void getDefaultWithFileShouldReturnFileBackedTaskManager() {
        java.io.File tempFile = new java.io.File("test.csv");
        TaskManager manager = Managers.getDefault(tempFile);
        assertTrue(manager instanceof FileBackedTaskManager,
                "Должен возвращаться FileBackedTaskManager при передаче файла");
        tempFile.delete();
    }
}