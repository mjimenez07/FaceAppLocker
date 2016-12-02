package com.example.developer.facetracker;

import java.util.ArrayList;

public class FaceDetailsProcessor {
    //TODO refact this.
    public ArrayList<Double> eyesDistanceValues = new ArrayList();
    public ArrayList<Double> rightEyeNoseBaseDistanceValues = new ArrayList();
    public ArrayList<Double> leftEyeNoseBaseDistanceValues = new ArrayList();
    public ArrayList<Double> noseBaseMouthDistanceValues = new ArrayList();
    public ArrayList<Double> rightMouthLeftMouthDistanceValues = new ArrayList();
    public ArrayList<Double> rightMouthBottomMouthDistanceValues = new ArrayList();
    public ArrayList<Double> leftMouthBottomMouthDistanceValues = new ArrayList();
    public ArrayList<Double> rightEyeMouthDistanceValues = new ArrayList();
    public ArrayList<Double> leftEyeMouthDistanceValues = new ArrayList();

    public double eyesDistanceRatio = 0;
    public double rightEyeNoseBaseDistanceRatio = 0;
    public double leftEyeNoseBaseDistanceRatio = 0;
    public double noseBaseMouthDistanceRatio = 0;
    public double rightMouthLeftMouthDistanceRatio = 0;
    public double rightMouthBottomMouthDistanceRatio = 0;
    public double leftMouthBottomMouthDistanceRatio = 0;
    public double rightEyeMouthDistanceRatio = 0;
    public double leftEyeMouthDistanceRatio = 0;

    public void avg() {
        double tempEyesRatio = 0;
        for (int index = 0; index < eyesDistanceValues.size(); index++) {
            tempEyesRatio += eyesDistanceValues.get(index);
        }
        eyesDistanceRatio = tempEyesRatio / eyesDistanceValues.size();


        double tempRightEyeNoseBaseDistanceRatio = 0;
        for (int index = 0; index < rightEyeNoseBaseDistanceValues.size(); index++) {
            tempRightEyeNoseBaseDistanceRatio += rightEyeNoseBaseDistanceValues.get(index);
        }
        rightEyeNoseBaseDistanceRatio = tempRightEyeNoseBaseDistanceRatio / rightEyeNoseBaseDistanceValues.size();

        double tempLeftEyeNoseBaseDistanceRatio = 0;
        for (int index = 0; index < leftEyeNoseBaseDistanceValues.size(); index++) {
            tempLeftEyeNoseBaseDistanceRatio += leftEyeNoseBaseDistanceValues.get(index);
        }
        leftEyeNoseBaseDistanceRatio = tempLeftEyeNoseBaseDistanceRatio / leftEyeNoseBaseDistanceValues.size();

        double tempNoseBaseMouthDistanceRatio = 0;
        for (int index = 0; index < noseBaseMouthDistanceValues.size(); index++) {
            tempNoseBaseMouthDistanceRatio += noseBaseMouthDistanceValues.get(index);
        }
        noseBaseMouthDistanceRatio = tempNoseBaseMouthDistanceRatio / noseBaseMouthDistanceValues.size();

        double tempRightMouthLeftMouthDistanceRatio = 0;
        for (int index = 0; index < rightMouthLeftMouthDistanceValues.size(); index++) {
            tempRightMouthLeftMouthDistanceRatio += rightMouthLeftMouthDistanceValues.get(index);
        }
        rightMouthLeftMouthDistanceRatio = tempRightMouthLeftMouthDistanceRatio / rightMouthLeftMouthDistanceValues.size();

        double tempRightMouthBottomMouthDistanceValues = 0;
        for (int index = 0; index < rightMouthBottomMouthDistanceValues.size(); index++) {
            tempRightMouthBottomMouthDistanceValues += rightMouthBottomMouthDistanceValues.get(index);
        }
        rightMouthBottomMouthDistanceRatio = tempRightMouthBottomMouthDistanceValues / rightMouthBottomMouthDistanceValues.size();

        double tempLeftMouthBottomMouthDistanceValues = 0;
        for (int index = 0; index < leftMouthBottomMouthDistanceValues.size(); index++) {
            tempLeftMouthBottomMouthDistanceValues += leftMouthBottomMouthDistanceValues.get(index);
        }
        leftMouthBottomMouthDistanceRatio = tempLeftMouthBottomMouthDistanceValues / leftMouthBottomMouthDistanceValues.size();

        double tempRightEyeMouthDistanceRatio = 0;
        for (int index = 0; index < rightEyeMouthDistanceValues.size(); index++) {
            tempRightEyeMouthDistanceRatio += rightEyeMouthDistanceValues.get(index);
        }
        rightEyeMouthDistanceRatio = tempRightEyeMouthDistanceRatio / rightEyeMouthDistanceValues.size();

        double tempLeftEyeMouthDistanceRatio = 0;
        for (int index = 0; index < leftEyeMouthDistanceValues.size(); index++) {
            tempLeftEyeMouthDistanceRatio += leftEyeMouthDistanceValues.get(index);
        }
        leftEyeMouthDistanceRatio = tempLeftEyeMouthDistanceRatio / leftEyeMouthDistanceValues.size();
    }
}
