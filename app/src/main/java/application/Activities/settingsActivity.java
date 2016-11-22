package application.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import billsorganizer.billsorganizer.R;

public class settingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setActionBar();
    }

    /*=======================================
      Set tool bar to the top of the screen.
    =========================================*/

    private void setActionBar(){

        LayoutInflater _inflater = LayoutInflater.from(this);
        View _actionBarView = _inflater.inflate(R.layout.actionbar, null);
        android.support.v7.app.ActionBar _actionBar = getSupportActionBar();
        TextView _actionBarTitleText = (TextView) _actionBarView.findViewById(R.id.title_text_toolbar);
        ImageView left_profile_btn = (ImageView) _actionBarView.findViewById(R.id.left_button_toolbar);
        ImageButton right_refresh_btn = (ImageButton) _actionBarView.findViewById(R.id.right_button_toolbar);

        _actionBar.setDisplayShowHomeEnabled(false);
        _actionBar.setDisplayShowTitleEnabled(false);

        _actionBarTitleText.setText(getString(R.string.settings));


        left_profile_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        right_refresh_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());            }
        });

        _actionBar.setBackgroundDrawable(getDrawable(R.drawable.gradient_bg_company));
        _actionBar.setCustomView(_actionBarView);
        _actionBar.setDisplayShowCustomEnabled(true);
    }

}
