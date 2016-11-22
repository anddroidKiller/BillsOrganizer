package application.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import application.API.Company;
import application.API.User;
import application.Configuration.Configuration;
import billsorganizer.billsorganizer.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class mainActivity extends ActionBarActivity {


    private Intent
            Scan_New_Bill_Intent,
            Insert_New_Bill_Intent,
            View_Edit_Bills_Intent,
            View_Reports_Intent,
            Settings_Intent;

    private ArrayList<Company> final_table;
    private Drawer drawer;
    private String Email;
    private User user_profile;

    @InjectView(R.id.insert_bill_activity) Button _insert_billButton;
    @InjectView(R.id.scan_bill_activity) Button _scan_billButton;
    @InjectView(R.id.view_edit_bills_activity) Button _view_edit_billsButton;
    @InjectView(R.id.view_reports_activity) Button _view_reportsButton;
    @InjectView(R.id.settings_activity) Button _settingsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Email = (String) getIntent().getSerializableExtra("Email");
        final_table = (ArrayList<Company>) getIntent().getSerializableExtra("user_information");
        user_profile = (User) getIntent().getSerializableExtra("user_profile");


        //Initialize the xml
        ButterKnife.inject(this);
        //Add oncklick listeners
        setListeners();
        //Add new intent to pass activities
        setIntents();
        //Set navigation drawer to main
        drawer = setUserProfileDrawer();
        //Add Action Bar
        setActionBar();

    }



    /*=======================================
      Set tool bar to the top of the screen.
    =========================================*/

    private void setActionBar(){

        android.support.v7.app.ActionBar _actionBar = getSupportActionBar();
        _actionBar.setDisplayShowHomeEnabled(false);
        _actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater _Inflater = LayoutInflater.from(this);
        View _CustomView = _Inflater.inflate(R.layout.actionbar_main, null);
        TextView _TitleTextView = (TextView) _CustomView.findViewById(R.id.title_text_toolbar);
        _TitleTextView.setText(Email);


        ImageView btn = (ImageView) _CustomView.findViewById(R.id.left_button_toolbar);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (drawer.isDrawerOpen())
                    drawer.closeDrawer();
                else
                    drawer.openDrawer();
            }
        });

        _actionBar.setBackgroundDrawable(getDrawable(R.drawable.gradient_bg_company));
        _actionBar.setCustomView(_CustomView);
        _actionBar.setDisplayShowCustomEnabled(true);
    }

    /*====================
      create the drawer
    ======================*/

    private Drawer setUserProfileDrawer(){

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Home");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Change Profile");
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName("Share A Friend");
        SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(4).withName("Logout");


        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
                        item1, new DividerDrawerItem(),
                        item2, new SecondaryDrawerItem(),
                        item3, new SecondaryDrawerItem(),
                        item4, new SecondaryDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D

                        switch (position) {
                            case 1: {
                                break;
                            }
                            //Login
                            case 2: {
                                break;
                            }

                            case 3: {
                                break;
                            }
                            case 6: {
                                Intent login_intent = new Intent(mainActivity.this, loginActivity.class);
                                startActivity(login_intent);
                                finish(); // kill the loading activity
                                break;
                            }
                        }
                        return true;
                    }
                })
                .build();

        return result;
    }

    /*=============================================
       Set intents for new activities available.
    =============================================== */

    private void setIntents(){

        Scan_New_Bill_Intent = new Intent(getBaseContext(), scanNewBillActivity.class);
        Insert_New_Bill_Intent = new Intent(getBaseContext(), insertNewBillActivity.class);
        View_Edit_Bills_Intent = new Intent(getBaseContext(), viewEditBillsActivity.class);
        View_Reports_Intent  = new Intent(getBaseContext(), viewReportsActivity.class);
        Settings_Intent = new Intent(getBaseContext(), settingsActivity.class);

    }

    /*==========================================
       Set listeners to screen buttons.
    ============================================= */

    private void setListeners(){

        _scan_billButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scan_New_Bill_Intent.putExtra("user_information", final_table);
                Scan_New_Bill_Intent.putExtra("user_profile", user_profile);
                startActivity(Scan_New_Bill_Intent);
            }
        });

        _insert_billButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Insert_New_Bill_Intent.putExtra("user_information", final_table);
                Insert_New_Bill_Intent.putExtra("user_profile", user_profile);
                startActivity(Insert_New_Bill_Intent);
            }
        });

        _view_edit_billsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View_Edit_Bills_Intent.putExtra("user_information", final_table);
                View_Edit_Bills_Intent.putExtra("user_profile", user_profile);
                startActivity(View_Edit_Bills_Intent);

            }
        });

        _view_reportsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                View_Reports_Intent.putExtra("user_information", final_table);
                View_Reports_Intent.putExtra("user_profile", user_profile);
                startActivity(View_Reports_Intent);
            }
        });

        _settingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Settings_Intent.putExtra("user_information", final_table);
                Settings_Intent.putExtra("user_profile", user_profile);
                startActivity(Settings_Intent);
            }
        });
    }

    /*=======================================
       If user pressed to exit application
    =========================================*/

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}

