package com.example.developer.facetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class ConfirmPinActivity extends AppCompatActivity {
    public SharedPreferences mSharedPreferences;
    public String userPassword;
    public EditText confirmPin;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_confirm_pin );
        init();
    }

    private  void init() {
        mSharedPreferences = getSharedPreferences( "UserPin", MODE_PRIVATE );
        userPassword = mSharedPreferences.getString( "pin", "" );
        confirmPin = ( EditText ) findViewById( R.id.confirm_pin );

        confirmPin.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction( TextView textView, int i, KeyEvent keyEvent ) {
                if ( i == EditorInfo.IME_ACTION_DONE ) {
                    if ( confirmPin() ) {
                        startFaceTrackerActivity();
                    }
                }
                return false;
            }
        });
    }

    private boolean confirmPin() {
        return confirmPin.getText().toString().equals( userPassword );
    }

    private void startFaceTrackerActivity() {
        Intent intent = new Intent( getApplicationContext(), FaceTrackerActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity( intent );
        finish();
    }
}
