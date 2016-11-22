package application.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import application.API.Bill;
import application.API.Company;
import application.API.User;
import application.Configuration.Configuration;
import billsorganizer.billsorganizer.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class loadingActivity extends Activity {

    private int progress_precentage = 0;
    private Handler handler_data_ready;
    private String SharedPreferences_Email = "1";
    private ArrayList<Company> final_table;
    private boolean isRegisterd = false;
    private boolean login_load = false;
    private User user_profile;

    @InjectView(R.id.progress_bar_loading) ProgressBar _loadingProgressBar;

    /*========================================================================
        disable the option for the backpress when loading
    ========================================================================== */
    @Override
    public void onBackPressed() {
        if (isNetworkConnected() ) {
            return;
        }
        else
            finish();
    }

    /*============================================================================================
      First we check internet connection than we ask for all the tables from ServerConfiguration
    =============================================================================================== */

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ButterKnife.inject(this);
        handler_data_ready = new Handler();

        if ( getIntent().getSerializableExtra("login_load") != null) {
            login_load = (boolean) getIntent().getSerializableExtra("login_load");
        }


        if (isNetworkConnected() ) {
            // Start lengthy operation in a background thread
            new Thread(new Runnable() {
                public void run() {
                    initApplication();
                    handler_data_ready.post(new Runnable() {
                        public void run() {
                            onDataReady();
                        }
                    });
                }
            }).start();

        }
        else
            Toast.makeText(loadingActivity.this, getString(R.string.noConnection), Toast.LENGTH_SHORT).show();

    }

    /*========================================================================
            we ask for all the tables from database (Configuration)
    ========================================================================== */

    private void initApplication() {

        //update progress bar for the feeling of user.
        updateProgress(25);

        //happens only when user connected in login.
        if (login_load){
            SharedPreferences_Email = (String) getIntent().getSerializableExtra("Email"); // get from login
        }
        else{
            //check if user exists in shared preferences.
            SharedPreferences_Email = retrieveSharedPreferences();
        }


        if (SharedPreferences_Email == null){  //if user is no registered

            isRegisterd = false;
            updateProgress(100);
            return;
        }
        else{         //if user is registered to system shared preferences we have email and load of the data from data base.

            isRegisterd = true;

            volleyGetUserCompamnies companies = new volleyGetUserCompamnies(SharedPreferences_Email);
            while (companies.final_table == null) ;
            updateProgress(50);


            volleyGetUserBills bills = new volleyGetUserBills(SharedPreferences_Email,companies.final_table);
            while (bills.final_table == null) ;
            updateProgress(75);
            final_table = bills.final_table;

            if (final_table.size() == 0 )
                final_table = null;

            //Retrieve user profile
            volleyGetUserProfile(SharedPreferences_Email);
            while(user_profile == null);
            updateProgress(100);

        }

        //just to make sure
        login_load = false;
    }

    /*========================================================================
                This function get called when the all the data is ready
    ========================================================================== */

    private void onDataReady() {

        Intent login_intent = new Intent(loadingActivity.this, loginActivity.class);
        Intent main_intent = new Intent(loadingActivity.this, mainActivity.class);

        if ( isRegisterd ){

            main_intent.putExtra("user_information", final_table);
            main_intent.putExtra("Email", SharedPreferences_Email);
            main_intent.putExtra("user_profile", user_profile);

            this.startActivity(main_intent);
            finish(); // kill the loading activity
        }
        else{
            login_intent.putExtra("Email", SharedPreferences_Email);
            this.startActivity(login_intent);
            finish(); // kill the loading activity

        }
    }

    /*================================================================
        Retrieve Shared Preferences email loading the regarding data
    ==================================================================*/

    private String retrieveSharedPreferences(){

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.PREF, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        return pref.getString("Email", null); // getting String
    }

    /*===========================
       Update the progress bar.
    ==============================*/

    private void updateProgress(int i) {
        progress_precentage = i;
        // Update the progress bar
        handler_data_ready.post(new Runnable() {
            public void run()
            {
                _loadingProgressBar.setProgress(progress_precentage);
            }
        });
    }

    /*=============================
        Load registered user bills.
    ================================*/

    private class volleyGetUserBills implements Serializable {

        private ArrayList<Company> final_table;

        public volleyGetUserBills(final String email, final ArrayList<Company> companies){

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.GET_USER_BILLS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String table_from_server) {

                            table_from_server = table_from_server.replace("\n", "").replace("\r", "");
                            final_table = ParseJson_UserBills(table_from_server,companies);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {


                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put(Configuration.KEY_EMAIL, email);


                    //returning parameter
                    return params;
                }
            };

            //Adding the string request to the queue
            RequestQueue requestQueue = Volley.newRequestQueue(loadingActivity.this);
            requestQueue.add(stringRequest);
        }

        private ArrayList<Company> ParseJson_UserBills(String response,ArrayList<Company> companies) {

            JSONObject record;
            boolean isNewCompany = false;
            int numberOfCompanies = 0;
            try{
                JSONArray table = new JSONArray(response);

                for(int i=0;i<table.length();i++) {

                    record = table.getJSONObject(i);

                    String bill_id = record.getString("Bill_ID");
                    String company_id = record.getString("Company_ID");
                    String from_date = record.getString("From_Date");
                    String to_date = record.getString("To_Date");
                    String date_to_pay = record.getString("Date_To_Pay");
                    String amount = record.getString("Amount");
                    String paid = record.getString("Paid");

                    Bill bill = new Bill(bill_id,company_id,from_date,to_date,date_to_pay,amount,paid);

                    String company_name;

                    for(int j=0;j<companies.size();j++) {
                        if(companies.get(j).getCompany_ID().equals(company_id) )
                            companies.get(j).children.add(bill);
                    }//end add to specific companny


                }//end table from server


            }//end try
            catch (JSONException e1) {
                e1.printStackTrace();
            }
            return companies;
        }
    }

    private void setUser(User user_profile){
        this.user_profile = user_profile;
    }

    /*=======================================
      Get the user profile details
    =========================================*/

    private final void volleyGetUserProfile(final String email){

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.GET_USER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //If we are getting success from server
                        response = response.replace("\n", "").replace("\r", "");
                        response = response.replaceAll("\\s","");

                        try {
                            JSONObject record;
                            JSONArray table = new JSONArray(response);
                            record = table.getJSONObject(0);

                            String email = record.getString("Email");
                            String pass = record.getString("Password");
                            String firstname = record.getString("Firstname");
                            String surename = record.getString("Surename");

                            User user = new User(email,pass,firstname,surename);
                            setUser(user);

                        }
                        catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                        //

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Configuration.KEY_EMAIL, email);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    /*=======================================================
               load registered user comapmies
    ========================================================== */

    private class volleyGetUserCompamnies implements Serializable {

        private ArrayList<Company> final_table;

        public volleyGetUserCompamnies(final String email){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.GET_USER_COMPANIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String table_from_server) {

                        table_from_server = table_from_server.replace("\n", "").replace("\r", "");
                        final_table = ParseJson_UserCompanies(table_from_server);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Configuration.KEY_EMAIL, email);


                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(loadingActivity.this);
        requestQueue.add(stringRequest);
        }

        private ArrayList<Company> ParseJson_UserCompanies(String response) {

            ArrayList<Company> companies = new ArrayList<>();
            JSONObject record;
            boolean isNewCompany = false;
            int numberOfCompanies = 0;
            try{
                JSONArray table = new JSONArray(response);

                for(int i=0;i<table.length();i++) {
                    record = table.getJSONObject(i);

                    String company_id = record.getString("Company_ID");
                    String company_name = record.getString("Company_Name");

                    Company co = new Company(company_id, company_name);
                    companies.add(co);
                }

                }
            catch (JSONException e1) {
                e1.printStackTrace();
            }
            return companies;
        }
    }

    /*===========================
       Check internet connection
    ============================= */

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}
