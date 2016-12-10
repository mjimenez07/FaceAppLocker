package com.example.developer.facetracker;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConfirmPinActivity extends AppCompatActivity {
    public SharedPreferences mSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pin);
        init();
    }

    private  void init() {
        mSharedPreference = getSharedPreferences("UserPin", MODE_PRIVATE);
        final String userPassword = mSharedPreference.getString("pin","");
        final EditText confirmPin = (EditText) findViewById(R.id.confirm_pin);
        Button confirmButton = (Button) findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmPin.getText().toString().equals(userPassword)){
                    Log.v("Pin matched","true");
                }
            }
        });
    }
}
