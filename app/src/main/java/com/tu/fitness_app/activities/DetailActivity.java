package com.tu.fitness_app.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tu.fitness_app.Model.History;
import com.tu.fitness_app.Model.Nutriments;
import com.tu.fitness_app.Model.Product;
import com.tu.fitness_app.R;
import com.tu.fitness_app.api.FoodFactsApi;
import com.tu.fitness_app.api.FoodFactsApiResponse;
import com.tu.fitness_app.api.FoodFactsServer;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity  {
    public static Product product;
    String barcode = BarcodeScanner.myResult;
    String name, url, engry;
    private ImageView picture;
    private TextView productName, energyValue, fat, proteins, carbohydrates;
    private Button button;
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private ListView ingredientsList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference ref_history;
    private DatabaseReference ref_calories;
    public static float calRef1 = 0f;
    public static float user_fat1 = 0f;
    public static float user_carbs1 = 0f;
    public static float user_protein1 = 0f;
    public static String username = "";

    Date today = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        picture = findViewById(R.id.picture);
        productName = findViewById(R.id.product_name);
        energyValue = findViewById(R.id.energy_value);
        fat = findViewById(R.id.fat);
        proteins = findViewById(R.id.proteins);
        carbohydrates = findViewById(R.id.carbohydrates);
        ingredientsList = findViewById(R.id.ingredients_list);
        button = findViewById(R.id.button);

        // Toolbar
        mToolbar = findViewById(R.id.recycle_toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // get data
        GetDataFirebase();

        // get product with barcode
        GetProduct();
//
        //  add product to database
        button.setOnClickListener(v -> {
            calRef1 = calRef1 + Float.parseFloat(product.nutriments.getCalories());
            user_carbs1 = user_carbs1 + Float.parseFloat(product.nutriments.getCarbohydrates());
            user_fat1 = user_fat1 + Float.parseFloat(product.nutriments.getFat());
            user_protein1 = user_protein1 + Float.parseFloat(product.nutriments.getProteins());
            username = product.getProductName();

            getCaloriesRef("totalcalories").setValue(calRef1);
            getCaloriesRef("totalfat").setValue(user_carbs1);
            getCaloriesRef("totalcarbs").setValue(user_fat1);
            getCaloriesRef("totalprotein").setValue(user_protein1);

            //DB
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            ref_history = database.getReference("history");
            //VIEW
            final String date = today.getYear()+1900 + "-" + (1+today.getMonth()) + "-" + today.getDate();

            //set data
            String id = ref_history.push().getKey();
            String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            History history = new History(id, date, "EAT : " + username, String.valueOf(calRef1), user_fat1, user_fat1, user_protein1);
            ref_history.child(UserId).child(date).child(id).setValue(history);

            Intent intent = new Intent(DetailActivity.this, OverviewActivity.class);
            startActivity(intent);
        });
    }


        private DatabaseReference getCaloriesRef(String ref) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        final String date = today.getYear() + 1900 + "-" + (1 + today.getMonth()) + "-" + today.getDate();
        return mDatabase.child("Calories").child(userId).child(date).child(ref);
    }

    private void GetDataFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref_history = database.getReference("history");
        ref_calories = database.getReference("Calories");

        getCaloriesRef("totalcalories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    calRef1 = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
                } else {
                    calRef1 = 0f;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        getCaloriesRef("totalfat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user_fat1 = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
                } else {
                    user_fat1 = 0f;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        getCaloriesRef("totalcarbs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user_carbs1 = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
                } else {
                    user_carbs1 = 0f;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        getCaloriesRef("totalprotein").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user_protein1 = Float.parseFloat(String.valueOf(dataSnapshot.getValue()));
                } else {
                    user_protein1 = 0f;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public void GetProduct() {
        FoodFactsServer server = new FoodFactsServer();
        FoodFactsApi api = server.getApi();

        // https://world.openfoodfacts.org/api/v0/product/(barcode).json
        Call<FoodFactsApiResponse> call = api.getProduct(barcode);

        call.enqueue(new Callback<FoodFactsApiResponse>() {

            @Override
            public void onResponse(Call<FoodFactsApiResponse> call, Response<FoodFactsApiResponse> response) {
                if (response.isSuccessful()) {
                    product = response.body().getProduct();
                    if (product != null) {
                        Nutriments nutriment = product.getNutriments();
                        name = product.getProductName();
                        url = product.getImageFrontUrl();
                        energyValue.setText("Calories: " + nutriment.getCalories() + " g");
                        proteins.setText("Proteins: " + nutriment.getProteins() + " g");
                        fat.setText("Fats: " + nutriment.getFat() + " g");
                        carbohydrates.setText("Carbs: " + nutriment.getCarbohydrates() + " g");
                        productName.setText(name);
                        new DownLoadImageTask(picture).execute(url);
                        button.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(DetailActivity.this, "NOT FOUND FOOD", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<FoodFactsApiResponse> call, Throwable t) {
            }
        });


    }

    private class DownLoadImageTask extends AsyncTask<String,Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
