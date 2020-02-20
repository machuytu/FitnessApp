package com.tu.fitness_app.Model;

public class History {
    private String id;
    private String date;
    private String item;
    private String totalCalories;
    private float totalfat;
    private float totalcarbs;
    private float totalprotein;

    @Override
    public String toString() {
        return item + " : " + totalCalories + " calories";
    }

    public History() {
    }

    public History(String id, String date, String item, String totalCalories, float totalfat, float totalcarbs, float totalprotein) {
        this.id = id;
        this.date = date;
        this.item = item;
        this.totalCalories = totalCalories;
        this.totalfat = totalfat;
        this.totalcarbs = totalcarbs;
        this.totalprotein = totalprotein;
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
        return totalfat;
    }

    public float getTotalCarbs() {
        return totalcarbs;
    }

    public float getTotalProtein() {
        return totalprotein;
    }
}
