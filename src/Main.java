import model.*;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        // проверка taskManager ( по условиям из практикума)

        Task task1 = new Task("Task1", " ", TaskStatus.IN_PROGRESS);
        Task task2 = new Task("Task2", " ");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        EpicTask epicTask1 = new EpicTask("epicTask1", " ");
        taskManager.addEpicTask(epicTask1);
        SubTask subTask1 = new SubTask("subTask1", " ", TaskStatus.IN_PROGRESS, epicTask1.getId());
        SubTask subTask2 = new SubTask("subTask2", " ", TaskStatus.DONE, epicTask1.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        EpicTask epicTask2 = new EpicTask("epicTask2", " ");
        taskManager.addEpicTask(epicTask2);
        SubTask subTask3 = new SubTask("subTask3", " ", epicTask2.getId());
        taskManager.addSubTask(subTask3);

        System.out.println(taskManager.getEpicTasksList());
        System.out.println(taskManager.getSubTasksList());
        System.out.println(taskManager.getTasksList());

        taskManager.updateTask(new Task(task2.getId(), task2.getName(), task2.getDescription(), TaskStatus.DONE));

        taskManager.updateSubTask(new SubTask(subTask1.getId(), subTask1.getName(),
                subTask1.getDescription(), TaskStatus.DONE, epicTask1.getId()));

        taskManager.updateSubTask(new SubTask(subTask3.getId(), subTask3.getName(),
                subTask3.getDescription(), TaskStatus.IN_PROGRESS, epicTask2.getId()));

        System.out.println();
        System.out.println(taskManager.getEpicTasksList());
        System.out.println(taskManager.getSubTasksList());
        System.out.println(taskManager.getTasksList());

        taskManager.removeEpicTasksMap(epicTask1.getId());
        taskManager.removeTask(task1.getId());
        taskManager.removeSubTask(subTask3.getId());

        System.out.println();
        System.out.println(taskManager.getEpicTasksList());
        System.out.println(taskManager.getSubTasksList());
        System.out.println(taskManager.getTasksList());
    }
}
