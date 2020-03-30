package com.tu.fitness_app.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.tu.fitness_app.Model.Product;
import com.tu.fitness_app.R;
import com.tu.fitness_app.api.FoodFactsApi;
import com.tu.fitness_app.api.FoodFactsApiResponse;
import com.tu.fitness_app.api.FoodFactsServer;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    String barcode = BarcodeScanner.myResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        FoodFactsServer server = new FoodFactsServer();
        FoodFactsApi api = server.getApi();

        Call<FoodFactsApiResponse> call = api.getProduct(barcode);


        try {
            Response<FoodFactsApiResponse> response = call.execute();
            Product product = response.body().getProduct();
            String name = product.getProductName();

            Log.d("FoodFactsTest", name);
        } catch (IOException e) {
            Log.e("FoodFactsTest", "IOException", e);
        }
    }
}
