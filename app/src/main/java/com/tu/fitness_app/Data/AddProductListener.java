package com.tu.fitness_app.Data;

import com.google.android.gms.analytics.ecommerce.Product;

public interface AddProductListener {
    void networkRequestError(String message);
    void productNotFound();
    void productAdded(Product product);
}
