package com.tu.fitness_app.Model;

import android.content.Context;

public class Setting {
    public int mode;

    public Setting() {}

    public Setting(int mode) {
        this.mode = mode;
    }

    public int GetSetting() {
        return mode;
    }

    public void SetSetting(int mode) {
        this.mode = mode;
    }
}
