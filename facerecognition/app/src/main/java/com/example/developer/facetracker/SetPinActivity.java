package com.example.developer.facetracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetPinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);
        init();
    }


    private void init() {
        final EditText setPin = (EditText) findViewById(R.id.set_pin);
        Button saveButton = (Button) findViewById(R.id.buttonsave);
        final SharedPreferences.Editor editor = getEditor(getApplicationContext());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("pin", setPin.getText().toString());
                if (editor.commit()) {
                    startConfirmActivity();
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
