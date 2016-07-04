package mariotest.facerecognition;

/**
 * Created by Mario on 6/22/2016.
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.FaceDetector;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class PictureHandler implements PictureCallback {

    private final Context context;

    public PictureHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            Log.d("Error", "Can't create directory to save image.");
            Toast.makeText(context, "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;

        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoFile = "Picture_" + date + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;
        Log.v("Neka to see", filename);

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            Toast.makeText(context, "New Image saved:" + photoFile,
                    Toast.LENGTH_LONG).show();
        } catch (Exception error) {
            Log.d("\"Error\"", "File" + filename + "not saved: "
                    + error.getMessage());
            Toast.makeText(context, "Image could not be saved.",
                    Toast.LENGTH_LONG).show();
        }

        BitmapFactory.Options bitMapFactoryOpts = new BitmapFactory.Options();
        bitMapFactoryOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap image = BitmapFactory.decodeFile(filename, bitMapFactoryOpts);
        int height = image.getHeight();
        int width = image.getWidth();
        FaceDetector faceDetector = new FaceDetector(width, height, 1);
        FaceDetector.Face[] faceInImage = new FaceDetector.Face[1];
        int findFaces = faceDetector.findFaces(image, faceInImage);
        Log.v("width", width + "");
        Log.v("height", height + "");
        Log.v("faces found", findFaces + "");

    }

    private File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "FaceRecognitionfile");
    }
}