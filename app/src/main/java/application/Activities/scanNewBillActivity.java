package application.Activities;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;


import application.PolygonLibrary.*;
import billsorganizer.billsorganizer.R;

public class scanNewBillActivity extends AppCompatActivity implements IScanner  {

    static int openCvReady = 0,scaleFactor = 0;

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

    /*     Fragment polygon  *//*     Fragment polygon  *//*     Fragment polygon  *//*     Fragment polygon  *//*     Fragment polygon  */
    private void init() {
        PickImageFragment fragment = new PickImageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ScanConstants.OPEN_INTENT_PREFERENCE, getPreferenceContent());
        fragment.setArguments(bundle);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, fragment);
        fragmentTransaction.commit();
    }

    protected int getPreferenceContent() {
        return getIntent().getIntExtra(ScanConstants.OPEN_INTENT_PREFERENCE, 0);
    }
    @Override
    public void onBitmapSelect(Uri uri) {
        ScanFragment fragment = new ScanFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ScanConstants.SELECTED_BITMAP, uri);
        bundle.putString("Operation","Select Document");
        fragment.setArguments(bundle);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, fragment);
        fragmentTransaction.addToBackStack(ScanFragment.class.toString());
        fragmentTransaction.commit();
    }
    @Override
    public void MarkLogo(Uri uri) {
    ScanFragment fragment = new ScanFragment();
    Bundle bundle = new Bundle();
    bundle.putParcelable(ScanConstants.SELECTED_BITMAP, uri);
    bundle.putString("Operation","Select Logo");
    fragment.setArguments(bundle);
    android.app.FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.add(R.id.content, fragment);
    fragmentTransaction.addToBackStack(ScanFragment.class.toString());
    fragmentTransaction.commit();
}

    
    @Override
    public void onScanFinish(Uri uri) {
        ResultFragment fragment = new ResultFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ScanConstants.SCANNED_RESULT, uri);
        fragment.setArguments(bundle);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, fragment);
        fragmentTransaction.addToBackStack(ResultFragment.class.toString());
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_img);
        init();
    }

}
