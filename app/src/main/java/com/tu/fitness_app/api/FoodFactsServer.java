package com.tu.fitness_app.api;

import com.google.android.gms.analytics.ecommerce.Product;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FoodFactsServer {
    public static final String BASE_URL = "https://world.openfoodfacts.org/api/v0/";

    private FoodFactsApi api;

    public FoodFactsServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(FoodFactsApi.class);
    }

    public void getProduct(String barcode, final Listener requestListener) {
        api.getProduct(barcode).enqueue(new Callback<FoodFactsApiResponse>() {
            @Override
            public void onResponse(Call<FoodFactsApiResponse> call, Response<FoodFactsApiResponse> response) {
                requestListener.requestCompleted(response.body().getProduct());
            }

            @Override
            public void onFailure(Call<FoodFactsApiResponse> call, Throwable t) {
                requestListener.requestFailure(t);
            }
        });
    }

    public FoodFactsApi getApi() {
        return api;
    }

    public interface Listener {
        void requestCompleted(Product product);
        void requestFailure(Throwable t);
    }
}
