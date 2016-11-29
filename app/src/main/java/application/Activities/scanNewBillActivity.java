package application.Activities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import billsorganizer.billsorganizer.R;

public class scanNewBillActivity extends AppCompatActivity {

static int openCvReady = 0;

    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this){
        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case BaseLoaderCallback.SUCCESS:{
                   // javaCameraView.enableView();
                    openCvReady = 1;
                    break;
                }
                default:{
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    Context context = this.getBaseContext();
    String mCurrentPhotoPath,errorMsg;
    ImageView ivImage;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Uri fileUri;
    final int LOAD_PHOTO = 0, CLICK_PHOTO = 1;
    Button bClickImage, bLoadImage;
    Mat srcOrig,src;
    static int scaleFactor = 0;

    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_img);

        ivImage = (ImageView)findViewById(R.id.ivImage);
        bClickImage = (Button)findViewById(R.id.bClickImage);
        bLoadImage = (Button)findViewById(R.id.bLoadImage);

        bLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                errorMsg = null;
                startActivityForResult(intent, LOAD_PHOTO);


            }
        });

        bClickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OutputStream output;

                boolean i = hasPermissionInManifest(scanNewBillActivity.this,"ACTION_IMAGE_CAPTURE");


                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                errorMsg = null;
                File filePath = Environment.getExternalStorageDirectory();
                File imagesFolder = new File(filePath.getAbsolutePath() + "/SavedImages");
                imagesFolder.mkdirs();
                File image = new File(imagesFolder, "image_10.jpg");
                fileUri = Uri.fromFile(image);


                Log.d("LensActivity", "File URI = " + fileUri.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                // start the image capture Intent
                startActivityForResult(intent, CLICK_PHOTO);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch(requestCode) {
            case LOAD_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        InputStream stream = getContentResolver().
                                openInputStream(
                                        data.getData());
                        final Bitmap selectedImage =
                                BitmapFactory.decodeStream(stream);
                        stream.close();
                        ivImage.setImageBitmap(selectedImage);
                        srcOrig = new Mat(selectedImage.
                                getHeight(), selectedImage.
                                getWidth(), CvType.CV_8UC4);

                 //   Mat mImgTranspose = new Mat(i1, i, CvType.CV_8UC4);
                 //    Core.transpose(mRgbaMat,mImgTranspose);
                 //    Imgproc.resize(mImgTranspose, mImgF, mImgF.size(), 0,0, 0);
                 //    Core.flip(mImgF, mRgbaMat, 1 );

                        Imgproc.cvtColor(srcOrig, srcOrig,
                                Imgproc.COLOR_BGR2RGB);
                        Utils.bitmapToMat(selectedImage, srcOrig);
                        scaleFactor = calcScaleFactor(
                                srcOrig.rows(), srcOrig.cols());

                        src = new Mat();
                        Imgproc.resize(srcOrig, src, new
                                Size(srcOrig.rows() / scaleFactor,
                                srcOrig.cols() / scaleFactor));
                        Imgproc.GaussianBlur(src, src,
                                new Size(5, 5), 1);
                        getpage();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CLICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Log.d("LensActivity", fileUri.toString());
                        final InputStream imageStream =
                                getContentResolver().openInputStream(fileUri);
                        if (openCvReady == 1) {
                            final Bitmap selectedImage =
                                    BitmapFactory.decodeStream(imageStream);


                            // convert to bitmap:
                            //  Bitmap bm = Bitmap.createBitmap(m.cols(), m.rows(),Bitmap.Config.ARGB_8888);
                            //  Utils.matToBitmap(m, bm);

                            //   find the imageview and draw it!
                            ImageView iv = (ImageView) findViewById(R.id.ivImage);

                            iv.setImageBitmap(selectedImage);

                            //step2.setImageBitmap(selectedImage);
                           // step3.setImageBitmap(selectedImage);
                           // step4.setImageBitmap(selectedImage);

                            // MediaStore.Images.Media.insertImage(getContentResolver(), yourBitmap, yourTitle, yourDescription);


                            //Drawable myDrawable = getResources().getDrawable(R.drawable.paper);

                            //  final Bitmap selectedImage = ((BitmapDrawable) myDrawable).getBitmap();


                            srcOrig = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC1);
                            Utils.bitmapToMat(selectedImage, srcOrig);



                            src = new Mat();

                            scaleFactor = calcScaleFactor(
                                    srcOrig.rows(), srcOrig.cols());

                            Imgproc.resize(srcOrig, src, new
                                    Size(srcOrig.rows() / scaleFactor,
                                    srcOrig.cols() / scaleFactor));
                          getpage();

                        }
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    void getpage() {

        ImageView step1 = (ImageView) findViewById(R.id.step1);
        ImageView step2 = (ImageView) findViewById(R.id.step2);
        ImageView step3 = (ImageView) findViewById(R.id.step3);
        ImageView step4 = (ImageView) findViewById(R.id.step4);
        ImageView step5 = (ImageView) findViewById(R.id.step5);
        ImageView step6 = (ImageView) findViewById(R.id.step6);
        ImageView step7 = (ImageView) findViewById(R.id.step7);

        /*
        First of all, we will make our image in the desired form to perform a k-means
        clustering with two clusters. The intuition behind applying k-means is that the
        background and foreground will be quite distinct from the background and most of
        the area will be occupied by the page:
         */

        Mat samples = new Mat(src.rows() * src.cols(), 3, CvType.CV_32F);
        for (int y = 0; y < src.rows(); y++) {
            for (int x = 0; x < src.cols(); x++) {
                for (int z = 0; z < 3; z++) {
                    samples.put(x + y * src.cols(), z, src.get(y, x)[z]);
                }
            }
        }


        //Then, we will apply the k-means algorithm as follows:
        int clusterCount = 2;
        Mat labels = new Mat();
        int attempts = 5;
        Mat centers = new Mat();
        Core.kmeans(samples, clusterCount, labels, new
                        TermCriteria(TermCriteria.MAX_ITER |
                        TermCriteria.EPS, 10000, 0.0001), attempts,
                Core.KMEANS_PP_CENTERS, centers);

        /*
        Now, we have the two cluster centers and the labels for each pixel in the original
        image. We will use the two cluster centers to detect which one corresponds to the
        paper. For this, we will find the Euclidian distance between the color of both the
        centers and the color pure white. The one which is closer to the color pure white will
        be considered as the foreground:
         */

        double dstCenter0 = calcWhiteDist(centers.get(0,
                0)[0], centers.get(0, 1)[0], centers.get(0, 2)[0]);
        double dstCenter1 = calcWhiteDist(centers.get(1,
                0)[0], centers.get(1, 1)[0], centers.get(1, 2)[0]);
        int paperCluster = (dstCenter0 < dstCenter1) ? 0 : 1;

        //We need to define two Mat objects that we will use in the next step:

        Mat srcRes = new Mat(src.size(), src.type());
        Mat srcGray = new Mat();

        /*
        Now, we will perform a segmentation where we will display all the foreground
        pixels as white and all the background pixels as black:
         */

        for (int y = 0; y < src.rows(); y++) {
            for (int x = 0; x < src.cols(); x++) {
                int cluster_idx = (int) labels.get(x + y * src.cols(), 0)[0];
                if (cluster_idx != paperCluster) {
                    srcRes.put(y, x, 0, 0, 0, 255);
                } else {
                    srcRes.put(y, x, 255, 255, 255, 255);
                }
            }
        }

        // we present the result
        Bitmap bit1 = Bitmap.createBitmap(srcRes.cols(), srcRes.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(srcRes, bit1);
        step1.setImageBitmap(bit1);

        /*
        Now, we will move on to the next step; that is, detecting contours in this image.
        First, we will apply the Canny edge detector to detect just the edges and then
        apply a contouring algorithm:
        */

        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);

        // we present the result - grayscale
        Bitmap bit2 = Bitmap.createBitmap(srcGray.cols(), srcGray.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(srcGray, bit2);
        step2.setImageBitmap(bit2);

        Imgproc.Canny(srcGray, srcGray, 50, 150);

        // we present the result - canny
        Bitmap bit3 = Bitmap.createBitmap(srcGray.cols(), srcGray.rows(),Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(srcGray, bit3);
        step3.setImageBitmap(bit3);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(srcGray, contours, hierarchy,
                Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        /*
        We now make an assumption that the page occupies the biggest part of the
        foreground and so it corresponds to the biggest contour we find:
        */

        int index = 0;
        double maxim = Imgproc.contourArea(contours.get(0));
        for (int contourIdx = 1; contourIdx < contours.size();
             contourIdx++) {
            double temp;
            temp=Imgproc.contourArea(contours.get(contourIdx));
            if(maxim<temp)
            {
                maxim=temp;
                index=contourIdx;
            }
        }
        Mat drawing = Mat.zeros(srcRes.size(), CvType.CV_8UC1);
        Imgproc.drawContours(drawing, contours, index, new Scalar(255), 1);


        // we present the result - canny
        Bitmap bit4 = Bitmap.createBitmap(drawing.cols(), drawing.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(drawing, bit4);
        step4.setImageBitmap(bit4);

        /*
        Now, we will detect the lines in this image, which contain only the biggest contours.
        We will try to find the point of intersection of these lines, and use this to detect the
        corners of the page in the image:
        */

        Mat lines = new Mat();

        Imgproc.HoughLinesP(drawing, lines, 1, Math.PI/180, 50,20,20);

        ArrayList<Point> corners = new ArrayList<Point>();
        for (int i = 0; i < lines.rows(); i++)
        {
            for (int j = i+1; j < lines.rows(); j++) {
                double[] line1 = lines.get(i,0);
                double[] line2 = lines.get(j, 0);
                Point pt = findIntersection(line1, line2);
                Log.d("com.packtpub.chapter9", pt.x+" "+pt.y);
                if (pt.x >= 0 && pt.y >= 0 && pt.x <=
                        drawing.cols() && pt.y <= drawing.rows()){
                    if(!exists(corners, pt)){
                        corners.add(pt);
                    }
                }
            }
        }

        if(corners.size() != 4){
            errorMsg = "Cannot detect perfect corners";
            //return null;

        }
        sortCorners(corners);// sort to 0-top_left , 1-top_right , 2-bottom_right, 3-bottom_left

        double top = Math.sqrt(Math.pow(corners.get(0).x -
                corners.get(1).x, 2) + Math.pow(corners.get(0).y -
                corners.get(1).y, 2));
        double right = Math.sqrt(Math.pow(corners.get(1).x -
                corners.get(2).x, 2) + Math.pow(corners.get(1).y -
                corners.get(2).y, 2));
        double bottom = Math.sqrt(Math.pow(corners.get(2).x -
                corners.get(3).x, 2) + Math.pow(corners.get(2).y -
                corners.get(3).y, 2));
        double left = Math.sqrt(Math.pow(corners.get(3).x -
                corners.get(1).x, 2) + Math.pow(corners.get(3).y -
                corners.get(1).y, 2));
        Mat quad = Mat.zeros(new Size(Math.max(top, bottom),
                Math.max(left, right)), CvType.CV_8UC3);

        ArrayList<Point> result_pts = new ArrayList<Point>();
        result_pts.add(new Point(0, 0));
        result_pts.add(new Point(quad.cols(), 0));
        result_pts.add(new Point(quad.cols(), quad.rows()));
        result_pts.add(new Point(0, quad.rows()));

        Mat cornerPts = Converters.vector_Point2f_to_Mat(corners);
        Mat resultPts = Converters.vector_Point2f_to_Mat(result_pts);
        Mat transformation = Imgproc.getPerspectiveTransform(cornerPts,
                resultPts);
        Imgproc.warpPerspective(srcOrig, quad, transformation,
                quad.size());


        Imgproc.cvtColor(quad, quad, Imgproc.COLOR_BGR2RGBA);



        Bitmap bitmap = Bitmap.createBitmap(quad.cols(), quad.rows(),
                Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(quad, bitmap);

        if(bitmap!=null) {
            ivImage.setImageBitmap(bitmap);
        } else if (errorMsg != null){
            Toast.makeText(getApplicationContext(),
                    errorMsg, Toast.LENGTH_SHORT).show();
        }

        //Imgproc.circle(drawing,corners.get(0),22,new Scalar(50));
         //Imgproc.circle(drawing,corners.get(1),22,new Scalar(100));
         //Imgproc.circle(drawing,corners.get(2),22,new Scalar(150));
         //Imgproc.circle(drawing,corners.get(3),22,new Scalar(200));
         //Bitmap bitmap1 = Bitmap.createBitmap(drawing.cols(), drawing.rows(), Bitmap.Config.ARGB_8888);
         //Utils.matToBitmap(drawing, bitmap1);

    }

    private static int calcScaleFactor(int rows, int cols){
        int idealRow, idealCol;
        if(rows<cols){
            idealRow = 240;
            idealCol = 320;
        } else {
            idealCol = 240;
            idealRow = 320;
        }
        int val = Math.min(rows / idealRow, cols / idealCol);
        if(val<=0){
            return 1;
        } else {
            return val;
        }
    }

    static void sortCorners(ArrayList<Point> corners) {
        ArrayList<Point> top, bottom;
        top = new ArrayList<Point>();
        bottom = new ArrayList<Point>();
        Point center = new Point();
        for(int i=0; i<corners.size(); i++){
            center.x += corners.get(i).x/corners.size();
            center.y += corners.get(i).y/corners.size();
        }
        for (int i = 0; i < corners.size(); i++)
        {
            if (corners.get(i).y < center.y)
                top.add(corners.get(i));
            else
                bottom.add(corners.get(i));
        }
        corners.clear();
        if (top.size() == 2 && bottom.size() == 2){
            Point top_left = top.get(0).x > top.get(1).x ?
                    top.get(1) : top.get(0);
            Point top_right = top.get(0).x > top.get(1).x ?
                    top.get(0) : top.get(1);
            Point bottom_left = bottom.get(0).x > bottom.get(1).x
                    ? bottom.get(1) : bottom.get(0);
            Point bottom_right = bottom.get(0).x > bottom.get(1).x
                    ? bottom.get(0) : bottom.get(1);
            top_left.x *= scaleFactor;
            top_left.y *= scaleFactor;
            top_right.x *= scaleFactor;
            top_right.y *= scaleFactor;
            bottom_left.x *= scaleFactor;
            bottom_left.y *= scaleFactor;
            bottom_right.x *= scaleFactor;
            bottom_right.y *= scaleFactor;
            corners.add(top_left);
            corners.add(top_right);
            corners.add(bottom_right);
            corners.add(bottom_left);
        }
    }

    static boolean exists(ArrayList<Point> corners, Point pt){
        for(int i=0; i<corners.size(); i++){
            if(Math.sqrt(Math.pow(corners.get(i).x-pt.x,
                    2)+Math.pow(corners.get(i).y-pt.y, 2)) < 10){
                return true;
            }
        }
        return false;
    }

    static Point findIntersection(double[] line1, double[] line2) {
        double start_x1 = line1[0];
        double start_y1 = line1[1];
        double end_x1 = line1[2];
        double end_y1 = line1[3];
        double start_x2 = line2[0];
        double start_y2 = line2[1];
        double end_x2 = line2[2];
        double end_y2 = line2[3];

        double denominator = ((start_x1 - end_x1) * (start_y2 -
                end_y2)) - ((start_y1 - end_y1) * (start_x2 - end_x2));
        if (denominator!=0)
        {
            Point pt = new Point();
            pt.x = ((start_x1 * end_y1 - start_y1 * end_x1) *
                    (start_x2 - end_x2) - (start_x1 - end_x1) *
                    (start_x2 * end_y2 - start_y2 * end_x2)) /
                    denominator;
            pt.y = ((start_x1 * end_y1 - start_y1 * end_x1) *
                    (start_y2 - end_y2) - (start_y1 - end_y1) *
                    (start_x2 * end_y2 - start_y2 * end_x2)) /
                    denominator;
            return pt;
        }
        else
            return new Point(-1, -1);
    }

    static double calcWhiteDist(double r, double g, double b){
        return Math.sqrt(Math.pow(255 - r, 2) +
                Math.pow(255 - g, 2) + Math.pow(255 - b, 2));
    }


}
