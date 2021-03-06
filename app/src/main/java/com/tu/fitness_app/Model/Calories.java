package com.tu.fitness_app.Model;

public class Calories {
    private float totalcalories;
    private float totalfat;
    private float totalcarbs;
    private float totalprotein;
    private Calories() {
        // Default constructor required for calls to DataSnapshot.getValue(Calories.class)
    }

    public Calories(float totalcalories, float totalfat, float totalcarbs, float totalprotein) {
        this.totalcalories = totalcalories;
        this.totalfat = totalfat;
        this.totalcarbs = totalcarbs;
        this.totalprotein = totalprotein;
    }

    public float getTotalcalories() {
        return totalcalories;
    }

    public float getTotalfat() {
        return totalfat;
    }

    public float getTotalcarbs() {
        return totalcarbs;
    }

    public float getTotalprotein() {
        return totalprotein;
    }

    public void setTotalcalories(float totalcalories) {
        this.totalcalories = totalprotein;
    }

    public void setTotalfat(float totalfat) {
        this.totalfat = totalfat;
    }

    public void setTotalcarbs(float totalcarbs) {
        this.totalcarbs = totalcarbs;
    }

    public void setTotalprotein(float totalprotein) {
        this.totalprotein = totalprotein;
    }
}