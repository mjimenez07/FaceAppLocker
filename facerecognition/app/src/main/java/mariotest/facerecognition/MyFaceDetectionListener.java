package mariotest.facerecognition;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by User on 6/14/2016.
 */
public class MyFaceDetectionListener implements Camera.FaceDetectionListener {
    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        if (faces.length > 0 ) {
            Log.d("FaceDetection","True");
        }else {
            Log.d("FaceDetection","False");
        }
    }
}
