package com.lengel.barcodeappdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private Button button;
    private TextView myresult;
    private ZXingScannerView ScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btn);
        myresult = findViewById(R.id.result);
        ScannerView = findViewById(R.id.zxscaner);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)      //ask permission
                == PackageManager.PERMISSION_DENIED){
            Toast.makeText(this, "Nemáš povolenie", Toast.LENGTH_LONG).show();
        }

        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, 1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){    //portrait mode
                    IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                    integrator.setCaptureActivity(CaptureActivityPortrait.class);
                    integrator.setCameraId(0);  // Use a specific camera of the device
                    integrator.setOrientationLocked(true);
                    integrator.setBeepEnabled(true);
                    integrator.initiateScan();
                    ScannerView.setResultHandler(MainActivity.this);
                    ScannerView.resumeCameraPreview(MainActivity.this);
                }
                else {          //landscape mode
                    IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                    integrator.setCaptureActivity(CaptureActivityLandscape.class);
                    integrator.setCameraId(0);  // Use a specific camera of the device
                    integrator.setOrientationLocked(true);
                    integrator.setBeepEnabled(true);
                    integrator.initiateScan();
                    ScannerView.setResultHandler(MainActivity.this);
                    ScannerView.resumeCameraPreview(MainActivity.this);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                myresult.setText(result.getContents());
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onDestroy() {
        ScannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        myresult.setText(rawResult.getText());
        ScannerView.resumeCameraPreview(MainActivity.this);
    }

}
