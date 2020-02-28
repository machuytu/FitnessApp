package com.tu.fitness_app.Model;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDays {
    public List<String> day;

    public WorkoutDays() {}

    public WorkoutDays(List<String> Day) {
        this.day = Day;
    }

    public List<String> GetWorkoutDays() {
        List<String> result = new ArrayList<>();
        return result;
    }

    public void SetWorkoutDays(List<String> Day) {
        this.day = Day;
    }
}
