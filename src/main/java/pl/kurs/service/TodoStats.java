package pl.kurs.service;

import java.util.List;

public class TodoStats {

    public double completionRatio(List<Boolean> completedFlags) {
        if (completedFlags == null || completedFlags.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int completed = 0;
        for (boolean flag : completedFlags) {
            if (flag) {
                completed++;
            }
        }
        return (double) completed / completedFlags.size();
    }
}
