package com.tu.fitness_app.Model;

public class History {
    private String id;
    private String date;
    private String item;
    private String totalCalories;
    private float totalFat;
    private float totalCarbs;
    private float totalProtein;

    @Override
    public String toString() {
        return item + " : " + totalCalories + " calories, "
                + totalFat + " fat, " + totalCarbs
                + " carbs, " + totalProtein + " ,protein";
    }

    public History() {
    }

    public History(String id, String date, String item, String totalCalories, float totalFat, float totalCarbs, float totalProtein) {
        this.id = id;
        this.date = date;
        this.item = item;
        this.totalCalories = totalCalories;
        this.totalFat = totalFat;
        this.totalCarbs = totalCarbs;
        this.totalProtein = totalProtein;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(String totalCalories) {
        this.totalCalories = totalCalories;
    }

    public float getTotalFat() {
        return totalFat;
    }

    public float getTotalCarbs() {
        return totalCarbs;
    }

    public float getTotalProtein() {
        return totalProtein;
    }
}
