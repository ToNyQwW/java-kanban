import model.EpicTask;
import model.SubTask;
import model.Task;
import service.FileBackedTaskManager;
import service.interfaces.TaskManager;
import util.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    //Дополнительное задание. Реализуем пользовательский сценарий
    public static void main(String[] args) throws IOException {

        //1.Заведите несколько разных задач, эпиков и подзадач.
        Path tempFile = Files.createTempFile("ManagerTask", ".txt");
        TaskManager taskManager = Managers.getDefault(tempFile);

        Task task1 = new Task("Task1", "");
        Task task2 = new Task("Task2", "");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        EpicTask epicTask1 = new EpicTask("EpicTask1", "");
        taskManager.addEpicTask(epicTask1);
        SubTask subTask1 = new SubTask("subTask1", "from epicTask1", epicTask1.getId());
        SubTask subTask2 = new SubTask("subTask2", "from epicTask1", epicTask1.getId());
        SubTask subTask3 = new SubTask("subTask3", "from epicTask1", epicTask1.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        EpicTask epicTask2 = new EpicTask("EpicTask2", "");
        taskManager.addEpicTask(epicTask2);

        //2.Создайте новый FileBackedTaskManager-менеджер из этого же файла.
        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        //3.Проверьте, что все задачи, эпики, подзадачи, которые были в старом менеджере, есть в новом.
        System.out.println("Вывод taskList из первого менеджера: ");
        System.out.println(taskManager.getTasksList());
        System.out.println("Вывод taskList из второго менеджера: ");
        System.out.println(loadedTaskManager.getTasksList());
        System.out.println();
        System.out.println("Вывод subList из первого менеджера: ");
        System.out.println(taskManager.getSubTasksList());
        System.out.println("Вывод subList из второго менеджера: ");
        System.out.println(loadedTaskManager.getSubTasksList());
        System.out.println();
        System.out.println("Вывод epicList из первого менеджера: ");
        System.out.println(taskManager.getEpicTasksList());
        System.out.println("Вывод epicList из второго менеджера: ");
        System.out.println(loadedTaskManager.getEpicTasksList());
    }
}
