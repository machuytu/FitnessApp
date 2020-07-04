package com.tu.fitness_app.JSON;

import android.util.Log;
import android.widget.TextView;

import com.tu.fitness_app.Custom.MyUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodFactFoodJson {
    public List<Map<String,?>> foodList;
    public TextView t;

    public List<Map<String, ?>> getMoviesList() {
        return foodList;
    }

    public int getSize() {
        return foodList.size();
    }

    public HashMap getItem(int i) {
        if (i >= 0 && i < foodList.size()) {
            return (HashMap) foodList.get(i);
        } else
            return null;
    }

    public FoodFactFoodJson() {
        foodList = new ArrayList<Map<String, ?>>();
    }

    public void removeItem(int i) {
        foodList.remove(i);
    }

    public void addItem(int position, HashMap clone) {
        foodList.add(position, clone);
    }

    public void downloadFoodDataJson(String json_url) throws JSONException {
        foodList.clear(); // clear the list

        String foodArray = MyUtility.downloadJSONusingHTTPGetRequest(json_url);
        foodArray = foodArray.toString();
        longInfo(foodArray);
        Log.d("FoodArray", foodArray);

        if (foodArray == null) {
            Log.d("MyDebugMsg", "Having trouble loading URL: " + json_url);
            return;
        }

        String json = "Assuming that here is your JSON response";
        try {
            JSONObject parentObject = new JSONObject(foodArray);
            JSONArray hitsJsonArray = parentObject.getJSONArray("product");
            Log.d("product", hitsJsonArray.toString());
            Log.d("product length", String.valueOf(hitsJsonArray.length()));
            for (int i = 0; i < hitsJsonArray.length(); ++i) {
                JSONObject f = hitsJsonArray.getJSONObject(i);
//                JSONObject fi = f.getJSONObject("fields");
//                Log.d("Hits array item:", fi.toString());
                {
                    String name = f.getString("product_name_en");
                    foodList.add(createFood_brief(name));
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d("MyDebugMsg", "JSONException in downloadFoodDataJson");
            e.printStackTrace();
        }

    }

    private static HashMap createFood_brief(String name) {
        HashMap fd = new HashMap();
        fd.put("product_name_en", name);
        return fd;
    }

    public static void longInfo(String str) {
        if (str.length() > 4000) {
            Log.i("FoodArray1:", str.substring(0, 4000));
            longInfo(str.substring(4000));
        } else
            Log.i("FoodArray2", str);
    }
}
