package com.example.imageuploaddropbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{


    Button Start_service;
    Button Stop_service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissionGranted();
        Start_service = findViewById(R.id.Start);
        Stop_service = findViewById(R.id.stop);

        Start_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, mine_service.class));
            }
        });


        Stop_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, mine_service.class));
                Toast.makeText(getApplicationContext(), "SERVICE Successfully STOPPED.", Toast.LENGTH_SHORT)
                        .show();

            }
        });
    }

    private void checkPermissionGranted() {
        if(( ActivityCompat.checkSelfPermission(getApplicationContext() , Manifest.permission.READ_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED ) && ( ActivityCompat.checkSelfPermission(getApplicationContext() , Manifest.permission.WRITE_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED )) {
            Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
           requestPermission();
        }
        else {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE} ,123);
    }



}
