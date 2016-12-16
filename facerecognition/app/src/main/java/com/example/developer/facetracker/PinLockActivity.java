package com.example.developer.facetracker;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PinLockActivity extends AppCompatActivity {
    public SharedPreferences mSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_lock);
        init();
    }

    private  void init() {
        mSharedPreference = getSharedPreferences("UserPin", MODE_PRIVATE);
        final String userPassword = mSharedPreference.getString("pin","");
        final EditText confirmPin = (EditText) findViewById(R.id.confirm_pin);

        confirmPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (confirmPin.getText().toString().equals(userPassword)){
                        finish();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }
}
