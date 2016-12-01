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


    // Creates and start the camera.
    private void createCameraSource() {
        Context context = getApplicationContext();

        FaceDetector detector = createFaceDetector(context);

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640,480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }
    //==============================================================================================
    // Face Detector Builder
    //==============================================================================================

    /** Function to build the face detector.
     * @param context
     * @return face detector instance
     */
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


    /**
     * function to notify the service that the current app doesn't need to be tracked anymore
     * */
    public  void sendCustomBroadcast() {
        Intent intent = new Intent();
        intent.setAction(getString(R.string.broadcast_enable_app_receiver_action));
        sendBroadcast(intent);
    }

    public void release() {
        sendCustomBroadcast();
        finish();
    }


    //Class face trackerFactory to track and process the landmarks
    public class FaceTrackerFactory extends Tracker<Face> {
        public FaceGraphic mFaceGraphic;
        public int index = 0;



        /* class created to handle the distance mapping in order to
        return an avg of the distances tracked
         */

         FaceDetailsProccesor faceDetailsAvg;

        // Record the previously seen proportions of the landmark locations relative to the bounding box
        // of the face.  These proportions can be used to approximate where the landmarks are within the
        // face bounding box if the eye landmark is missing in a future update.
        private Map<Integer, PointF> mPreviousProportions = new HashMap<>();



        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int id, Face item) {
            super.onNewItem(id, item);
            mFaceGraphic = new FaceGraphic(mGraphicOverlay);
        }


        /**
         * Update the position/characteristics of the face within the overlay.
         */
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


        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);
            mGraphicOverlay.remove(mFaceGraphic);
        }


        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            super.onDone();
            mGraphicOverlay.remove(mFaceGraphic);
        }

        /*
        * function updatePreviousProportions
        * here we have a mapping of the landmarks previous positions
        * to avoid 0 value registration if the face it's removed
        * from the camera preview overlay
         * @param face
        * */
        private void updatePreviousProportions(Face face) {
            for (Landmark landmark : face.getLandmarks()) {
                PointF position = landmark.getPosition();
                float xProp = (position.x - face.getPosition().x) / face.getWidth();
                float yProp = (position.y - face.getPosition().y) / face.getHeight();
                mPreviousProportions.put(landmark.getType(), new PointF(xProp, yProp));
            }
        }


        /**
         * function getLandmarkPosition
         *
         * @param face       face objects that we will use to take the landmarks
         * @param landMarkID int value of the landmark needed
         *                   return landmark position
         **/
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


        /**
         * Function landMarkProcessor
         * @param  leftEyePosition
         * @param  rightEyePosition
         * @param  bottomMouthPosition
         * this will calculate the distance of each point and save it in the sharedpreference
         * after that will launch the next activity
         * */
        private void landMarkProcessor(PointF leftEyePosition, PointF rightEyePosition, PointF bottomMouthPosition) {
            if (index <= 20) {
                double leftEyeXposition = (double) leftEyePosition.x;
                double leftEyeYposition = (double) leftEyePosition.y;
                double rightEyeXposition = (double) rightEyePosition.x;
                double rightEyeYposition = (double) rightEyePosition.y;
                double bottomMouthXposition = (double) bottomMouthPosition.x;
                double bottomMouthYposition = (double) bottomMouthPosition.y;

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
                Log.v("Eyes_distance_ratio", String.format("%.2f", faceDetailsAvg.eyesRatio) );
                Log.v("Left_eye_mouth_ratio", String.format("%.2f", faceDetailsAvg.leftEyeMouthRatio));
                Log.v("Right_eye_mouth_ratio", String.format("%.2f", faceDetailsAvg.rightEyeMouthRatio));

                if (mEyesDistance != null && mLeftEyeMouthDistance != null && mRightEyeMouthDistance != null) {
                    if (String.format("%.2f",faceDetailsAvg.eyesRatio) == mEyesDistance && String.format("%.2f", faceDetailsAvg.leftEyeMouthRatio) == mLeftEyeMouthDistance && String.format("%.2f", faceDetailsAvg.rightEyeMouthRatio) == mRightEyeMouthDistance) {
                        Log.v("Recognize activity","User recognized");
                        release();
                    } else {
                        Log.v("Recognize activity", "User not recognized");
                        faceDetailsAvg.eyesRatios.clear();
                        faceDetailsAvg.rightEyeMouthRatios.clear();
                        faceDetailsAvg.leftEyeMouthRatios.clear();
                        faceDetailsAvg.eyesRatio = 0;
                        faceDetailsAvg.leftEyeMouthRatio = 0;
                        faceDetailsAvg.rightEyeMouthRatio = 0;
                        index = 0;
                    }
                } else {
                    Log.v("Distance values:", "are 0");
                }
            }
        }
    }
}
