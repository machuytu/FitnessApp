package com.tu.fitness_app.Model;

public class Eat {
    public String food;
    public float eatcalories;
    public float eatfat;
    public float eatcarbs;
    public float eatprotein;

    public Eat() {
    }

    public Eat(String food, float eatcalories, float eatfat, float eatcarbs, float eatprotein) {
        this.food = food;
        this.eatcalories = eatcalories;
        this.eatfat = eatfat;
        this.eatcarbs = eatcarbs;
        this.eatprotein = eatprotein;
    };

    @Override
    public String toString() {
        return food + ","+ eatcalories + "," + eatfat + "," + eatcarbs + "," + eatprotein;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public float getCalories() {
        return eatcalories;
    }

    public void setCalories(float eatcalories) {
        this.eatcalories = eatcalories;
    }

    public float getFat() {
        return eatfat;
    }

    public void setFat(float eatfat) {
        this.eatfat = eatfat;
    }

    public float getCarbs() {
        return eatcarbs;
    }

    public void setCarbs(float eatcarbs) {
        this.eatcarbs = eatcarbs;
    }

    public float getProtein() {
        return eatprotein;
    }

    public void setProtein(float eatprotein) {
        this.eatprotein = eatprotein;
    }
}
