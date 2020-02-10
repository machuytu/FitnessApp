package com.tu.fitness_app.Model;

public class Steps {
    public int totalsteps;

    public Steps() {
        // Default constructor required for calls to DataSnapshot.getValue(Steps.class)
    }

    public Steps(int totalsteps) {
        this.totalsteps = totalsteps;
    }
}
