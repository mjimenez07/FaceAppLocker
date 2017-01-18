package com.example.developer.facetracker.utility;

public class Constants {

    public static boolean IS_RUNNING = true;

    /**
     * The amount of times that we are going to be filling the
     * faceDetailsAvg arrays with each landmark position to determinate the
     * average*/
    public static int NUMBER_OF_ITERATIONS = 10;

    /**
     * Accuracy level to determinate if the user can be marked as recognized
     * current level is 6, if you want it to be more strict feel free to change it
     * from 7 - 9, currently with 6 it's working perfectly
     * */
    public static int ACCURACY_LEVEL = 6;

    public static int MAX_RECOGNITION_INTENTS = 5;

}
