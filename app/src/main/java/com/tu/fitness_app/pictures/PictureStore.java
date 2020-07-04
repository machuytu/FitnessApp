package com.tu.fitness_app.pictures;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.tu.fitness_app.Model.Product;

import java.io.File;

/**
 * Manage pictures download and storage
 */
public class PictureStore {
    private static final String FILE_EXTENSION = ".jpg";

    private Context context;
    
    public PictureStore(Context context) {
        this.context = context;
        context.getFilesDir();
    }

    /**
     * Load the big image of the product and display it into an {@link ImageView}
     * @param product
     * @param imageView
     */
    public void loadImage(Product product, ImageView imageView) {
        loadPicture(product.getImageFrontUrl(), getImagePath(product.getBarcode()), imageView);
    }

    /**
     * Load the small image of the product and display it into an {@link ImageView}
     * @param product
     * @param imageView
     */
    public void loadThumbnail(Product product, ImageView imageView) {
        loadPicture(product.getImageFrontSmallUrl(), getThumbnailPath(product.getBarcode()), imageView);
    }

    private String getImagePath(String barcode) {
        return context.getFilesDir().getAbsolutePath() + "/" + barcode + FILE_EXTENSION;
    }

    private String getThumbnailPath(String barcode) {
        return context.getFilesDir().getAbsolutePath() + "/" + barcode + "_thumb" + FILE_EXTENSION;
    }

    private void loadPicture(String url, String destination, ImageView imageView) {
        File image = new File(destination);
        if (image.exists()) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(destination));
        } else {
            new FileDownloader(url, destination, imageView).execute();
        }
    }
}
