package com.tu.fitness_app.Data;

import android.content.Context;

import com.tu.fitness_app.api.FoodFactsServer;

public class AccessData {
    private FoodFactsServer server;

    private static AccessData instance;

    private  AccessData(Context context) {
        server = new FoodFactsServer();
    }

    public  static AccessData getInstance(Context context) {
        if (instance == null) {
            instance = new AccessData(context);
        }

        return instance;
    }


}
