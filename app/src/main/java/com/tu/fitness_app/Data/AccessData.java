package com.tu.fitness_app.Data;

import android.content.Context;
import android.os.AsyncTask;

import com.tu.fitness_app.Model.Ingredient;
import com.tu.fitness_app.Model.Product;
import com.tu.fitness_app.Utils.Ln;
import com.tu.fitness_app.api.FoodFactsServer;
import com.tu.fitness_app.pictures.PictureStore;

import java.util.ArrayList;
import java.util.List;

public class AccessData {
    private PictureStore pictureStore;
    private FoodFactsServer server;

    private static AccessData instance;

    private  AccessData(Context context) {
        server = new FoodFactsServer();
        pictureStore = new PictureStore(context);
    }

    public  static AccessData getInstance(Context context) {
        if (instance == null) {
            instance = new AccessData(context);
        }

        return instance;
    }

    public PictureStore getPictureStore() {
        return pictureStore;
    }

    public void getIngredients(Product product, IngredientsQueryListener listener) {
        new IngredientsQueryTask(listener).execute(product.getUid());
    }

    public void queryAndSaveProduct(String barcode, AddProductListener listener) {
        Ln.v("queryAndSaveProduct " + barcode);
        new AddProductQuery(barcode, listener);
    }

    private class AddProductQuery implements FoodFactsServer.Listener {
        private AddProductListener listener;
        private Product product;

        public AddProductQuery(String barcode, AddProductListener listener) {
            this.listener = listener;
            server.getProduct(barcode, this);
        }

        @Override
        public void requestCompleted(Product product1) {
            Ln.v("requestCompleted " + product1);
            this.product = product1;
        }

        @Override
        public void requestFailure(Throwable t) {
            if (listener != null) {
                listener.networkRequestError(t.getMessage());
            }
        }
    }

    private class IngredientsQueryTask extends AsyncTask<Long, Void, List<Ingredient>> {
        private IngredientsQueryListener listener;

        public IngredientsQueryTask(IngredientsQueryListener listener) {
            this.listener = listener;
        }

        @Override
        protected List<Ingredient> doInBackground(Long... longs) {

            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<Ingredient> result) {
            if (listener != null) {
                listener.allIngredientsLoaded(result);
            }
        }
    }
}
