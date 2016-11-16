package com.example.developer.facetracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.util.Log;
import com.example.developer.facetracker.ui.camera.CameraSourcePreview;
import com.example.developer.facetracker.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FaceTracker extends Tracker<Face> {
    private GraphicOverlay mOverlay;
    private FaceGraphic mFaceGraphic;
    private Context activityContext;
    public int index = 0;
    private Activity mActivity;

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

    static FaceDetailsAvg faceDetailsAvg;

    // Record the previously seen proportions of the landmark locations relative to the bounding box
    // of the face.  These proportions can be used to approximate where the landmarks are within the
    // face bounding box if the eye landmark is missing in a future update.
    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();

    FaceTracker(GraphicOverlay overlay, Activity activity, Context context) {
        mOverlay = overlay;
        mFaceGraphic = new FaceGraphic(overlay);
        faceDetailsAvg = new FaceDetailsAvg();
        mActivity = activity;
        activityContext = context;
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


        landMarkProcessor(leftEyePosition, rightEyePosition, bottomMouthPosition);
        mFaceGraphic.updateFace(face);

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


    private void landMarkProcessor(PointF leftEyePosition, PointF rightEyePosition, PointF bottomMouthPosition) {
        //Here we calculate the distance of each point

        if (index < 20) {
            double leftEyeXposition = (double) leftEyePosition.x * mFaceGraphic.scale;
            double leftEyeYposition = (double) leftEyePosition.y * mFaceGraphic.scale;
            double rightEyeXposition = (double) rightEyePosition.x * mFaceGraphic.scale;
            double rightEyeYposition = (double) rightEyePosition.y * mFaceGraphic.scale;
            double bottomMouthXposition = (double) bottomMouthPosition.x * mFaceGraphic.scale;
            double bottomMouthYposition = (double) bottomMouthPosition.y * mFaceGraphic.scale;

            if ((leftEyeXposition != 0) && (leftEyeYposition != 0) && (rightEyeXposition != 0) && (rightEyeYposition != 0) && (bottomMouthXposition != 0) && (bottomMouthYposition != 0)) {
                int eyesDistance = (int) Math.sqrt(Math.pow((leftEyeXposition - rightEyeXposition), 2) + Math.pow((leftEyeYposition - rightEyeYposition), 2));
                int rightEyeMouseDistance = (int) Math.sqrt(Math.pow((rightEyeXposition - bottomMouthXposition), 2) + Math.pow((rightEyeYposition - bottomMouthYposition), 2));
                int leftEyeMouseDistance = (int) Math.sqrt(Math.pow((leftEyeXposition - bottomMouthXposition), 2) + Math.pow((leftEyeYposition - bottomMouthYposition), 2));
                int minValue = Math.min(Math.min(eyesDistance, rightEyeMouseDistance), leftEyeMouseDistance);

                faceDetailsAvg.eyesRatios.add((double) eyesDistance / minValue);
                faceDetailsAvg.rightEyeMouthRatios.add((double) rightEyeMouseDistance / minValue);
                faceDetailsAvg.leftEyeMouthRatios.add((double) leftEyeMouseDistance / minValue);

                faceDetailsAvg.avg();
            } else {
                Log.v("Nothing", "Detected");
            }

            index++;
        } else {
            SharedPreferences.Editor editor = getEditor(activityContext);
            editor.putFloat("Eyes_distance_ratio", (float) faceDetailsAvg.eyesRatio);
            editor.putFloat("Left_eye_bottom_mouth_ratio", (float) faceDetailsAvg.leftEyeMouthRatio);
            editor.putFloat("Right_eye_bottom_mouth_ratio", (float) faceDetailsAvg.rightEyeMouthRatio);

            if (editor.commit()) {
                Log.v("Face information: ", "Saved");
                Log.v("Eyes_ratio", faceDetailsAvg.eyesRatio + "");
                Log.v("Left_eye_mouth_ratio", faceDetailsAvg.leftEyeMouthRatio + "");
                Log.v("Right_eye_mouth_ratio", faceDetailsAvg.rightEyeMouthRatio + "");
                faceDetailsAvg.eyesRatios.clear();
                faceDetailsAvg.leftEyeMouthRatios.clear();
                faceDetailsAvg.rightEyeMouthRatios.clear();
                index = 0;
                Intent intent = new Intent(activityContext, ListApplicationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activityContext.startActivity(intent);
                mActivity.finish();

                //Todo release the camera and the preview before closing this activity.
            }
        }
    }

    public SharedPreferences getSharedPrerence(Context context) {
        SharedPreferences shrdprefences = context.getSharedPreferences("FaceInfo", Context.MODE_PRIVATE);
        return shrdprefences;
    }

    public SharedPreferences.Editor getEditor(Context context) {
        return getSharedPrerence(context).edit();
    }
}
