package com.tu.fitness_app.Model;

import com.google.gson.annotations.SerializedName;

public class Nutriments {
    @SerializedName("carbohydrates")
    private String carbohydrates;

    @SerializedName("proteins")
    private String proteins;

    @SerializedName("fat")
    private String fat;

    @SerializedName("energy_value")
    private String energy_value;

    public String getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(String carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public String getProteins() { return proteins; }

    public void setProteins(String proteins) {
        this.proteins = proteins;
    }

    public String getFat() { return fat; }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getCalories() { return energy_value; }

    public void setCalories(String energy_value) {
        this.energy_value = energy_value;
    }
}
