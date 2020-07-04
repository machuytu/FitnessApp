package com.tu.fitness_app.pictures;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.tu.fitness_app.Utils.Ln;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Download picture files from an HTTP server and store them locally
 * Used only by the {@link PictureStore}
 */
class FileDownloader implements Callback {

    private OkHttpClient client;
    private String url;
    private String destination;
    private ImageView imageView;

    /**
     *
     * @param url
     * @param destination
     * @param imageView
     */
    FileDownloader(String url, String destination, ImageView imageView) {
        client = new OkHttpClient();
        this.url = url;
        this.destination = destination;
        this.imageView = imageView;
    }

    void execute() {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(this);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Ln.e("Error saving " + url + " to " + destination + ": " + e.getMessage(), e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        File file = new File(destination);
        FileOutputStream output = null;
        BufferedInputStream input = null;
        try {
            output = new FileOutputStream(file);
            input = new BufferedInputStream(response.body().byteStream());

            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            Ln.d("URL " + url + " saved into " + destination);

            if (imageView != null) {
                ((Activity)imageView.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(BitmapFactory.decodeFile(destination));
                    }
                });
            }
        } catch (IOException e) {
            Ln.e("Error saving file to " + destination, e);
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        }
    }
}
