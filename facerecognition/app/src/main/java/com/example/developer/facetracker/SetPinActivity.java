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
import android.widget.Toast;

public class SetPinActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_set_pin );
        init();
    }


    private void init() {
        final EditText setPin = ( EditText ) findViewById( R.id.set_pin );
        final SharedPreferences.Editor editor = getEditor( getApplicationContext() );

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
                    Toast toast = Toast.makeText( getApplicationContext(), "The pin must have 4 digits", Toast.LENGTH_SHORT );
                    toast.show();
                } else  {
                    setPin.setOnEditorActionListener( new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction( TextView textView, int i, KeyEvent keyEvent ) {
                            if ( i == EditorInfo.IME_ACTION_GO ) {
                                editor.putString( "pin", setPin.getText().toString() );
                                if ( editor.commit() ) {
                                    startConfirmActivity();
                                }
                            }
                            return false;
                        }
                    });
                }
            }
        });

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
