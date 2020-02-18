package com.tu.fitness_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tu.fitness_app.Model.History;
import com.tu.fitness_app.R;
import com.tu.fitness_app.activities.Food_RecyclerFrag_Main;
import com.tu.fitness_app.activities.HistoryActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Food_MyRecycleAdapter extends RecyclerView.Adapter<Food_MyRecycleAdapter.ViewHolder> {
    public static float caloriesCount = 0f;
    public static float totalFat = 0f;
    public static float totalCarbs = 0f;
    public static float totalProtein = 0f;
    // Set Popup Window
    public RelativeLayout mRelativeLayout;
    public PopupWindow mPopupWindow;

    private DatabaseReference ref_history;
    private List<Map<String,?>> mDataset;
    private Context mContext;
    private int count;
    String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


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
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Filling Data into ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, ?> food = mDataset.get(position);
        holder.bindMovieData(food);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView vTitle;
        public TextView vType;
        public TextView vCal;
        public Button vAdd;

        private FirebaseAuth mAuth;
        private DatabaseReference mDatabase;
        private List<History> historyArrayList;
        private View historyListView;
        private Date today = new Date();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vTitle = (TextView) itemView.findViewById(R.id.title);
            vType = (TextView) itemView.findViewById(R.id.type);
            vCal = (TextView) itemView.findViewById(R.id.calories);
            vAdd = (Button) itemView.findViewById(R.id.addFood);
            //mRelativeLayout = (RelativeLayout) v.findViewById(R.id.recyclr_frag_pop);
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }

        private DatabaseReference getCaloriesRef(String ref) {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            return mDatabase.child("Calories").child(userId).child(ref);
        }


        public void bindMovieData(final Map<String,?> fooditem) {
            vTitle.setText((String) fooditem.get("iname"));
            vType.setText((String) fooditem.get("bname"));
            vCal.setText((String)fooditem.get("ical"));

            // Count item
            caloriesCount = Food_RecyclerFrag_Main.calRef1;
            totalCarbs = Food_RecyclerFrag_Main.user_carbs1;
            totalProtein = Food_RecyclerFrag_Main.user_protein1;
            totalFat = Food_RecyclerFrag_Main.user_fat1;

            // History
            historyArrayList = new ArrayList<>();
            //DB
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            ref_history = database.getReference("history");

            //VIEW
//            historyListView = historyListView.findViewById(R.id.historyListView);
            final String date = today.getYear()+1900 + "-" + (1+today.getMonth()) + "-" + today.getDate();

            vAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count++;
                    Log.d("Before adding", String.valueOf(caloriesCount) +
                            String.valueOf(totalCarbs) + String.valueOf(totalProtein) +
                            String.valueOf(totalFat));
                    caloriesCount = caloriesCount + (Float.parseFloat(String.valueOf(fooditem.get("ical"))));
                    totalCarbs = totalCarbs + (Float.parseFloat(String.valueOf(fooditem.get("icarbs"))));
                    totalFat = totalFat + (Float.parseFloat(String.valueOf(fooditem.get("ifat"))));
                    totalProtein = totalProtein + (Float.parseFloat((String.valueOf(fooditem.get("iprotein")))));
                    String eat = String.valueOf(fooditem.get("iname"));
                    String cal = String.valueOf(fooditem.get("ical"));

                    Log.d("After Adding", "fat" + String.valueOf(caloriesCount) +
                            String.valueOf(totalCarbs) + String.valueOf(totalFat) +
                            String.valueOf(totalProtein));
                    Log.d("Adapter", (String.valueOf(Food_RecyclerFrag_Main.user_fat1)) +
                            (String.valueOf(Food_RecyclerFrag_Main.user_carbs1)) +
                            (String.valueOf(Food_RecyclerFrag_Main.user_protein1)) +
                            (String.valueOf(Food_RecyclerFrag_Main.calRef1)));

                    getCaloriesRef("totalcalories").setValue(caloriesCount);
                    getCaloriesRef("totalfat").setValue(totalFat);
                    getCaloriesRef("totalcarbs").setValue(totalCarbs);
                    getCaloriesRef("totalprotein").setValue(totalProtein);

                    if(count == 1) {
                        String toast1 = String.valueOf(count) + "item added";
                        Toast.makeText(mContext, toast1, Toast.LENGTH_SHORT).show();
                    } else if (count > 1) {
                        String toast2 = String.valueOf(count) + "items added";
                        Toast.makeText(mContext, toast2, Toast.LENGTH_SHORT).show();
                    }

                    //set data
                    String id = ref_history.push().getKey();
                    History history = new History(id, date, "EAT : " + eat, cal);
                    ref_history.child(UserId).child(date).child(id).setValue(history);
                }
            });
            JSONArray j = null;
        }
    }
}
