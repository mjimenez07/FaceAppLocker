package com.example.developer.facetracker;

import android.app.Activity;
import android.content.Context;

import android.content.SharedPreferences;
import android.graphics.PointF;
import android.util.Log;
import com.example.developer.facetracker.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import java.util.HashMap;
import java.util.Map;


public class FaceTracker extends Tracker<Face> {
    private GraphicOverlay mOverlay;
    private FaceGraphic mFaceGraphic;
    private Context activityContext;
    public int index = 0;
    private Activity mActivity;


    /* class created to handle the distance mapping in order to
    return an avg of the distances tracked
     */

    FaceDetailsProccesor faceDetailsAvg;

    // Record the previously seen proportions of the landmark locations relative to the bounding box
    // of the face.  These proportions can be used to approximate where the landmarks are within the
    // face bounding box if the eye landmark is missing in a future update.
    private Map< Integer, PointF > mPreviousProportions = new HashMap<>();

    FaceTracker( GraphicOverlay overlay, Activity activity, Context context ) {
        mOverlay = overlay;
        mFaceGraphic = new FaceGraphic( overlay );
        faceDetailsAvg = new FaceDetailsProccesor();
        mActivity = activity;
        activityContext = context;
    }

    /**
     * Start tracking the detected face instance within the face overlay.
     */
    @Override
    public void onNewItem( int faceId, Face face ) {
        super.onNewItem( faceId, face );
        mFaceGraphic = new FaceGraphic( mOverlay );
    }

    /**
     * Update the position/characteristics of the face within the overlay.
     */
    @Override
    public void onUpdate( FaceDetector.Detections<Face> detections, Face face ) {
        super.onUpdate( detections, face );
        mOverlay.add( mFaceGraphic );

        updatePreviousProportions( face );

        PointF leftEyePosition = getLandmarkPosition( face, Landmark.LEFT_EYE );
        PointF rightEyePosition = getLandmarkPosition( face, Landmark.RIGHT_EYE );
        PointF noseBasePosition = getLandmarkPosition( face, Landmark.NOSE_BASE );
        PointF rightMouthPosition = getLandmarkPosition( face, Landmark.RIGHT_MOUTH );
        PointF leftMouthPosition = getLandmarkPosition( face, Landmark.LEFT_MOUTH );
        PointF bottomMouthPosition = getLandmarkPosition( face, Landmark.BOTTOM_MOUTH );


        landMarkProcessor( leftEyePosition, rightEyePosition, noseBasePosition, leftMouthPosition, rightMouthPosition, bottomMouthPosition );

        mFaceGraphic.updateFace( face );

    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily (e.g., if the face was momentarily blocked from
     * view).
     */
    @Override
    public void onMissing( FaceDetector.Detections<Face> detections ) {
        super.onMissing( detections );
        mOverlay.remove( mFaceGraphic );
    }

    /**
     * Called when the face is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
        super.onDone();
        mOverlay.remove( mFaceGraphic );
    }

    /*
    * function updatePreviousProportions
    * here we have a mapping of the landmarks previous positions
    * to avoid 0 value registration if the face it's removed
    * from the camera preview overlay
     * @param face
    * */
    private void updatePreviousProportions( Face face ) {
        for ( Landmark landmark : face.getLandmarks() ) {
            PointF position = landmark.getPosition();
            float xProp = ( position.x - face.getPosition().x ) / face.getWidth();
            float yProp = ( position.y - face.getPosition().y ) / face.getHeight();
            mPreviousProportions.put( landmark.getType(), new PointF( xProp, yProp ) );
        }
    }

    /**
     * function getLandmarkPosition
     *
     * @param face       face objects that we will use to take the landmarks
     * @param landMarkID int value of the landmark needed
     *                   return landmark position
     **/
    private PointF getLandmarkPosition( Face face, int landMarkID ) {
        for ( Landmark landmark : face.getLandmarks() ) {
            if ( landmark.getType() == landMarkID ) {
                return landmark.getPosition();
            }
        }

        PointF prop = mPreviousProportions.get( landMarkID );
        if ( prop == null ) {
            return null;
        }

        float x = face.getPosition().x + ( prop.x * face.getWidth() );
        float y = face.getPosition().y + ( prop.y * face.getHeight() );
        return new PointF( x, y );
    }

    /**
     * Function landMarkProcessor
     * @param  leftEyePosition
     * @param  rightEyePosition
     * @param  bottomMouthPosition
     * this will calculate the distance of each point and save it in the sharedpreference
     * after that will launch the next activity
     * */
    private void landMarkProcessor(PointF leftEyePosition, PointF rightEyePosition, PointF noseBasePosition, PointF leftMouthPosition, PointF rightMouthPosition, PointF bottomMouthPosition) {
        //Here we calculate the distance of each point

        if (index < 20) {
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

                int noseBaseMouthDistance = (int) Math.sqrt( Math.pow( ( noseBaseXPosition - bottomMouthXPosition ), 2 ) + Math.pow( noseBaseYPosition - bottomMouthYPosition ,2 ) );

                int rightMouthLeftMouthDistance = (int) Math.sqrt( Math.pow( ( rightMouthXPosition - leftMouthXPosition ) ,2 ) + Math.pow( ( rightMouthYPosition - leftMouthYPosition ),2 ) );

                int rightMouthBottomMouthDistance = (int) Math.sqrt( Math.pow( ( rightMouthXPosition - bottomMouthXPosition ),2 )  + Math.pow( ( rightMouthYPosition - bottomMouthYPosition ) ,2)  );

                int leftMouthBottomMouthDistance = (int) Math.sqrt( Math.pow( ( leftMouthXPosition - bottomMouthXPosition ),2 ) + Math.pow( ( leftMouthYPosition - bottomMouthYPosition ),2 ) );

                int rightEyeMouthDistance = (int) Math.sqrt(Math.pow( ( rightEyeXPosition - bottomMouthXPosition), 2) + Math.pow((rightEyeYPosition - bottomMouthYPosition ), 2 ) );

                int leftEyeMoutheDistance = (int) Math.sqrt( Math.pow( ( leftEyeXPosition - bottomMouthXPosition), 2) + Math.pow((leftEyeYPosition - bottomMouthYPosition), 2 ) );

                int minValue = getMinValue( eyesDistance, rightEyeNoseBaseDistance, leftEyeNoseBaseDistance, noseBaseMouthDistance, rightMouthLeftMouthDistance, rightMouthBottomMouthDistance, leftMouthBottomMouthDistance);

                faceDetailsAvg.eyesRatios.add( (double) eyesDistance / minValue );
                faceDetailsAvg.rightEyeMouthRatios.add( (double) rightEyeMouthDistance / minValue );
                faceDetailsAvg.leftEyeMouthRatios.add( (double) leftEyeMoutheDistance / minValue );

                faceDetailsAvg.avg();

            } else {
                Log.v("Nothing", "Detected");
            }

            index++;
        } else {
            Log.v("Face information: ", "Saved");
            Log.v("Eyes_distance_ratio", String.format("%.2f", faceDetailsAvg.eyesRatio) );
            Log.v("Left_eye_mouth_ratio", String.format("%.2f", faceDetailsAvg.leftEyeMouthRatio));
            Log.v("Right_eye_mouth_ratio", String.format("%.2f", faceDetailsAvg.rightEyeMouthRatio));
            faceDetailsAvg.eyesRatio = 0;
            faceDetailsAvg.leftEyeMouthRatio = 0;
            faceDetailsAvg.rightEyeMouthRatio = 0;
            faceDetailsAvg.eyesRatios.clear();
            faceDetailsAvg.leftEyeMouthRatios.clear();
            faceDetailsAvg.rightEyeMouthRatios.clear();
            index = 0;
//            SharedPreferences.Editor editor = getEditor(activityContext);
//            editor.putString("Eyes_distance_ratio", String.format("%.2f", faceDetailsAvg.eyesRatio));
//            editor.putString("Left_eye_bottom_mouth_ratio", String.format("%.2f", faceDetailsAvg.leftEyeMouthRatio));
//            editor.putString("Right_eye_bottom_mouth_ratio", String.format("%.2f", faceDetailsAvg.rightEyeMouthRatio));
//
//            if (editor.commit()) {
//                Log.v("Face information: ", "Saved");
//                Log.v("Eyes_distance_ratio", String.format("%.2f", faceDetailsAvg.eyesRatio) );
//                Log.v("Left_eye_mouth_ratio", String.format("%.2f", faceDetailsAvg.leftEyeMouthRatio));
//                Log.v("Right_eye_mouth_ratio", String.format("%.2f", faceDetailsAvg.rightEyeMouthRatio));
//                faceDetailsAvg.eyesRatios.clear();
//                faceDetailsAvg.leftEyeMouthRatios.clear();
//                faceDetailsAvg.rightEyeMouthRatios.clear();
//                index = 0;
//                Intent intent = new Intent(activityContext, ListApplicationActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                activityContext.startActivity(intent);
//                mActivity.finish();
//
//                //Todo release the camera and the preview before closing this activity.
//            }
        }
    }


    private int getMinValue( int eyesDistance, int rightEyeMouthDistance, int leftEyeNoseBaseDistance, int noseBaseMouthDistance,  int rightMouthLeftMouthDistance, int rightMouthBottomMouthDistance, int leftMouthBottomMouthDistance ) {
        return Math.min(Math.min( eyesDistance, rightEyeMouthDistance ), leftEyeMoutheDistance );
    }
    //return sharedPrefence instance
    public SharedPreferences getSharedPrerence(Context context) {
        SharedPreferences shrdprefences = context.getSharedPreferences("FaceInfo", Context.MODE_PRIVATE);
        return shrdprefences;
    }

    //return sharedPreference Editor
    public SharedPreferences.Editor getEditor(Context context) {
        return getSharedPrerence(context).edit();
    }
}
