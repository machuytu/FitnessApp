package com.tu.fitness_app.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tu.fitness_app.Model.Nutriments;
import com.tu.fitness_app.Model.Product;
import com.tu.fitness_app.R;
import com.tu.fitness_app.api.FoodFactsApi;
import com.tu.fitness_app.api.FoodFactsApiResponse;
import com.tu.fitness_app.api.FoodFactsServer;

import java.io.InputStream;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    public static Product product;
    String barcode = BarcodeScanner.myResult;
    String name, url, engry;
    private ImageView picture;
    private TextView productName;
    private TextView energyValue;
    private ListView ingredientsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        picture = findViewById(R.id.picture);
        productName = findViewById(R.id.product_name);
        energyValue = findViewById(R.id.energy_value);
        ingredientsList = findViewById(R.id.ingredients_list);



        GetProduct();
    }



    public void GetProduct()  {
        FoodFactsServer server = new FoodFactsServer();
        FoodFactsApi api = server.getApi();

        // https://world.openfoodfacts.org/api/v0/product/3329770057258.json
        Call<FoodFactsApiResponse> call = api.getProduct(barcode);

        call.enqueue(new Callback<FoodFactsApiResponse>() {

            @Override
            public void onResponse(Call<FoodFactsApiResponse> call, Response<FoodFactsApiResponse> response) {
                if(response.isSuccessful()){
                    product = response.body().getProduct();

                    Nutriments nutriments = product.getNutriments();
                    name = product.getProductName();
                    url = product.getImageFrontUrl();
                    engry = product.getNutriments().getEnergyUnit();
                    energyValue.setText(engry);
//                    energyValue.setText(nutriment.getEnergyValue() + " " + nutriment.getEnergyUnit());
                    productName.setText(name);
                    if (nutriments != null) {
                        energyValue.setText(nutriments.getEnergyValue() + " " + nutriments.getEnergyUnit());
                    }
                    new DownLoadImageTask(picture).execute(url);
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

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }
}
