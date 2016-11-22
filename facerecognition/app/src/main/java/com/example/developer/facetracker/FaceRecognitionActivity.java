package com.example.developer.facetracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.developer.facetracker.ui.camera.CameraSourcePreview;
import com.example.developer.facetracker.ui.camera.GraphicOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FaceRecognitionActivity extends AppCompatActivity {
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private SharedPreferences mSharedPref;
    private String mEyesDistance;
    private String mLeftEyeMouthDistance;
    private String mRightEyeMouthDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        mSharedPref = getSharedPreferences("FaceInfo", MODE_PRIVATE);
        mEyesDistance = mSharedPref.getString("Eyes_distance_ratio", null);
        mLeftEyeMouthDistance = mSharedPref.getString("Left_eye_bottom_mouth_ratio", null);
        mRightEyeMouthDistance = mSharedPref.getString("Right_eye_bottom_mouth_ratio", null);
        createCameraSource();
    }

    private void createCameraSource() {
        Context context = getApplicationContext();

        FaceDetector detector = createFaceDetector(context);

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640,480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    private FaceDetector createFaceDetector(Context context) {
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
    protected void onStop() {
        super.onStop();
        mPreview.stop();
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

    public void release() {
        sendCustomBroadcast();
        finish();
    }

    public class FaceTrackerFactory extends Tracker<Face> {
        public FaceGraphic mFaceGraphic;
        public int index = 0;

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

        FaceDetailsAvg faceDetailsAvg = new FaceDetailsAvg();

        private Map<Integer, PointF> mPreviousProportions = new HashMap<>();

        @Override
        public void onNewItem(int id, Face item) {
            super.onNewItem(id, item);
            mFaceGraphic = new FaceGraphic(mGraphicOverlay);
        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            super.onUpdate(detections, face);
            mGraphicOverlay.add(mFaceGraphic);

            updatePreviousProportions(face);

            PointF leftEyePosition = getLandmarkPosition(face, Landmark.LEFT_EYE);
            PointF rightEyePosition = getLandmarkPosition(face, Landmark.RIGHT_EYE);
            PointF bottomMouthPosition = getLandmarkPosition(face, Landmark.BOTTOM_MOUTH);

            landMarkProcessor(leftEyePosition, rightEyePosition, bottomMouthPosition);
            mFaceGraphic.updateFace(face);
        }

        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);
            mGraphicOverlay.remove(mFaceGraphic);
        }

        @Override
        public void onDone() {
            super.onDone();
            mGraphicOverlay.remove(mFaceGraphic);
        }


        private void updatePreviousProportions(Face face) {
            for (Landmark landmark : face.getLandmarks()) {
                PointF position = landmark.getPosition();
                float xProp = (position.x - face.getPosition().x) / face.getWidth();
                float yProp = (position.y - face.getPosition().y) / face.getHeight();
                mPreviousProportions.put(landmark.getType(), new PointF(xProp, yProp));
            }
        }

        private PointF getLandmarkPosition(Face face, int landMarkID) {
            for (Landmark landmark : face.getLandmarks()) {
                if (landmark.getType() == landMarkID) {
                    return landmark.getPosition();
                }
            }

            PointF prop = mPreviousProportions.get(landMarkID);
            if (prop == null) {
                return null;
            }

            float x = face.getPosition().x + (prop.x * face.getWidth());
            float y = face.getPosition().y + (prop.y * face.getHeight());
            return new PointF(x, y);
        }


        private void landMarkProcessor(PointF leftEyePosition, PointF rightEyePosition, PointF bottomMouthPosition) {
            if (index <= 20) {
                double leftEyeXposition = (double) leftEyePosition.x * mFaceGraphic.scale;
                double leftEyeYposition = (double) leftEyePosition.y * mFaceGraphic.scale;
                double rightEyeXposition = (double) rightEyePosition.x * mFaceGraphic.scale;
                double rightEyeYposition = (double) rightEyePosition.y * mFaceGraphic.scale;
                double bottomMouthXposition = (double) bottomMouthPosition.x * mFaceGraphic.scale;
                double bottomMouthYposition = (double) bottomMouthPosition.y * mFaceGraphic.scale;

                if ((leftEyeXposition != 0) && (leftEyeYposition != 0) && (rightEyeXposition != 0) && (rightEyeYposition != 0) && (bottomMouthXposition != 0) && (bottomMouthYposition != 0)) {
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
            }

            if (index == 20) {
                Log.v("Eyes distance ratio", faceDetailsAvg.eyesRatio + "");
                Log.v("lefteyedistanceratio ", faceDetailsAvg.leftEyeMouthRatio+ "");
                Log.v("righteyedistanceratio ", faceDetailsAvg.rightEyeMouthRatio+ "");

                if (mEyesDistance != null && mLeftEyeMouthDistance != null && mRightEyeMouthDistance != null) {
                    if (String.format("%.2f",faceDetailsAvg.eyesRatio) == mEyesDistance && String.format("%.2f", faceDetailsAvg.leftEyeMouthRatio) == mLeftEyeMouthDistance && String.format("%.2f", faceDetailsAvg.rightEyeMouthRatio) == mRightEyeMouthDistance) {
                        Log.v("Recognize activity","User recognized");
                        release();
                    } else {
                        Log.v("Recognize activity", "User not recognized");
                        index = 0;
                    }
                } else {
                    Log.v("Distance values:", "are 0");
                }
            }
        }
    }
}
