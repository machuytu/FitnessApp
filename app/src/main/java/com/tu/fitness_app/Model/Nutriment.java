package com.tu.fitness_app.Model;

import com.google.gson.annotations.SerializedName;

public class Nutriment {
    @SerializedName("energy_unit")
    private String energyUnit;

    @SerializedName("energy_value")
    private String energyValue;

    public String getEnergyUnit() {
        return energyUnit;
    }

    public void setEnergyUnit(String energyUnit) {
        this.energyUnit = energyUnit;
    }

    public String getEnergyValue() {
        return energyValue;
    }

    public void setEnergyValue(String energyValue) {
        this.energyValue = energyValue;
    }
}
