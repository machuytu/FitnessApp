package com.tu.fitness_app.Model;

public class StepCalculate {
    private int totalsteps;
    private double totaldistances;
    private double totalcalories;
    public static int mode;

    public StepCalculate () {
        totalsteps = 0;
        totaldistances = 0;
        totalcalories = 0;
    }

    public StepCalculate (int step) {
        setTotalsteps(step);
    }

    public void setTotalsteps(int step) {
        double a = 1;
        if (mode == 1) a = 1.5;
        totalsteps = step;
        totaldistances = Math.round(step * 0.7314 * a * 100.0) / 100.0;
        totalcalories = Math.round(totaldistances * 0.055 * 100.0) / 100.0;
    }

    public int getTotalsteps() {
        return totalsteps;
    }

    public double getTotaldistances() {
        return totaldistances;
    }

    public double getTotalcalories() {
        return totalcalories;
    }
}
