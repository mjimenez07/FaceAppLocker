package com.example.developer.facetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
                        enableAccess();
                    } else {
                        confirmPin.clearFocus();
                        confirmPin.getText().clear();
                        confirmPin.setHint( getString( R.string.wrong_pin_try_again ) );
                    }
                }
                return false;
            }
        });

        confirmPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (confirmPin.getText().toString().equals(userPassword)) {
                    enableAccess();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    /**
     * function to notify the service that the current app doesn't need to be tracked anymore
     * */
    public  void sendCustomBroadcast() {
        Intent intent = new Intent();
        intent.setAction(getString(R.string.broadcast_enable_app_receiver_action));
        sendBroadcast(intent);
    }

    public void enableAccess() {
        sendCustomBroadcast();
        finish();
    }

}
