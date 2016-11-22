package application.Activities;

import java.util.List;

import android.app.Activity;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import application.Configuration.CameraPreview;
import application.Configuration.imageHandler;
import application.Configuration.ResizableCameraPreview;
import billsorganizer.billsorganizer.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class scanNewBillActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private ResizableCameraPreview mPreview;
    private ArrayAdapter<String> mAdapter;
    private int mCameraId = 0;
    private Camera camera;

    @InjectView(R.id.camera_layout) RelativeLayout mLayout;
    @InjectView(R.id.spinner_size) Spinner spinnerSize;
    @InjectView(R.id.spinner_camera) Spinner spinnerCamera;
    @InjectView(R.id.capture_button) ImageButton capture_button;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_new_bill);

        //Initialize the xml
        ButterKnife.inject(this);
        //Initialize the xml spinners with adapters
        initSpinners();
        //Add oncklick listeners
        setListeners();
    }

    /*==============================================================================
                        Set listeners to screen buttons.
    ================================================================================= */

    private void setListeners() {

        capture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start proccess
               mPreview.mCamera.takePicture(null, null,
               new imageHandler(getApplicationContext()));

            }
        });

        //end add_listeners

    }

    /*==============================================================================
                    Initialize spinner options for the preview.
    ================================================================================= */

    private void initSpinners() {

        // Spinner for preview sizes
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(mAdapter);
        spinnerSize.setOnItemSelectedListener(this);

        // Spinner for camera ID
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCamera.setAdapter(adapter);
        spinnerCamera.setOnItemSelectedListener(this);
        adapter.add("0");
        adapter.add("1");
        adapter.add("2");

    }

    /*==============================================================================
                   Implemented when spinner item is selected.
    ================================================================================= */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_size:
                Rect rect = new Rect();
                mLayout.getDrawingRect(rect);

                if (0 == position) { // "Auto" selected
                    mPreview.surfaceChanged(null, 0, rect.width(), rect.height());
                } else {
                    mPreview.setPreviewSize(position - 1, rect.width(), rect.height());
                }
                break;
            case R.id.spinner_camera:
                mPreview.stop();
                mLayout.removeView(mPreview);
                mCameraId = position;
                createCameraPreview();
                break;
        }
    }

    /*==============================================================================
                Implemented when spinner item is opened and nothing changed.
    ================================================================================= */

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    /*==============================================================================
                Must create camera surface on activity started.
    ================================================================================= */

    @Override
    protected void onResume() {
        super.onResume();
        createCameraPreview();
    }

    /*==============================================================================
                Must remove camera surface on activity pause.
    ================================================================================= */

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
        mLayout.removeView(mPreview);
        mPreview = null;
    }

    /*==============================================================================
                    Create the camera preview on screen.
    ================================================================================= */

    private void createCameraPreview() {
        // Set the second argument by your choice.
        // Usually, 0 for back-facing camera, 1 for front-facing camera.
        // If the OS is pre-gingerbreak, this does not have any effect.
        mPreview = new ResizableCameraPreview(this, mCameraId, CameraPreview.LayoutMode.FitToParent, false);
        LayoutParams previewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLayout.addView(mPreview, 0, previewLayoutParams);

        mAdapter.clear();
        mAdapter.add("Auto");
        List<Camera.Size> sizes = mPreview.getSupportedPreivewSizes();
        for (Camera.Size size : sizes) {
            mAdapter.add(size.width + " x " + size.height);
        }
    }
}
