package com.tu.fitness_app.Adapter;

import android.content.Context;
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

import org.json.JSONArray;

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
    private DatabaseReference ref_calories;
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
            vTitle = itemView.findViewById(R.id.title);
            vType = itemView.findViewById(R.id.type);
            vCal = itemView.findViewById(R.id.calories);
            vAdd = itemView.findViewById(R.id.addFood);
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }

        private DatabaseReference getCaloriesRef(String ref) {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();
            return mDatabase.child("Calories").child(userId).child(date).child(ref);
        }


        private void bindMovieData(final Map<String,?> fooditem) {
            vTitle.setText((String) fooditem.get("iname"));
            vType.setText((String) fooditem.get("bname"));
            vCal.setText((String)fooditem.get("ical"));

            // Count item
            caloriesCount = Food_RecyclerFrag_Main.calRef1;
            totalCarbs = Food_RecyclerFrag_Main.user_carbs1;
            totalProtein = Food_RecyclerFrag_Main.user_protein1;
            totalFat = Food_RecyclerFrag_Main.user_fat1;

            //DB
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            ref_history = database.getReference("history");
            ref_calories = database.getReference("Calories");

            //VIEW
            final String date = today.getYear()+1900 + "-" + (1+today.getMonth()) + "-" + today.getDate();

            vAdd.setOnClickListener(v -> {
                count++;
                caloriesCount = caloriesCount + (Float.parseFloat(String.valueOf(fooditem.get("ical"))));
                totalCarbs = totalCarbs + (Float.parseFloat(String.valueOf(fooditem.get("icarbs"))));
                totalFat = totalFat + (Float.parseFloat(String.valueOf(fooditem.get("ifat"))));
                totalProtein = totalProtein + (Float.parseFloat((String.valueOf(fooditem.get("iprotein")))));
                Log.d("After Adding", String.valueOf(caloriesCount) + String.valueOf(caloriesCount) + String.valueOf(caloriesCount) + String.valueOf(caloriesCount));


                String eat = String.valueOf(fooditem.get("iname"));
                String cal = String.valueOf(fooditem.get("ical"));
                float carbs = (Float.parseFloat(String.valueOf(fooditem.get("icarbs"))));
                float fat = (Float.parseFloat(String.valueOf(fooditem.get("ifat"))));
                float protein = (Float.parseFloat((String.valueOf(fooditem.get("iprotein")))));

                getCaloriesRef("totalcalories").setValue(caloriesCount);
                getCaloriesRef("totalfat").setValue(totalFat);
                getCaloriesRef("totalcarbs").setValue(totalCarbs);
                getCaloriesRef("totalprotein").setValue(totalProtein);

                if(count == 1) {
                    String toast1 = count + "item added";
                    Toast.makeText(mContext, toast1, Toast.LENGTH_SHORT).show();
                } else if (count > 1) {
                    String toast2 = count + "items added";
                    Toast.makeText(mContext, toast2, Toast.LENGTH_SHORT).show();
                }

                //set data
                String id = ref_history.push().getKey();
                History history = new History(id, date, "EAT : " + eat, cal, fat, carbs, protein);
                ref_history.child(UserId).child(date).child(id).setValue(history);
            });
            JSONArray j = null;
        }
    }
}
