package com.example.developer.facetracker;

import java.util.ArrayList;

public class FaceDetailsProccesor {
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
