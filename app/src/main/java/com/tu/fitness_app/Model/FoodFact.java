package com.tu.fitness_app.Model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "products", indices = {@Index(value = {"barcode"}, unique = true)})
public class FoodFact extends BasicObject {
    @SerializedName("_id")
    private String barcode;
    private Long timestamp;

    @Ignore
    private List<Ingredient> ingredients;

    @ColumnInfo(name = "image_front_url")
    @SerializedName("image_front_url")
    private String imageFrontUrl;

    @ColumnInfo(name = "image_front_small_url")
    @SerializedName("image_front_small_url")
    private String imageFrontSmallUrl;

    @ColumnInfo(name = "product_name")
    @SerializedName("product_name")
    private String productName;

    @Embedded
    private Nutriment nutriment;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getImageFrontUrl() {
        return imageFrontUrl;
    }

    public void setImageFrontUrl(String imageFrontUrl) {
        this.imageFrontUrl = imageFrontUrl;
    }

    public String getImageFrontSmallUrl() {
        return imageFrontSmallUrl;
    }

    public void setImageFrontSmallUrl(String imageFrontSmallUrl) {
        this.imageFrontSmallUrl = imageFrontSmallUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Nutriment getNutriments() {
        return nutriment;
    }

    public void setNutriments(Nutriment nutriment) {
        this.nutriment = nutriment;
    }

    @Override
    public String toString() {
        return barcode + " / " + productName;
    }
}
