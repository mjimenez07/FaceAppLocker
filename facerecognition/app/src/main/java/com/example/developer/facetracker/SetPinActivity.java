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


public class SetPinActivity extends AppCompatActivity {
    public EditText setPin;
    private SharedPreferences.Editor editor;
    public SharedPreferences mSharedPreferences;
    public String pin;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_set_pin );
        init();
    }


    private void init() {
        setPin = ( EditText ) findViewById( R.id.set_pin );
        editor  = getEditor( getApplicationContext() );
        setPin.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence charSequence, int i, int i1, int i2 ) {
                /**
                 *  nothing to do here
                 * ¯\_(ツ)_/¯
                 * */
            }

            @Override
            public void onTextChanged( CharSequence charSequence, int i, int i1, int i2 ) {
                /**
                 *  nothing to do here
                 * ¯\_(ツ)_/¯
                 * */
            }

            @Override
            public void afterTextChanged( Editable editable ) {
                if ( editable.length() < 4 ) {
                    setPin.setError( getString( R.string.pin_invalid_length_error_message ) );
                }
            }
        });

        setPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ( i == EditorInfo.IME_ACTION_GO ) {
                    setUpEditTextHintMessage();
                    if ( ( setPin.getText().length() == 4 ) && ( pin.equals("") ) ) {
                        editor.putString( "pin", setPin.getText().toString() );
                        if ( editor.commit() ) {
                            setPin.clearFocus();
                            setPin.getText().clear();
                            setPin.setHint( getString( R.string.confirm_pin ) );
                        }
                    } else  {
                        if ( confirmPin() ) {
                            startFaceTrackerActivity();
                        } else {
                            setPin.clearFocus();
                            setPin.getText().clear();
                            setPin.setHint( getString( R.string.wrong_pin_try_again ) );
                        }
                    }
                }
                return false;
            }
        });
    }

    private void setUpEditTextHintMessage() {
        mSharedPreferences = getSharedPreferences( "UserPin", MODE_PRIVATE );
        pin = mSharedPreferences.getString( "pin", "" );
        if ( !pin.equals("") ) {
            setPin.setHint( getString( R.string.confirm_pin ) );
        }
    }

    private boolean confirmPin() {
        pin = mSharedPreferences.getString( "pin", "" );
        return setPin.getText().toString().equals( pin );
    }

    private void startFaceTrackerActivity() {
        Intent intent = new Intent( getApplicationContext(), FaceTrackerActivity.class );
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
