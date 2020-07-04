package com.tu.fitness_app.Model;

import androidx.room.PrimaryKey;

public class BasicObject {
    @PrimaryKey(autoGenerate = true)

    private long uid;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }
}
