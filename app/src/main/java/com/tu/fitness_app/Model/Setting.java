package com.tu.fitness_app.Model;

public class Setting {
    public static int mode;

    public Setting() {}

    public Setting(int mode) {
        Setting.mode = mode;
    }

    public static int GetSetting() {
        return mode;
    }

    public static int SetSetting(int mode) {
        Setting.mode = mode;
        return mode;
    }
}
