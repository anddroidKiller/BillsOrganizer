package application.Activities;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import billsorganizer.billsorganizer.R;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class openCvTest extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
  JavaCameraView javaCameraView;
    Mat mRgbaMat, mImgGray, mImgCanny,mImgTranspose,mImgF;
    SeekBar mSeekBar1,mSeekBar2;
    Switch mGrayOrCanny;
    double mTreshold1 = 0,mTreshold2 = 0;
    TextView mT1label,mT2label;
    boolean mGrayOrCannyR = false;


    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this){
        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case BaseLoaderCallback.SUCCESS:{
                    javaCameraView.enableView();
                    break;
                }
                default:{
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_cv_test);

        mSeekBar1 = (SeekBar)findViewById(R.id.seekBar1);
        mSeekBar2 = (SeekBar)findViewById(R.id.seekBar2);
        mT1label = (TextView) findViewById(R.id.treshold1) ;
        mT2label = (TextView) findViewById(R.id.treshold2) ;
        mGrayOrCanny = (Switch) findViewById(R.id.GrayscaleOrCanny);

        javaCameraView =(JavaCameraView) findViewById(R.id.java_cam);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);


            mSeekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                       mTreshold1 = i;
                    mT1label.setText("Treshold1 - " + String.valueOf(i));

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            mSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    mTreshold2 = i;
                    mT2label.setText("Treshold2 - " + String.valueOf(i));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            mGrayOrCanny.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    mGrayOrCannyR = b;
                }
            });
    }
    @Override
    protected void onPause(){
        super.onPause();
        if(javaCameraView!=null)
            javaCameraView.disableView();

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(javaCameraView!=null)
            javaCameraView.disableView();

    }
    @Override
    protected void onResume(){
        super.onResume();
        if(OpenCVLoader.initDebug()){
            Log.i("OpenCV","Loaded");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else
        {
            Log.i("OpenCV","Loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9,this,mLoaderCallback);
        }
    }

    @Override
    public void onCameraViewStarted(int i, int i1) {
        mRgbaMat = new Mat(i1, i, CvType.CV_8UC4);
        mImgGray = new Mat(i1, i, CvType.CV_8UC1);
        mImgCanny = new Mat(i1, i, CvType.CV_8UC1);
        mImgF = new Mat(i1, i, CvType.CV_8UC4);
        mImgTranspose = new Mat(i1, i, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
    mRgbaMat.release();
    }

    @Override


    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame cvCameraViewFrame) {



        mRgbaMat = cvCameraViewFrame.rgba();
        Core.transpose(mRgbaMat,mImgTranspose);
        Imgproc.resize(mImgTranspose, mImgF, mImgF.size(), 0,0, 0);
        Core.flip(mImgF, mRgbaMat, 1 );
        Imgproc.cvtColor(mRgbaMat,mImgGray,Imgproc.COLOR_RGB2GRAY);
        Imgproc.Canny(mImgGray,mImgCanny,mTreshold1,mTreshold2);

        if(mGrayOrCannyR)
            return mImgCanny;
        else
            return mImgGray ;
    }


}