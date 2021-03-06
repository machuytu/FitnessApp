package com.tu.fitness_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tu.fitness_app.Interface.ItemClickListener;
import com.tu.fitness_app.R;
import com.tu.fitness_app.activities.ViewEx;
import com.tu.fitness_app.Model.Exercise;

import java.util.List;

class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView image;
    TextView text;

    private ItemClickListener itemClickListener;

    RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.ex_img);
        text = itemView.findViewById(R.id.ex_name);

        itemView.setOnClickListener(this);
    }

    void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition());
    }
}
public class RecycleViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private List<com.tu.fitness_app.Model.Exercise> excerciseList;
    private Context context;

    public RecycleViewAdapter(List<Exercise> excerciseList, Context context) {
        this.excerciseList = excerciseList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_exercise, parent, false);
        return new  RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.image.setImageResource(excerciseList.get(position).getImage_id());
        holder.text.setText(excerciseList.get(position).getName());


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {

                //get activity
                Intent intent = new Intent(context, ViewEx.class);
                intent.putExtra("image_id", excerciseList.get(position).getImage_id());
                intent.putExtra("name", excerciseList.get(position).getName());
                intent.putExtra("intro", excerciseList.get(position).getName1());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return excerciseList.size();
    }
}
