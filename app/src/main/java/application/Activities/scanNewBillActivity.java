package application.Activities;


import android.app.FragmentTransaction;
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
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.scanlibrary.*;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
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
import java.util.Queue;
import java.util.Vector;

import billsorganizer.billsorganizer.R;

public class scanNewBillActivity extends AppCompatActivity {

    static int openCvReady = 0;
    progressBar pb;
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
    final private Context mContext = this;
    Context context = this.getBaseContext();
    String mCurrentPhotoPath,errorMsg;
    static final int REQUEST_TAKE_PHOTO = 1,REQUEST_IMAGE_CAPTURE = 1;
    Uri fileUri;
    final int LOAD_PHOTO = 0, CLICK_PHOTO = 1;
    Button bClickImage, bLoadImage,newbClickImage, newbLoadImage;
    Mat srcOrig,src,logo1Mat,srcMat;
    static int scaleFactor = 0;
    Bitmap bitmap;
    Intent intent;
    ImageView ivImage, logo[] = new ImageView[5];
    TextView spTextView[] = new TextView[5],dpTextView[] = new TextView[5];
    int reziseCols = 60,reziseRows = 20;


    static int count = 0;

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


        bClickImage = (Button)findViewById(R.id.bClickImage);
        bLoadImage = (Button)findViewById(R.id.bLoadImage);
        intent = new Intent(this.getBaseContext(),ScanActivity.class);

        //initial Logos Compared
        logo[0] = (ImageView) findViewById(R.id.logo1);
        logo[1] = (ImageView) findViewById(R.id.logo2);
        logo[2] = (ImageView) findViewById(R.id.logo3);
        logo[3] = (ImageView) findViewById(R.id.logo4);
        logo[4] = (ImageView) findViewById(R.id.logo5);
        spTextView[0] = (TextView)findViewById(R.id.spVal1);
        spTextView[1] = (TextView)findViewById(R.id.spVal2);
        spTextView[2] = (TextView)findViewById(R.id.spVal3);
        spTextView[3] = (TextView)findViewById(R.id.spVal4);
        spTextView[4] = (TextView)findViewById(R.id.spVal5);
        dpTextView[0] = (TextView)findViewById(R.id.dpVal1);
        dpTextView[1] = (TextView)findViewById(R.id.dpVal2);
        dpTextView[2] = (TextView)findViewById(R.id.dpVal3);
        dpTextView[3] = (TextView)findViewById(R.id.dpVal4);
        dpTextView[4] = (TextView)findViewById(R.id.dpVal5);



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
        final LinearLayout linearLayout1 = (LinearLayout)findViewById(R.id.findDoclayout);
        final LinearLayout linearLayout2 = (LinearLayout)findViewById(R.id.checkMatchLayout);
        Switch formOrLogo = (Switch) findViewById(R.id.logoOrForm);
        formOrLogo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    count = -1;
                    linearLayout1.setVisibility(View.VISIBLE);
                    linearLayout2.setVisibility(View.GONE);
                }
                else {
                    count = 0;
                    linearLayout1.setVisibility(View.GONE);
                    linearLayout2.setVisibility(View.VISIBLE);
                }
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
                        if (openCvReady == 1) {
                            final Bitmap selectedImage = BitmapFactory.decodeStream(stream);
                            stream.close();
                                if(count == -1){
                                    FindDoc(selectedImage);
                                }else if(count == 0){ //MatchLogo
                                    LogoMatch(selectedImage);
                                }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
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
                            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                if(count == -1){ //find doc
                                    FindDoc(selectedImage);
                                }else if(count == 0) {
                                    LogoMatch(selectedImage);
                                }
                        }
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
    Mat prepareLogoToCompare(int LogoNum){
        Mat tmp = new Mat();
        Bitmap bitmap;
        bitmap = ((BitmapDrawable)logo[LogoNum].getDrawable()).getBitmap();
        Utils.bitmapToMat(bitmap, tmp);
        Imgproc.cvtColor(tmp, tmp,Imgproc.COLOR_RGB2GRAY);
        Imgproc.blur(tmp, tmp, new Size(3, 3));
        //convert to black and white
        Imgproc.threshold(tmp, tmp, 0, 255, Imgproc.THRESH_OTSU);
        Imgproc.resize(tmp, tmp, new
                Size(reziseCols,reziseRows));
        tmp = CropLogo(tmp);
        Imgproc.resize(tmp, tmp, new
                Size(reziseCols,reziseRows));
        return tmp;
    }


    void LogoMatch(Bitmap selectedImage){

        logo1Mat = new Mat(selectedImage.
                getHeight(), selectedImage.
                getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(selectedImage, logo1Mat);
        Bitmap bitmap = Bitmap.createBitmap(logo1Mat.cols(), logo1Mat.rows(),
                Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(logo1Mat,bitmap);
        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setImageBitmap(bitmap);
        //img Procces
        Imgproc.cvtColor(logo1Mat, logo1Mat,Imgproc.COLOR_RGB2GRAY);
        //Clean
        Imgproc.blur(logo1Mat, logo1Mat, new Size(3, 3));
        //convert to black and white
        Imgproc.threshold(logo1Mat, logo1Mat, 0, 255, Imgproc.THRESH_OTSU);
        //Mat to BitMap
        Imgproc.resize(logo1Mat, logo1Mat, new
                Size(reziseCols,reziseRows));
        logo1Mat = CropLogo(logo1Mat);
        Imgproc.resize(logo1Mat, logo1Mat, new
                Size(reziseCols,reziseRows));


        double MinSub = 1000,MaxDp = 0;
        String companydp = "",companysp ="";
        //count++;
        for(int i=0 ; i < 5 ;i++) {
            Mat tmp =  prepareLogoToCompare(i);
            double dpVal  = CompareLogoDp(tmp,logo1Mat);
            dpTextView[i].setText("Dot Product Value : " + dpVal);
            double spVal  = CompareLogo(tmp,logo1Mat);
            spTextView[i].setText("Sub Pixels Value : " + spVal);

            if(dpVal > MaxDp){
                MaxDp = dpVal;
                switch(i) {
                    case 0: companydp = "hot";  break;
                    case 1: companydp = "isracard"; break;
                    case 2: companydp = "leumi"; break;
                    case 3: companydp = "yes"; break;
                    case 4: companydp = "shufersal"; break;
                }

                if(spVal < MinSub){
                    MinSub = spVal;
                    switch(i) {
                        case 0: companysp = "hot"; break;
                        case 1: companysp = "isracard"; break;
                        case 2: companysp = "leumi"; break;
                        case 3: companysp = "yes"; break;
                        case 4: companysp = "shufersal"; break;
                    }
                }
            }
        }

        TextView spTextView6 = (TextView)findViewById(R.id.companydp);
        spTextView6.setText("Dp Matching:"+ companydp);
        TextView spTextView7 = (TextView)findViewById(R.id.companysp);
        spTextView7.setText("Sp Matching:"+companydp);


    }

    void FindDoc(Bitmap selectedImage){

        ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
        ivImage.setImageBitmap(selectedImage);
        srcOrig = new Mat(selectedImage.
                getHeight(), selectedImage.
                getWidth(), CvType.CV_8UC4);
        Imgproc.cvtColor(srcOrig, srcOrig,
                Imgproc.COLOR_BGR2RGB);
        Utils.bitmapToMat(selectedImage, srcOrig);
        scaleFactor = calcScaleFactor(
                srcOrig.rows(), srcOrig.cols());
        src = new Mat();
        Imgproc.resize(srcOrig, src, new
                Size(srcOrig.cols() / scaleFactor,
                srcOrig.rows() / scaleFactor));
        Imgproc.GaussianBlur(src, src,
                new Size(5, 5), 1);

        boolean res = getpage();

        ImageView out = (ImageView) findViewById(R.id.out);
        if(res)
            out.setImageBitmap(bitmap);
        else{
            Toast.makeText(getApplicationContext(),
                    errorMsg, Toast.LENGTH_LONG).show();
        }
    }
    Mat CropLogo(Mat srcLogo) {

        int top = 0,bottom = 0,left = 0,right = 0;
        double currPix = 0.0, averageBack;
        Mat CropLogoMat = new Mat(srcLogo.rows(), srcLogo.cols(), CvType.CV_8UC1);
        averageBack = srcLogo.get(0,0)[0];

        //findTop
        for (int i = 0; i < srcLogo.rows(); i++) {
            for (int j = 0; j < srcLogo.cols(); j++) {
                currPix = srcLogo.get(i,j)[0];
                if(Math.abs(currPix - averageBack) < 10){
                    averageBack = (averageBack+currPix)/2;
                }else

                {
                    top = i;
                    j = srcLogo.cols();
                    i = srcLogo.rows();
                }
            }
        }
        Rect roi = new Rect(0,top,srcLogo.cols(),srcLogo.rows()-top);
        Mat cropped = new Mat(srcLogo, roi);


        //findBottom

        averageBack = cropped.get(cropped.rows()-1,0)[0];
        for (int i = cropped.rows()-1; i > 0; i--) {
            for (int j = 0; j < cropped.cols(); j++) {
                currPix = cropped.get(i,j)[0];
                if(Math.abs(currPix - averageBack) < 10){
                    averageBack = (averageBack+currPix)/2;
                }else

                {
                    bottom = i;
                    j = cropped.cols();
                    i = 0;
                }
            }
        }

         roi = new Rect(0,0,cropped.cols(),bottom);
         cropped = new Mat(cropped, roi);


        //findRight
        averageBack = cropped.get(0,0)[0];
         for(int j = 0; j < cropped.cols(); j++) {
              for (int i = 0; i < cropped.rows(); i++) {
                currPix = cropped.get(i,j)[0];
                if(Math.abs(currPix - averageBack) < 10){
                    averageBack = (averageBack+currPix)/2;
                }
                else
                {
                    right = j;
                    j = cropped.cols();
                    i = cropped.rows();
                }
            }
        }

        roi = new Rect(right,0,cropped.cols() - right,cropped.rows());
        cropped = new Mat(cropped, roi);


        //findLeft
        averageBack = cropped.get(0,cropped.cols()-1)[0];
        for(int j = cropped.cols()-1; j > 0; j--) {
            for (int i = 0; i < cropped.rows(); i++) {
                currPix = cropped.get(i,j)[0];
                if(Math.abs(currPix - averageBack) < 10){
                    averageBack = (averageBack+currPix)/2;
                }
                else
                {
                    left = j;
                    j = 0;
                    i = cropped.rows();
                }
            }
        }

        roi = new Rect(0,0,left,cropped.rows());
        cropped = new Mat(cropped, roi);




       // Bitmap bitmap1 = Bitmap.createBitmap(cropped.cols(), cropped.rows(),
         //       Bitmap.Config.ARGB_8888);

      //  Utils.matToBitmap(cropped,bitmap1);

        //set on src
       // ImageView logo2 = (ImageView) findViewById(R.id.ivImage);
        //logo.setImageBitmap(bitmap1);

        return cropped;
    }
    double CompareLogoDp(Mat logoMat1,Mat logoMat2) {

        double[] pix1, pix2, pix3;
        double sum = 0.0;

        for (int i = 0; i < Math.min(logoMat1.rows(), logoMat2.rows()); i++) {
            for (int j = 0; j < Math.min(logoMat1.cols(), logoMat2.cols()); j++) {
                pix1 = logoMat1.get(i, j); //R,G,B,(A-transprant)
                pix2 = logoMat2.get(i, j); //R,G,B,(A-transprant)
                sum += pix1[0] * pix2[0];

            }
        }
        return sum / 1000000;
    }
    double CompareLogo(Mat logoMat1,Mat logoMat2) {

        double[] pix1, pix2, pix3;
        double sum = 0.0;

        for (int i = 0; i < Math.min(logoMat1.rows(), logoMat2.rows()); i++) {
            for (int j = 0; j < Math.min(logoMat1.cols(), logoMat2.cols()); j++) {
                pix1 = logoMat1.get(i, j); //R,G,B,(A-transprant)
                pix2 = logoMat2.get(i, j); //R,G,B,(A-transprant)
                sum += Math.abs(pix1[0] - pix2[0]);

            }
        }


/*


        //double i = quad.get(1,1);
        Mat grays= new Mat(),BGRgray = new Mat();
        bitmap = Bitmap.createBitmap(quad.cols(), quad.rows(),
                Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(quad, bitmap);

        Imgproc.cvtColor(quad,grays,Imgproc.COLOR_RGB2GRAY);
        Bitmap bitmap1 = Bitmap.createBitmap(grays.cols(), grays.rows(),
                Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(grays, bitmap1);

        Imgproc.cvtColor(quad,BGRgray,Imgproc.COLOR_RGB2BGR);
        Imgproc.cvtColor(BGRgray,BGRgray,Imgproc.COLOR_BGR2GRAY);
        Bitmap bitmap2 = Bitmap.createBitmap(BGRgray.cols(), BGRgray.rows(),
                Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(BGRgray, bitmap2);

        double[] pix1,pix2,pix3,sum12= new double[4],sum23= new double[4];
        for(int i = 0 ; i < quad.rows() ;i++) {
            for (int j = 0; j < quad.cols(); j++) {
                pix1 =  grays.get(i, j); //R,G,B,(A-transprant)
                pix2 =  quad.get(i, j); //R,G,B,(A-transprant
                pix3 =  quad.get(i, j); //R,G,B,(A-transprant
                sum12[0] += pix2[0]*pix3[0];
                sum12[1] += pix2[1]*pix3[1];
                sum12[2] += pix2[2]*pix3[2];
                sum12[3] += pix2[3]*pix3[3];
            }
            */
       return sum/1000000;
    }




    boolean getpage() {


        ImageView step1 = (ImageView) findViewById(R.id.step1);
        ImageView step2 = (ImageView) findViewById(R.id.step2);
        ImageView step3 = (ImageView) findViewById(R.id.step3);
        ImageView step4 = (ImageView) findViewById(R.id.step4);
        ImageView step5 = (ImageView) findViewById(R.id.step5);
        ImageView step7 = (ImageView) findViewById(R.id.step7);

        /*
        First of all, we will make our image in the desired form to perform a k-means
        clustering with two clusters. The intuition behind applying k-means is that the
        background and foreground will be quite distinct from the background and most of
        the area will be occupied by the page:
         */
/*
        Mat samples = new Mat(src.rows() * src.cols(), 3, CvType.CV_32F);
        for (int y = 0; y < src.rows(); y++) {
            for (int x = 0; x < src.cols(); x++) {
                for (int z = 0; z < 3; z++) {
                    samples.put(x + y * src.cols(), z, src.get(y, x)[z]);
                }
            }
        }
*/
/*
        //Then, we will apply the k-means algorithm as follows:
        int clusterCount = 2;
        Mat labels = new Mat();
        int attempts = 5;
        Mat centers = new Mat();
        Core.kmeans(samples, clusterCount, labels, new
                        TermCriteria(TermCriteria.MAX_ITER |
                        TermCriteria.EPS, 10000, 0.0001), attempts,
                Core.KMEANS_PP_CENTERS, centers);
*/
        /*
        Now, we have the two cluster centers and the labels for each pixel in the original
        image. We will use the two cluster centers to detect which one corresponds to the
        paper. For this, we will find the Euclidian distance between the color of both the
        centers and the color pure white. The one which is closer to the color pure white will
        be considered as the foreground:
         */
/*
        double dstCenter0 = calcWhiteDist(centers.get(0,
                0)[0], centers.get(0, 1)[0], centers.get(0, 2)[0]);
        double dstCenter1 = calcWhiteDist(centers.get(1,
                0)[0], centers.get(1, 1)[0], centers.get(1, 2)[0]);
        int paperCluster = (dstCenter0 < dstCenter1) ? 0 : 1;

        //We need to define two Mat objects that we will use in the next step:


*/
        /*
        Now, we will perform a segmentation where we will display all the foreground
        pixels as white and all the background pixels as black:
         */
/*
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
 */
        /*
        Now, we will move on to the next step; that is, detecting contours in this image.
        First, we will apply the Canny edge detector to detect just the edges and then
        apply a contouring algorithm:
        */
        Mat srcRes = new Mat(src.size(), src.type());
        Mat srcGray = new Mat();
        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);

        // we present the result - grayscale
        Bitmap bit2 = Bitmap.createBitmap(srcGray.cols(), srcGray.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(srcGray, bit2);
        step2.setImageBitmap(bit2);

        Imgproc.Canny(srcGray, srcGray, 100, 200);

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

            return false;
        }
        sortCorners(corners);// sort to 0-top_left , 1-top_right , 2-bottom_, 3-bottom_left

        double top = Math.sqrt(Math.pow(corners.get(0).x -
                corners.get(1).x, 2) + Math.pow(corners.get(0).y -
                corners.get(1).y, 2));
        double right = Math.sqrt(Math.pow(corners.get(1).x -
                corners.get(3).x, 2) + Math.pow(corners.get(1).y -
                corners.get(3).y, 2));
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
        result_pts.add(new Point(0, quad.rows()));
        result_pts.add(new Point(quad.cols(), quad.rows()));

        Mat cornerPts = Converters.vector_Point2f_to_Mat(corners);
        Mat resultPts = Converters.vector_Point2f_to_Mat(result_pts);
        Mat transformation = Imgproc.getPerspectiveTransform(cornerPts,
                resultPts);
        Imgproc.warpPerspective(srcOrig, quad, transformation,
                quad.size());

        bitmap = Bitmap.createBitmap(quad.cols(), quad.rows(),
                Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(quad, bitmap);

        return true;


        /*
        Imgproc.drawMarker(drawing,corners.get(0),new Scalar(0,0,255),10,10,10,10);
        Imgproc.drawMarker(drawing,corners.get(1),new Scalar(0,25,255),10,10,10,10);
        Imgproc.drawMarker(drawing,corners.get(2),new Scalar(0,0,255),10,10,10,10);
        Imgproc.drawMarker(drawing,corners.get(4),new Scalar(0,0,255),10,10,10,10);
        Imgproc.circle(drawing,corners.get(0),22,new Scalar(50));
        Imgproc.circle(drawing,corners.get(1),22,new Scalar(100));
        Imgproc.circle(drawing,corners.get(2),22,new Scalar(150));
        Imgproc.circle(drawing,corners.get(3),22,new Scalar(200));
        Bitmap bitmap1 = Bitmap.createBitmap(drawing.cols(), drawing.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(drawing, bitmap1);
*/
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
            corners.add(bottom_left);
            corners.add(bottom_right);

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
