package com.example.developer.facetracker;

import java.util.ArrayList;

public class FaceDetailsProcessorAvg {

    public ArrayList<Double> eyesDistanceRatioValues = new ArrayList();
    public ArrayList<Double> rightEyeNoseBaseDistanceRatioValues = new ArrayList();
    public ArrayList<Double> leftEyeNoseBaseDistanceRatioValues = new ArrayList();
    public ArrayList<Double> noseBaseMouthDistanceRatioValues = new ArrayList();
    public ArrayList<Double> rightMouthLeftMouthDistanceRatioValues = new ArrayList();
    public ArrayList<Double> rightMouthBottomMouthDistanceRatioValues = new ArrayList();
    public ArrayList<Double> leftMouthBottomMouthDistanceRatioValues = new ArrayList();
    public ArrayList<Double> rightEyeMouthDistanceRatioValues = new ArrayList();
    public ArrayList<Double> leftEyeMouthDistanceRatioValues = new ArrayList();

    public double eyesDistanceRatioApproximate = 0;
    public double rightEyeNoseBaseDistanceRatioApproximate = 0;
    public double leftEyeNoseBaseDistanceRatioApproximate = 0;
    public double noseBaseMouthDistanceRatioApproximate = 0;
    public double rightMouthLeftMouthDistanceRatioApproximate = 0;
    public double rightMouthBottomMouthDistanceRatioApproximate = 0;
    public double leftMouthBottomMouthDistanceRatioApproximate = 0;
    public double rightEyeMouthDistanceRatioApproximate = 0;
    public double leftEyeMouthDistanceRatioApproximate = 0;

    public void approximate() {

        double tempEyesRatioValues = 0;
        for (int index = 0; index < eyesDistanceRatioValues.size(); index++) {
            tempEyesRatioValues += eyesDistanceRatioValues.get(index);
        }
        eyesDistanceRatioApproximate = tempEyesRatioValues / eyesDistanceRatioValues.size();

        double tempRightEyeNoseBaseRatioValues = 0;
        for (int index = 0; index < rightEyeNoseBaseDistanceRatioValues.size(); index++) {
            tempRightEyeNoseBaseRatioValues += rightEyeNoseBaseDistanceRatioValues.get(index);
        }
        rightEyeNoseBaseDistanceRatioApproximate = tempRightEyeNoseBaseRatioValues / rightEyeNoseBaseDistanceRatioValues.size();

        double tempLeftEyeNoseBaseRatioValues = 0;
        for (int index = 0; index < leftEyeNoseBaseDistanceRatioValues.size(); index++) {
            tempLeftEyeNoseBaseRatioValues += leftEyeNoseBaseDistanceRatioValues.get(index);
        }
        leftEyeNoseBaseDistanceRatioApproximate = tempLeftEyeNoseBaseRatioValues / leftEyeNoseBaseDistanceRatioValues.size();

        double tempNoseBaseMouthDistanceRatioValues = 0;
        for (int index = 0; index < noseBaseMouthDistanceRatioValues.size(); index++) {
            tempNoseBaseMouthDistanceRatioValues += noseBaseMouthDistanceRatioValues.get(index);
        }
        noseBaseMouthDistanceRatioApproximate = tempNoseBaseMouthDistanceRatioValues / noseBaseMouthDistanceRatioValues.size();

        double tempRightMouthLeftMouthDistanceRatioValues = 0;
        for (int index = 0; index < rightMouthLeftMouthDistanceRatioValues.size(); index++) {
            tempRightMouthLeftMouthDistanceRatioValues += rightMouthLeftMouthDistanceRatioValues.get(index);
        }
        rightMouthLeftMouthDistanceRatioApproximate = tempRightMouthLeftMouthDistanceRatioValues / rightMouthLeftMouthDistanceRatioValues.size();

        double tempRightMouthBottomMouthDistanceRatioValues = 0;
        for (int index = 0; index < rightMouthBottomMouthDistanceRatioValues.size(); index++) {
            tempRightMouthBottomMouthDistanceRatioValues += rightMouthBottomMouthDistanceRatioValues.get(index);
        }
        rightMouthBottomMouthDistanceRatioApproximate = tempRightMouthBottomMouthDistanceRatioValues / rightMouthBottomMouthDistanceRatioValues.size();

        double tempLeftMouthBottomMouthDistanceRatioValues = 0;
        for (int index = 0; index < leftMouthBottomMouthDistanceRatioValues.size(); index++) {
            tempLeftMouthBottomMouthDistanceRatioValues += leftMouthBottomMouthDistanceRatioValues.get(index);
        }
        leftMouthBottomMouthDistanceRatioApproximate = tempLeftMouthBottomMouthDistanceRatioValues / leftMouthBottomMouthDistanceRatioValues.size();

        double tempRightEyeMouthDistanceRatioValues = 0;
        for (int index = 0; index < rightEyeMouthDistanceRatioValues.size(); index++) {
            tempRightEyeMouthDistanceRatioValues += rightEyeMouthDistanceRatioValues.get(index);
        }
        rightEyeMouthDistanceRatioApproximate = tempRightEyeMouthDistanceRatioValues / rightEyeMouthDistanceRatioValues.size();

        double tempLeftEyeMouthDistanceRatioValues = 0;
        for (int index = 0; index < leftEyeMouthDistanceRatioValues.size(); index++) {
            tempLeftEyeMouthDistanceRatioValues += leftEyeMouthDistanceRatioValues.get(index);
        }
        leftEyeMouthDistanceRatioApproximate = tempLeftEyeMouthDistanceRatioValues / leftEyeMouthDistanceRatioValues.size();
    }
}
