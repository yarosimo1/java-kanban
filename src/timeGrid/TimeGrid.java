package timeGrid;

import task.Task;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TimeGrid {
    private final Map<LocalDateTime, Boolean> slots = new HashMap<>();
    private final LocalDateTime startOfYear;

    public TimeGrid(LocalDateTime startOfYear) {
        this.startOfYear = startOfYear;
    }

    public boolean canSchedule(Task task) {
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();

        if (start == null || end == null) return true;

        LocalDateTime cursor = roundTo15(start);

        while (cursor.isBefore(end)) {
            if (slots.getOrDefault(cursor, false)) {
                return false;
            }
            cursor = cursor.plusMinutes(15);
        }
        return true;
    }

    public void schedule(Task task) {
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();

        if (start == null || end == null) return;

        LocalDateTime cursor = roundTo15(start);

        while (cursor.isBefore(end)) {
            slots.put(cursor, true);
            cursor = cursor.plusMinutes(15);
        }
    }

    public void unschedule(Task task) {
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();

        if (start == null || end == null) return;

        LocalDateTime cursor = roundTo15(start);

        while (cursor.isBefore(end)) {
            slots.put(cursor, false);
            cursor = cursor.plusMinutes(15);
        }
    }

    private LocalDateTime roundTo15(LocalDateTime dt) {
        int minute = (dt.getMinute() / 15) * 15;
        return dt.withMinute(minute).withSecond(0).withNano(0);
    }
}