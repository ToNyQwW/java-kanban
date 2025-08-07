package util;

import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

public class TaskTimeIntersectionChecker {

    private static final int INTERVAL = 15;
    private static final int INTERVALS_PER_DAY = 4 * 24;
    private static final int INTERVALS_PER_YEAR = 366 * INTERVALS_PER_DAY;

    private final boolean[] intervalArray = new boolean[INTERVALS_PER_YEAR];
    private final LocalDateTime baseTIme;

    // пускай расчет интервалов начинается за месяц до создания нового экземпляра класса
    public TaskTimeIntersectionChecker() {
        this.baseTIme = LocalDateTime.now().minusMonths(1);
    }

    public boolean addIntervalFromTask(Task task) {
        int startIndex = getIndex(task.getStartTime());
        int endIndex = getIndex(task.getEndTime());
        if (startIndex < 0 || endIndex > intervalArray.length) {
            return false;
        }
        if (IntStream.range(startIndex, endIndex).anyMatch(i -> intervalArray[i])) {
            return false;
        }
        IntStream.range(startIndex, endIndex).forEach(i -> intervalArray[i] = true);
        return true;
    }

    public boolean removeIntervalFromTask(Task task) {
        int startIndex = getIndex(task.getStartTime());
        int endIndex = getIndex(task.getEndTime());
        if (startIndex < 0 || endIndex > intervalArray.length) {
            return false;
        }
        IntStream.range(startIndex, endIndex).forEach(i -> intervalArray[i] = false);
        return true;
    }

    private int getIndex(LocalDateTime time) {
        long minutes = Duration.between(baseTIme, time).toMinutes();
        return (int) (minutes) / INTERVAL;
    }

    public boolean[] getIntervalArray() {
        return intervalArray;
    }

    public LocalDateTime getBaseTIme() {
        return baseTIme;
    }
}
