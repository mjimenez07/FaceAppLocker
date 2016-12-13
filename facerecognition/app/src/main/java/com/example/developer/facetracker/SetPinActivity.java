package com.example.developer.facetracker;

import android.content.Context;
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
import android.widget.Toast;

public class SetPinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);
        init();
    }


    private void init() {
        final EditText setPin = (EditText) findViewById(R.id.set_pin);
        final SharedPreferences.Editor editor = getEditor(getApplicationContext());

        setPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    editor.putString( "pin", setPin.getText().toString() );
                    if ( editor.commit() ) {
                        startConfirmActivity();
                    }
                }
                return false;
            }
        });

        setPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 4) {
                    Toast toast = Toast.makeText( getApplicationContext(), "The pin must have 4 digits", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }

    private void startConfirmActivity() {
        Intent intent = new Intent(getApplicationContext(), ConfirmPinActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    //getting shared preference instance
    public SharedPreferences getSharedPrerence(Context context) {
        SharedPreferences shrdprefences = context.getSharedPreferences("UserPin", Context.MODE_PRIVATE);
        return shrdprefences;
    }

    public SharedPreferences.Editor getEditor(Context context) {
        return getSharedPrerence(context).edit();
    }
}
