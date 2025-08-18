package service.handler;

import com.google.gson.reflect.TypeToken;
import model.SubTask;
import model.Task;

import java.util.List;

public class TaskListTypeToken extends TypeToken<List<Task>> {
}

class SubTaskListTypeToken extends TypeToken<List<SubTask>> {
}

