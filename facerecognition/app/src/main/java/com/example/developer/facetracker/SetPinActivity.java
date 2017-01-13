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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.developer.facetracker.utility.Constants;

public class SetPinActivity extends AppCompatActivity {
    public EditText setPin;
    private SharedPreferences.Editor editor;
    public SharedPreferences mSharedPreference;
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
        setUpEditTextHintMessage();
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
                    if ( ( setPin.getText().length() == 4 ) && ( pin.equals("") ) ) {
                        editor.putString( "pin", setPin.getText().toString() );
                        if ( editor.commit() ) {
                            Log.v("FaceApplocker", "change hint message");
                        }
                    } else  {
                        Log.v("FaceApplocker", "pin to be confirmed");
                    }
                }
                return false;
            }
        });
    }

    private void setUpEditTextHintMessage() {
        mSharedPreference = getSharedPreferences( "UserPin", MODE_PRIVATE );
        pin = mSharedPreference.getString( "pin", "" );

        if ( pin.equals("") ) {
            setPin.setHint( getString( R.string.pin_hint ) );
        } else {
            setPin.setHint( getString( R.string.confirm_pin ) );
        }

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
