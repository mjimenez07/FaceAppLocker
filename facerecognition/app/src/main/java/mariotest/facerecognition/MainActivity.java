package mariotest.facerecognition;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    private CameraPreview mPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreview  = new CameraPreview(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_frame);
        preview.addView(mPreview);
    }


    public void captureButton(View view) {
//        mPreview.callTakePicture();
        Log.d("TODO!:","A few errors on capture pictures callback");
    }

}
