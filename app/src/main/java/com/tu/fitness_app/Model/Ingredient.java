package com.tu.fitness_app.Model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.google.android.gms.analytics.ecommerce.Product;

@Entity(tableName = "ingredients",
        foreignKeys = @ForeignKey(entity = Product.class,
                parentColumns = "uid",
                childColumns = "product_id"),
        indices = {@Index(value = {"product_id"})})

public class Ingredient {
    @ColumnInfo(name = "product_id")
    private long productId;

    private String id;
    private String text;
    private int rank;

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
