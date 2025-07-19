package service;

import model.*;
import service.exceptions.ManagerLoadException;
import service.exceptions.ManagerSaveException;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path saveFile;

    public FileBackedTaskManager(Path saveFile) {
        super();
        this.saveFile = saveFile;
    }

    public static FileBackedTaskManager loadFromFile(Path file) throws ManagerLoadException {
        FileBackedTaskManager result = new FileBackedTaskManager(file);
        try {
            List<String> taskManagerFile = Files.readAllLines(file);
            if (taskManagerFile.isEmpty() || taskManagerFile.size() == 1) {
                return result;
            }
            for (int i = 1; i < taskManagerFile.size(); i++) {
                Task task = result.fromString(taskManagerFile.get(i));
                if (task instanceof EpicTask epic) {
                    result.addEpicTask(epic);
                } else if (task instanceof SubTask sub) {
                    result.addSubTask(sub);
                } else {
                    result.addTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("ManagerLoadException", e);
        }
        return result;
    }

    public Task fromString(String value) {
        String[] taskString = value.split(",");
        Task result = null;
        switch (TaskType.valueOf(taskString[1])) {
            case TASK -> result = new Task(Integer.parseInt(taskString[0]), taskString[2], taskString[3],
                    TaskStatus.valueOf(taskString[4]));

            case SUB_TASK -> result = new SubTask(Integer.parseInt(taskString[0]), taskString[2], taskString[3],
                    TaskStatus.valueOf(taskString[4]), Integer.parseInt(taskString[5]));

            case EPIC_TASK -> result = new EpicTask(Integer.parseInt(taskString[0]), taskString[2], taskString[3]);
        }
        return result;
    }

    public String toString(Task task) {
        String result;
        if (task instanceof SubTask sub) {
            result = String.format("%d,%s,%s,%s,%s,%d",
                    sub.getId(),
                    TaskType.SUB_TASK,
                    sub.getName(),
                    sub.getDescription(),
                    sub.getStatus(),
                    sub.getEpicId());
        } else if (task instanceof EpicTask epic) {
            result = String.format("%d,%s,%s,%s, ,",
                    epic.getId(),
                    TaskType.EPIC_TASK,
                    epic.getName(),
                    epic.getDescription());
        } else {
            result = String.format("%d,%s,%s,%s,%s,",
                    task.getId(),
                    TaskType.TASK,
                    task.getName(),
                    task.getDescription(),
                    task.getStatus());
        }
        return result;
    }

    private void save() throws ManagerSaveException {
        try {
            Files.writeString(saveFile, "id,type,name,description,status,epic\n", UTF_8, CREATE, TRUNCATE_EXISTING);
            for (Task task : getTasksList()) {
                Files.writeString(saveFile, this.toString(task) + "\n", UTF_8, APPEND);
            }
            for (EpicTask epicTask : getEpicTasksList()) {
                Files.writeString(saveFile, this.toString(epicTask) + "\n", UTF_8, APPEND);
            }
            for (SubTask subTask : getSubTasksList()) {
                Files.writeString(saveFile, this.toString(subTask) + "\n", UTF_8, APPEND);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("ManagerSaveException", e);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        super.addEpicTask(epicTask);
        save();
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public boolean updateSubTask(SubTask subTasks) {
        boolean result = super.updateSubTask(subTasks);
        save();
        return result;
    }

    @Override
    public boolean updateEpicTask(EpicTask epicTask) {
        boolean result = super.updateEpicTask(epicTask);
        save();
        return result;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeEpicTask(int id) {
        super.removeEpicTask(id);
        save();
    }

    @Override
    public void clearTasksMap() {
        super.clearTasksMap();
        save();
    }

    @Override
    public void clearSubtasksMap() {
        super.clearSubtasksMap();
        save();
    }

    @Override
    public void clearEpicTasksMap() {
        super.clearEpicTasksMap();
        save();
    }

    //Дополнительное задание. Реализуем пользовательский сценарий
    public static void main(String[] args) throws IOException {

        //1.Заведите несколько разных задач, эпиков и подзадач.
        Path tempFile = Files.createTempFile("test", ".txt");
        TaskManager taskManager = new FileBackedTaskManager(tempFile);

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
