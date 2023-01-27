package tests;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
/**
 * Класс описывающий реализацию ТЕСТОВ менеджера InMemoryTasksManager.
 */
public class InMemoryTasksManagerTest extends TaskManagerTest<InMemoryTaskManager>{
    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

}

