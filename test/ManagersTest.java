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
}