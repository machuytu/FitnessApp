package com.tu.fitness_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tu.fitness_app.R;
import com.tu.fitness_app.activities.Food_RecyclerFrag_Main;

import java.util.List;
import java.util.Map;

public class Food_MyRecycleAdapter extends RecyclerView.Adapter<Food_MyRecycleAdapter.ViewHolder> {
    public static float caloriesCount = 0f;
    public static float totalFat = 0f;
    public static float totalCarbs = 0f;
    public static float totalProtein = 0f;
    public TextView vTitle;
    public TextView vType;
    public TextView vCal;
    public Button vAdd;
    // Set Popup Window
    public RelativeLayout mRelativeLayout;
    public PopupWindow mPopupWindow;


    private List<Map<String,?>> mDataset;
    private Context mContext;

    // Constructor
    public Food_MyRecycleAdapter(Context myContext, List<Map<String, ?>> myDataset) {
        mContext = myContext;
        mDataset = myDataset;
    }

    // Using View Holder
    @NonNull
    @Override
    public Food_MyRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_food, parent, false);
//        ViewHolder viewHolder = new ViewHolder();
        return null;
    }

    // Filling Data into ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, ?> food = mDataset.get(position);
        holder.bindMovieData(food);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindMovieData(Map<String,?> fooditem) {
            vTitle.setText((String) fooditem.get("iName"));
            vType.setText((String) fooditem.get("bName"));
            vCal.setText((String)fooditem.get("iCal"));

            // Count item
//            caloriesCount = Food_RecyclerFrag_Main.c
        }
    }
}
