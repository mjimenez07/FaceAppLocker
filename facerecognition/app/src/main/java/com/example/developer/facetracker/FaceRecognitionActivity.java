package com.example.developer.facetracker;

/**
 * Created by Mario on 11/13/2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FaceRecognitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        Button okButton = (Button) findViewById(R.id.okButton);
        SharedPreferences sharedPref = getSharedPreferences("FaceInfo", MODE_PRIVATE);
        float eyesRatios = sharedPref.getFloat("Eyes_ratio", 0);
        okButton.setOnClickListener(new View.OnClickListener() {
            EditText passwdText = (EditText) findViewById(R.id.password);
            @Override
            public void onClick(View v) {

                if (passwdText.getText() != null) {
                    sendCustomBroadcast();
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startHomescreen=new Intent(Intent.ACTION_MAIN);
        startHomescreen.addCategory(Intent.CATEGORY_HOME);
        startHomescreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startHomescreen);
        finish();
    }

    public  void sendCustomBroadcast() {
        Intent intent = new Intent();
        intent.setAction(getString(R.string.broadcast_enable_app_receiver_action));
        sendBroadcast(intent);
    }}
