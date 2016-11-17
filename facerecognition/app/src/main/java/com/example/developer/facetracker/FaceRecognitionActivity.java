package com.example.developer.facetracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import android.util.Log;
import android.view.View;
import com.example.developer.facetracker.ui.camera.CameraSourcePreview;
import com.example.developer.facetracker.ui.camera.GraphicOverlay;

import java.io.IOException;
import java.util.ArrayList;

public class FaceRecognitionActivity extends AppCompatActivity {
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    static FaceTrackerFactory.FaceDetailsAvg faceDetailsAvg;
    int index = 0;
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
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
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
        Intent startHomeScreen = new Intent(Intent.ACTION_MAIN);
        startHomeScreen.addCategory(Intent.CATEGORY_HOME);
        startHomeScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startHomeScreen);
        finish();
    }

    public  void sendCustomBroadcast() {
        Intent intent = new Intent();
        intent.setAction(getString(R.string.broadcast_enable_app_receiver_action));
        sendBroadcast(intent);
    }





    //==============================================================================================
    // Face Tracker Factory
    //==============================================================================================

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

            PointF leftEyePosition = getLandMarkPosition(face, Landmark.LEFT_EYE);
            PointF rightEyePosition = getLandMarkPosition(face, Landmark.RIGHT_EYE);
            PointF bottomMouthPosition = getLandMarkPosition(face, Landmark.BOTTOM_MOUTH);
            landMarkProcessor(leftEyePosition, rightEyePosition, bottomMouthPosition);

            mFaceGraphic.updateFace(face);
        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detections) {
            super.onMissing(detections);
            mGraphicOverlay.remove(mFaceGraphic);
        }

        private PointF getLandMarkPosition(Face face, int landMarkId) {
            for (Landmark landmark : face.getLandmarks()) {
                if (landmark.getType() == landMarkId) {
                    return face.getPosition();
                }
            }

            return null;
        }

        private void landMarkProcessor(PointF leftEyePosition, PointF rightEyePosition, PointF bottomMouthPosition) {
            while (index <= 20) {
                double leftEyeXposition = (double) leftEyePosition.x * mFaceGraphic.scale;
                double leftEyeYposition = (double) leftEyePosition.y * mFaceGraphic.scale;
                double rightEyeXposition = (double) rightEyePosition.x * mFaceGraphic.scale;
                double rightEyeYposition = (double) rightEyePosition.y * mFaceGraphic.scale;
                double bottomMouthXposition = (double) bottomMouthPosition.x * mFaceGraphic.scale;
                double bottomMouthYposition = (double) bottomMouthPosition.y * mFaceGraphic.scale;

                int eyesDistance = (int) Math.sqrt(Math.pow((rightEyeXposition - leftEyeXposition), 2) + Math.pow((rightEyeYposition - leftEyeYposition), 2));
                int rightEyeMouseDistance = (int) Math.sqrt(Math.pow((rightEyeXposition - bottomMouthXposition), 2) + Math.pow((rightEyeYposition - bottomMouthYposition), 2));
                int leftEyeMouseDistance = (int) Math.sqrt(Math.pow((leftEyeXposition - bottomMouthXposition), 2) + Math.pow((leftEyeYposition - bottomMouthYposition), 2));
                int minValue = Math.min(Math.min(eyesDistance, rightEyeMouseDistance), leftEyeMouseDistance);


                faceDetailsAvg.eyesRatios.add((double) eyesDistance / minValue);
                faceDetailsAvg.rightEyeMouthRatios.add((double) rightEyeMouseDistance / minValue);
                faceDetailsAvg.leftEyeMouthRatios.add((double) leftEyeMouseDistance / minValue);
                faceDetailsAvg.avg();
                index++;
            }

            if (index == 20) {
                Log.v("eyes distance", faceDetailsAvg.eyesRatio + "");
                Log.v("righteye-mouse", faceDetailsAvg.rightEyeMouthRatio + "");
                Log.v("lefteye-mouse", faceDetailsAvg.leftEyeMouthRatio + "");
            }

        }


        class FaceDetailsAvg {
            public ArrayList<Double> eyesRatios = new ArrayList();
            public ArrayList<Double> rightEyeMouthRatios = new ArrayList();
            public ArrayList<Double> leftEyeMouthRatios = new ArrayList();

            public double eyesRatio = 0;
            public double rightEyeMouthRatio = 0;
            public double leftEyeMouthRatio = 0;

            public void avg() {
                double tempEyesRatio = 0;
                for (int index = 0; index < eyesRatios.size(); index++) {
                    tempEyesRatio += eyesRatios.get(index);
                }
                eyesRatio = tempEyesRatio / eyesRatios.size();

                double tempRightEyeMouthRatios = 0;
                for (int index = 0; index < rightEyeMouthRatios.size(); index++) {
                    tempRightEyeMouthRatios += rightEyeMouthRatios.get(index);
                }
                rightEyeMouthRatio = tempRightEyeMouthRatios / rightEyeMouthRatios.size();

                double tempLeftEyeMouthRatio = 0;
                for (int index = 0; index < leftEyeMouthRatios.size(); index++) {
                    tempLeftEyeMouthRatio += leftEyeMouthRatios.get(index);
                }
                leftEyeMouthRatio = tempLeftEyeMouthRatio / leftEyeMouthRatios.size();
            }
        }



    }
}
