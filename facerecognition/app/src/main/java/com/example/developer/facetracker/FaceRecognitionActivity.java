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
import android.util.Range;
import android.widget.Toast;

import com.example.developer.facetracker.ui.camera.CameraSourcePreview;
import com.example.developer.facetracker.ui.camera.GraphicOverlay;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FaceRecognitionActivity extends AppCompatActivity {
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private SharedPreferences mSharedPref;
    private String mEyesDistanceRatio;
    private String mRightEyeNoseBaseDistanceRatio;
    private String mLeftEyeNoseBaseDistanceRatio;
    private String mNoseBaseMouthDistanceRatio;
    private String mRightMouthLeftMouthDistanceRatio;
    private String mRightMouthBottomMouthDistanceRatio;
    private String mLeftMouthBottomMouthDistanceRatio;
    private String mRightEyeMouthDistanceRatio;
    private String mLeftEyeMouthDistanceRatio;
    private FaceDetailsProcessor faceDetailsAvg = new FaceDetailsProcessor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        mSharedPref = getSharedPreferences("FaceInfo", MODE_PRIVATE);
        mEyesDistanceRatio = mSharedPref.getString( getString(R.string.eyes_distance_ratio), null );
        mRightEyeNoseBaseDistanceRatio = mSharedPref.getString( getString( R.string.right_eye_nose_base_distance_ratio ), null );
        mLeftEyeNoseBaseDistanceRatio = mSharedPref.getString( getString( R.string.left_eye_nose_base_distance_ratio ), null);
        mNoseBaseMouthDistanceRatio = mSharedPref.getString( getString( R.string.nose_base_bottom_mouth_distance_ratio ), null );
        mRightMouthLeftMouthDistanceRatio = mSharedPref.getString( getString( R.string.right_mouth_left_mouth_distance_ratio ), null );
        mRightMouthBottomMouthDistanceRatio = mSharedPref.getString( getString( R.string.right_mouth_bottom_mouth_distance_ratio ), null );
        mLeftMouthBottomMouthDistanceRatio = mSharedPref.getString( getString( R.string.left_mouth_bottom_mouth_distance_ratio ), null );
        mRightEyeMouthDistanceRatio = mSharedPref.getString( getString( R.string.right_eye_bottom_mouth_distance_ratio ), null );
        mLeftEyeMouthDistanceRatio = mSharedPref.getString( getString( R.string.left_eye_bottom_mouth_distance_ratio ), null );

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

    public void enableAccess() {
        Log.v("faceapplocker", "User Recognized");
        sendCustomBroadcast();
        finish();
    }


    //Class face trackerFactory to track and process the landmarks
    public class FaceTrackerFactory extends Tracker<Face> {
        public FaceGraphic mFaceGraphic;
        public int index = 0;
        public int matches = 0;


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

            PointF leftEyePosition = getLandmarkPosition( face, Landmark.LEFT_EYE );

            PointF rightEyePosition = getLandmarkPosition( face, Landmark.RIGHT_EYE );

            PointF noseBasePosition = getLandmarkPosition( face, Landmark.NOSE_BASE );

            PointF rightMouthPosition = getLandmarkPosition( face, Landmark.RIGHT_MOUTH );

            PointF leftMouthPosition = getLandmarkPosition( face, Landmark.LEFT_MOUTH );

            PointF bottomMouthPosition = getLandmarkPosition( face, Landmark.BOTTOM_MOUTH );


            landMarkProcessor( leftEyePosition, rightEyePosition, noseBasePosition, leftMouthPosition, rightMouthPosition, bottomMouthPosition );

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
         * Function landMa2rkProcessor
         * @param  leftEyePosition
         * @param  rightEyePosition
         * @param  noseBasePosition
         * @param  leftMouthPosition
         * @param  rightMouthPosition
         * @param bottomMouthPosition
         * this will calculate the distance of each point and save it in the sharedpreference
         * after that will launch the next activity
         * */
        private void landMarkProcessor(PointF leftEyePosition, PointF rightEyePosition, PointF noseBasePosition, PointF leftMouthPosition, PointF rightMouthPosition, PointF bottomMouthPosition) {
            //Here we calculate the distance of each point

            if (index < 10) {
                double leftEyeXPosition = (double) leftEyePosition.x;
                double leftEyeYPosition = (double) leftEyePosition.y;

                double rightEyeXPosition = (double) rightEyePosition.x;
                double rightEyeYPosition = (double) rightEyePosition.y;

                double noseBaseXPosition = (double) noseBasePosition.x;
                double noseBaseYPosition = (double) noseBasePosition.y;

                double leftMouthXPosition = (double) leftMouthPosition.x;
                double leftMouthYPosition = (double) leftMouthPosition.y;

                double rightMouthXPosition = (double) rightMouthPosition.x;
                double rightMouthYPosition = (double) rightMouthPosition.y;

                double bottomMouthXPosition = (double) bottomMouthPosition.x;
                double bottomMouthYPosition = (double) bottomMouthPosition.y;


                if ( ( leftEyeXPosition != 0 ) && ( leftEyeYPosition != 0 ) && ( rightEyeXPosition != 0 )
                        && ( rightEyeYPosition != 0 ) && ( bottomMouthXPosition != 0 )
                        && ( noseBaseXPosition !=0 ) && ( noseBaseYPosition !=0 )
                        && ( leftMouthXPosition !=0 ) && ( leftMouthYPosition !=0 )
                        && ( rightMouthXPosition !=0 ) && ( rightMouthYPosition !=0 )
                        && ( bottomMouthYPosition != 0 ) ) {

                    int eyesDistance = (int) Math.sqrt( Math.pow( ( rightEyeXPosition - leftEyeXPosition ), 2 ) + Math.pow( ( rightEyeYPosition - leftEyeYPosition), 2 ) );

                    int rightEyeNoseBaseDistance = (int) Math.sqrt( Math.pow( ( rightEyeXPosition - noseBaseXPosition ), 2 ) + Math.pow( ( rightEyeYPosition - noseBaseYPosition ), 2 ) );

                    int leftEyeNoseBaseDistance = (int) Math.sqrt( Math.pow( ( leftEyeXPosition - noseBaseXPosition ), 2 ) +  Math.pow( ( leftEyeYPosition - noseBaseYPosition ), 2 ) );

                    int noseBaseMouthDistance = (int) Math.sqrt( Math.pow( ( noseBaseXPosition - bottomMouthXPosition ), 2 ) + Math.pow( noseBaseYPosition - bottomMouthYPosition , 2 ) );

                    int rightMouthLeftMouthDistance = (int) Math.sqrt( Math.pow( ( rightMouthXPosition - leftMouthXPosition ) ,2 ) + Math.pow( ( rightMouthYPosition - leftMouthYPosition ), 2 ) );

                    int rightMouthBottomMouthDistance = (int) Math.sqrt( Math.pow( ( rightMouthXPosition - bottomMouthXPosition ),2 )  + Math.pow( ( rightMouthYPosition - bottomMouthYPosition ), 2)  );

                    int leftMouthBottomMouthDistance = (int) Math.sqrt( Math.pow( ( leftMouthXPosition - bottomMouthXPosition ),2 ) + Math.pow( ( leftMouthYPosition - bottomMouthYPosition ), 2 ) );

                    int rightEyeMouthDistance = (int) Math.sqrt(Math.pow( ( rightEyeXPosition - bottomMouthXPosition), 2) + Math.pow((rightEyeYPosition - bottomMouthYPosition ), 2 ) );

                    int leftEyeMouthDistance = (int) Math.sqrt( Math.pow( ( leftEyeXPosition - bottomMouthXPosition), 2) + Math.pow((leftEyeYPosition - bottomMouthYPosition), 2 ) );

                    int minValue = getMinValue( getMinValue(eyesDistance, rightEyeNoseBaseDistance, leftEyeNoseBaseDistance),
                            getMinValue(noseBaseMouthDistance, rightMouthLeftMouthDistance, rightMouthBottomMouthDistance),
                            getMinValue(leftMouthBottomMouthDistance, rightEyeMouthDistance, leftEyeMouthDistance));

                    faceDetailsAvg.eyesDistanceValues.add( (double) eyesDistance / minValue );
                    faceDetailsAvg.rightEyeNoseBaseDistanceValues.add( (double) rightEyeNoseBaseDistance / minValue );
                    faceDetailsAvg.leftEyeNoseBaseDistanceValues.add( (double) leftEyeNoseBaseDistance / minValue );
                    faceDetailsAvg.noseBaseMouthDistanceValues.add( (double) noseBaseMouthDistance / minValue );
                    faceDetailsAvg.rightMouthLeftMouthDistanceValues.add( (double) rightMouthLeftMouthDistance / minValue );
                    faceDetailsAvg.rightMouthBottomMouthDistanceValues.add( (double) rightMouthBottomMouthDistance / minValue );
                    faceDetailsAvg.leftMouthBottomMouthDistanceValues.add( (double) leftMouthBottomMouthDistance / minValue );
                    faceDetailsAvg.rightEyeMouthDistanceValues.add( (double) rightEyeMouthDistance / minValue );
                    faceDetailsAvg.leftEyeMouthDistanceValues.add( (double) leftEyeMouthDistance / minValue );

                } else {
                    Log.v("Nothing", "Detected");
                }

                index++;
            } else {
                faceDetailsAvg.avg();
                matches = numberOfMatches();
                Log.v("NumberofMatches: ", " " + matches + "");

                if (mEyesDistanceRatio != null && mLeftEyeNoseBaseDistanceRatio != null
                        && mRightEyeNoseBaseDistanceRatio != null && mNoseBaseMouthDistanceRatio != null
                        && mRightMouthBottomMouthDistanceRatio != null && mLeftMouthBottomMouthDistanceRatio != null
                        && mRightMouthLeftMouthDistanceRatio != null && mRightEyeMouthDistanceRatio != null
                        && mLeftEyeMouthDistanceRatio != null ) {

                    if ( matches >= 5 ) {
                        Log.v("FaceAppLocker", "User can be marked as recognized");
                        enableAccess();
                    } else {
                        Log.v("faceapplocker", "user not recognized");
                    }
                    cleanFaceDetailsArray();
                    index = 0;
                    matches = 0;
                }
            }
        }

        private int getMinValue( int firstDistance, int secondDistance, int thirdDistance) {
            return Math.min(Math.min( firstDistance, secondDistance ), thirdDistance );
        }

        private void cleanFaceDetailsArray() {
            faceDetailsAvg.eyesDistanceRatio = 0;
            faceDetailsAvg.rightEyeNoseBaseDistanceRatio = 0;
            faceDetailsAvg.leftEyeNoseBaseDistanceRatio = 0;
            faceDetailsAvg.noseBaseMouthDistanceRatio = 0;
            faceDetailsAvg.rightMouthLeftMouthDistanceRatio = 0;
            faceDetailsAvg.rightMouthBottomMouthDistanceRatio = 0;
            faceDetailsAvg.leftMouthBottomMouthDistanceRatio = 0;
            faceDetailsAvg.rightEyeMouthDistanceRatio = 0;
            faceDetailsAvg.leftEyeMouthDistanceRatio = 0;
            faceDetailsAvg.eyesDistanceValues.clear();
            faceDetailsAvg.rightEyeNoseBaseDistanceValues.clear();
            faceDetailsAvg.leftEyeNoseBaseDistanceValues.clear();
            faceDetailsAvg.noseBaseMouthDistanceValues.clear();
            faceDetailsAvg.rightMouthLeftMouthDistanceValues.clear();
            faceDetailsAvg.rightMouthBottomMouthDistanceValues.clear();
            faceDetailsAvg.leftMouthBottomMouthDistanceValues.clear();
            faceDetailsAvg.rightEyeMouthDistanceValues.clear();
            faceDetailsAvg.leftEyeMouthDistanceValues.clear();
        }


        public double castToDouble(String value) {
            return Double.parseDouble(value);
        }

        public boolean checkIfInRange(double key, double value) {
            Range<Double> matches = new Range(key - 0.08, key + 0.08);
            return matches.contains(value);
        }

        public int numberOfMatches() {
            int matches = 0;
            boolean holder[] = new boolean[9];
            holder[0] = checkIfInRange( castToDouble( mEyesDistanceRatio), castToDouble( String.format( "%.2f", faceDetailsAvg.eyesDistanceRatio ) ) );
            holder[1] = checkIfInRange( castToDouble( mRightEyeNoseBaseDistanceRatio ) , castToDouble( String.format( "%.2f", faceDetailsAvg.rightEyeNoseBaseDistanceRatio  ) ) );
            holder[2] = checkIfInRange( castToDouble( mLeftEyeNoseBaseDistanceRatio ) , castToDouble( String.format( "%.2f", faceDetailsAvg.leftEyeNoseBaseDistanceRatio )  ) );
            holder[3] = checkIfInRange( castToDouble( mNoseBaseMouthDistanceRatio ) , castToDouble( String.format( "%.2f", faceDetailsAvg.noseBaseMouthDistanceRatio )  ) );
            holder[4] = checkIfInRange( castToDouble( mRightMouthLeftMouthDistanceRatio ) , castToDouble( String.format( "%.2f",faceDetailsAvg.rightMouthLeftMouthDistanceRatio )  ) );
            holder[5] = checkIfInRange( castToDouble( mRightMouthBottomMouthDistanceRatio ) , castToDouble( String.format( "%.2f", faceDetailsAvg.rightMouthBottomMouthDistanceRatio ) ) );
            holder[6] = checkIfInRange( castToDouble( mLeftMouthBottomMouthDistanceRatio ) , castToDouble( String.format( "%.2f", faceDetailsAvg.leftMouthBottomMouthDistanceRatio ) ) );
            holder[7] = checkIfInRange( castToDouble( mRightEyeMouthDistanceRatio ) , castToDouble( String.format( "%.2f", faceDetailsAvg.rightEyeMouthDistanceRatio ) ) );
            holder[8] = checkIfInRange( castToDouble( mLeftEyeMouthDistanceRatio ) , castToDouble( String.format( "%.2f", faceDetailsAvg.rightEyeMouthDistanceRatio )  ) );
            for ( int i = 0; i < holder.length; i++ ) {
                if ( holder[i] == true ) {
                    matches++;
                }
            }

            return matches;
        }
    }
}
