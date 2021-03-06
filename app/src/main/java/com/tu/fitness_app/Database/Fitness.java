package com.tu.fitness_app.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.tu.fitness_app.Model.Setting;

import java.util.ArrayList;
import java.util.List;

public class Fitness extends SQLiteAssetHelper {


    private static final String WORKOUT_DAYS_TABLE_NAME = "WorkoutDays";

    private static final String DAY_COLUMN_NAME = "Day";


    private static final String DB_NAME = "Fitness.db";
    private static final int DB_VER = 1;

    public Fitness(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public int getSettingMode() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect =  {"Mode"};
        String sqlTable = "Setting";

        qb.setTables(sqlTable);
        Cursor cursor = qb.query(db, sqlSelect,null, null, null, null, null);
        cursor.moveToFirst();
        return Setting.GetSetting();


    }

    public void saveSettingMode(int value) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "UPDATE Setting SET Mode = " + value;
        db.execSQL(query);
    }

    public List<String> getWorkoutDays() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect =  {"Day"};
        String sqlTable = "WorkoutDays";

        qb.setTables(sqlTable);
        Cursor cursor = qb.query(db, sqlSelect,null, null, null, null, null);

        List<String> result = new ArrayList<>();
        if(cursor.moveToNext()) {
            do {
                result.add(cursor.getString(cursor.getColumnIndex("Day")));
            } while (cursor.moveToNext());
        }

        return result;
    }

    public void saveDay(String value) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO WorkoutDays (Day) VALUES(%s)", value);
        db.execSQL(query);
    }

}
