package com.example.developer.facetracker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.example.developer.facetracker.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.HashMap;
import java.util.Map;


public class FaceGraphic  extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    //Force!
    public float left;
    public float right;
    public float top;
    public float bottom;
    public float xOffset;
    public float yOffset;
    public double canvasHeight;
    public double canvasWidth;
    public double faceWidth;
    public double faceHeight;
    public double scale;

    public Map<String, Float> canvasDimensions = new HashMap<>();

    private static final int COLOR_CHOICES = Color.WHITE;
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    public Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        final int selectedColor = COLOR_CHOICES;;

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {

        Face face = mFace;
        if (face == null) {
            return;
        }
        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float  y = translateY(face.getPosition().y + face.getHeight() / 2);

        // Draws a bounding box around the face.
        xOffset = scaleX(face.getWidth() / 2.0f);
        yOffset = scaleY(face.getHeight() / 2.0f);
        left = x - xOffset;
        top = y - yOffset;
        right = x + xOffset;
        bottom = y + yOffset;
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        faceWidth = face.getWidth();
        faceHeight = face.getHeight();
        scale = Math.min(canvasWidth / faceWidth, canvasHeight / faceHeight);
        canvas.drawRect(left, top, right, bottom, mBoxPaint);

    }
}
