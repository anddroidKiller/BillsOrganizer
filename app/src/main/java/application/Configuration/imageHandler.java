package application.Configuration;

/**
 * Created by NivSwisa on 08/09/2016.
 */
import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;

public class imageHandler implements PictureCallback {




    private final Context activity;

    public imageHandler(Context context) {
        this.activity = context;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);

        SaveImage(picture);


            /////////////////opencvvvvvvvvvvvvvvvvv
    }

    private File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "CameraAPIDemo");
    }

    private void SaveImage(Bitmap finalBitmap) {

    }
}