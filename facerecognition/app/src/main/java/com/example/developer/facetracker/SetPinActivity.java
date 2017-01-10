package com.example.developer.facetracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.developer.facetracker.utility.Constants;

public class SetPinActivity extends AppCompatActivity {
    private EditText setPin;
    private SharedPreferences.Editor editor;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_set_pin );
        init();
    }


    private void init() {
        setPin = ( EditText ) findViewById( R.id.set_pin );
        editor  = getEditor( getApplicationContext() );
    }


    private void confirmPinMatches() {
        setPin.setText("");
        setPin.clearFocus();
        setPin.setHint( getString( R.string.confirm_pin ) );

    }

    private void startConfirmActivity() {
        Intent intent = new Intent( getApplicationContext(), ConfirmPinActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity( intent );
        finish();
    }

    //getting shared preference instance
    public SharedPreferences getSharedPreferences( Context context ) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( "UserPin", Context.MODE_PRIVATE );
        return sharedPreferences;
    }

    public SharedPreferences.Editor getEditor( Context context ) {
        return getSharedPreferences( context ).edit();
    }
}
