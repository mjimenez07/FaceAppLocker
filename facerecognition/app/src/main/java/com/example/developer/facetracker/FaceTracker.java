package com.example.developer.facetracker;

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

/**
 * Created by Mario on 10/4/2016.
 */
public class FaceTracker extends Tracker<Face> {
    private GraphicOverlay mOverlay;
    private FaceGraphic mFaceGraphic;

    // Record the previously seen proportions of the landmark locations relative to the bounding box
    // of the face.  These proportions can be used to approximate where the landmarks are within the
    // face bounding box if the eye landmark is missing in a future update.
    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();

    FaceTracker(GraphicOverlay overlay) {
        mOverlay = overlay;
        mFaceGraphic = new FaceGraphic(overlay);
    }

    /**
     * Start tracking the detected face instance within the face overlay.
     */
    @Override
    public void onNewItem(int faceId, Face face) {
        mFaceGraphic = new FaceGraphic(mOverlay);
    }

    /**
     * Update the position/characteristics of the face within the overlay.
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        mOverlay.add(mFaceGraphic);

        updatePreviousProportions(face);
        PointF leftEyePosition = getLandmarkPosition(face, Landmark.LEFT_EYE);
        PointF rightEyePosition = getLandmarkPosition(face, Landmark.RIGHT_EYE);
        PointF bottomMouthPosition = getLandmarkPosition(face, Landmark.BOTTOM_MOUTH);


        mFaceGraphic.updateFace(face);
        landMarkProcessor(leftEyePosition, rightEyePosition, bottomMouthPosition);

    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily (e.g., if the face was momentarily blocked from
     * view).
     */
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        mOverlay.remove(mFaceGraphic);
    }

    /**
     * Called when the face is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mFaceGraphic);
    }


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
     * @param face face objects that we will use to take the landmarks
     * @param landMarkID int value of the landmark needed
     *  return landmark position
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


    private void landMarkProcessor(PointF leftEyePosition, PointF rightEyePosition, PointF bottomMouthPosition) {
        double leftEyeXposition = (double) leftEyePosition.x * mFaceGraphic.scale;
        double leftEyeYposition = (double) leftEyePosition.y * mFaceGraphic.scale;
        double rightEyeXposition = (double) rightEyePosition.x * mFaceGraphic.scale;
        double rightEyeYposition = (double) rightEyePosition.y * mFaceGraphic.scale;
        double bottomMouthXposition = (double) bottomMouthPosition.x * mFaceGraphic.scale;
        double bottomMouthYposition = (double) bottomMouthPosition.y * mFaceGraphic.scale;

        //Here we calculate the distance of each point
        int eyesDistance = (int) Math.sqrt( Math.pow((leftEyeXposition - rightEyeXposition),2) + Math.pow((leftEyeYposition - rightEyeYposition),2));
        int rightEyeMouseDistance = (int) Math.sqrt( Math.pow((rightEyeXposition - bottomMouthXposition),2) + Math.pow((rightEyeYposition - bottomMouthYposition),2));
        int leftEyeMouseDistance = (int) Math.sqrt( Math.pow((leftEyeXposition - bottomMouthXposition),2) + Math.pow((leftEyeYposition - bottomMouthYposition),2));

        //This is a way to calculate the ratio of each distance we divide each distance by the lower distance
        int minValue = Math.min(Math.min(eyesDistance,rightEyeMouseDistance),leftEyeMouseDistance);
        double eyesRatio = (double) eyesDistance / minValue;
        double rightEyeMouthRatio = (double) rightEyeMouseDistance  / minValue;
        double leftEyeMouthRatio = (double) leftEyeMouseDistance / minValue;
//
//        //Printing results
        Log.v("Eyes distance: ", eyesDistance + "");
        Log.v("R.eye-Mouth distance ", rightEyeMouseDistance + "");
        Log.v("L.Eye-Mouth distance ", leftEyeMouseDistance + "");
        Log.v("min value", minValue + "");
        Log.v("Eyes distance Ratio", eyesRatio + "");
        Log.v("Right eye mouth ratio", rightEyeMouthRatio + "");
        Log.v("Left eye mouth ratio", leftEyeMouthRatio + "");

    }

}
