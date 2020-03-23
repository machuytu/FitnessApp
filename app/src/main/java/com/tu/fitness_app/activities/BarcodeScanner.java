package com.tu.fitness_app.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import com.google.zxing.Result;
import com.tu.fitness_app.R;

import java.net.ConnectException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;

public class BarcodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public static final int REQUEST_CAM = 1;
    private ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zXingScannerView = new ZXingScannerView(this);
        setContentView(zXingScannerView);

        if (checkPermission()) {
            Toast.makeText(BarcodeScanner.this, "Permission already granted", Toast.LENGTH_LONG).show();
        } else {
            requestPermissions();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermission()) {
            if(zXingScannerView == null) {
                zXingScannerView = new ZXingScannerView(this);
                setContentView(zXingScannerView);
            }
            zXingScannerView.setResultHandler(this);
            zXingScannerView.startCamera();
        } else {
            requestPermissions();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        zXingScannerView.stopCamera();
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[] {CAMERA} , REQUEST_CAM);
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        if (requestCode == REQUEST_CAM) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    Toast.makeText(BarcodeScanner.this, "Permission Granted!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(BarcodeScanner.this, "Permission Denied!", Toast.LENGTH_LONG).show();
                    if (shouldShowRequestPermissionRationale(CAMERA)) {
                        displayAlertMessage("Please both permission with allow access",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{CAMERA}, REQUEST_CAM);
                                    }
                                });
                    }
                }
            }
        }
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(BarcodeScanner.this).setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel",null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String scanResult = result.getText();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SCAN RESULT:");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                zXingScannerView.resumeCameraPreview(BarcodeScanner.this);
            }
        });
        builder.setNeutralButton("View", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
                startActivity(intent);
            }
        });
        builder.setMessage(scanResult);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
