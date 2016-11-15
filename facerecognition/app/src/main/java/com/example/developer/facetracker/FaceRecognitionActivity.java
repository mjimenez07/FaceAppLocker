package com.example.developer.facetracker;

/**
 * Created by Mario on 11/13/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import android.util.Log;
import android.view.View;
import com.example.developer.facetracker.ui.camera.CameraSourcePreview;
import com.example.developer.facetracker.ui.camera.GraphicOverlay;

import java.io.IOException;

public class FaceRecognitionActivity extends AppCompatActivity {
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        createCameraSource();
    }

    private void createCameraSource() {
        Context context = getApplicationContext();

        FaceDetector detector = createFaceDetector(context, mPreview);

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640,480)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();
    }

    private FaceDetector createFaceDetector(Context context, CameraSourcePreview mPreview) {
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(true)
                .setProminentFaceOnly(true)
                .setMode(FaceDetector.ACCURATE_MODE)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        Detector.Processor<Face> processor;
        Tracker<Face> tracker = new FaceTrackerFactory();
        processor = new LargestFaceFocusingProcessor(detector, tracker);
        detector.setProcessor(processor);

        if (!detector.isOperational()) {
            Log.v("FaceDetector", "Face detector dependencies are not yet available");
        }
        return detector;
    }

    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }


    private void startCameraSource() {

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            }catch (IOException e) {
                Log.e("Error", "Unable to start camera source", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startHomescreen=new Intent(Intent.ACTION_MAIN);
        startHomescreen.addCategory(Intent.CATEGORY_HOME);
        startHomescreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startHomescreen);
        finish();
    }

    public  void sendCustomBroadcast() {
        Intent intent = new Intent();
        intent.setAction(getString(R.string.broadcast_enable_app_receiver_action));
        sendBroadcast(intent);
    }


    public class FaceTrackerFactory extends Tracker<Face> {
        private FaceGraphic mFaceGraphic;

        @Override
        public void onNewItem(int id, Face item) {
            super.onNewItem(id, item);
            mFaceGraphic = new FaceGraphic(mGraphicOverlay);
        }

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detections, Face face) {
            super.onUpdate(detections, face);
            mGraphicOverlay.add(mFaceGraphic);

            mFaceGraphic.updateFace(face);
        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detections) {
            super.onMissing(detections);
            mGraphicOverlay.remove(mFaceGraphic);
        }
    }
}
