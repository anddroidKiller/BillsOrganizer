package application.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;
import application.Configuration.Configuration;
import billsorganizer.billsorganizer.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class signupActivity extends Activity {

    private ProgressDialog progressDialog;

    @InjectView(R.id.input_firstname) EditText _firstnameText;
    @InjectView(R.id.input_surename) EditText _surenameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login_Activity activity
                finish();
            }
        });
    }

   /*=======================================================================================
             Check all fields, connectivity and start progress dialog.
    ========================================================================================== */

    public void onSignup() {

        if (!validateFields() && isNetworkConnected()) {
            onSignupFailed(getString(R.string.signup_failed));
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(signupActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.creating_account));
        progressDialog.show();

        String firstname = _firstnameText.getText().toString();
        String surename = _surenameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        volleySignupRequest(firstname,surename,email,password);
    }

    /*========================================================================
             Check username availability and sign into database.
    =========================================================================== */

    private final void volleySignupRequest(final String firstname, final String surename , final String email, final String password){

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.SIGNUP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Dissmiss progress dialog and enable button
                        progressDialog.dismiss();
                        _signupButton.setEnabled(true);

                        //If we are getting success from server
                        response = response.replace("\n", "").replace("\r", "");
                        response = response.replaceAll("\\s","");

                        if(response.equalsIgnoreCase(Configuration.SUCCESS)){

                            //Starting profile activity
                            onSignupSuccess(email);

                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            onSignupFailed(getString(R.string.email_exists));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Dissmiss progress dialog  and enable button for any problems
                        progressDialog.dismiss();
                        _signupButton.setEnabled(true);

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Configuration.KEY_FIRSTNAME, firstname);
                params.put(Configuration.KEY_SURENAME, surename);
                params.put(Configuration.KEY_EMAIL, email);
                params.put(Configuration.KEY_PASSWORD, password);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    /*=========================================================
        store Shared Preferences email for next connection
    ===========================================================*/

    public void storeSharedPreferences(String email){

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.PREF, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Email", email); // Storing string
        editor.commit(); // commit changes

    }

    /*===================================================================================
               Implemented if no connection or problems in fields' validation.
    ====================================================================================== */

    public void onSignupFailed(String msg) {
        Toast.makeText(getBaseContext(),msg, Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }


    /*===================================================================================
           implemented if connection is okay and no problems in fields' validation.
    ====================================================================================== */

    public void onSignupSuccess(String email) {

        Toast.makeText(getBaseContext(), getString(R.string.welcomeMsg), Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);

        storeSharedPreferences(email);

        //Starting profile activity
        Intent intent = new Intent(signupActivity.this, mainActivity.class);
        intent.putExtra("Email", email);
        startActivity(intent);
        finish();

    }


    /*=======================================================
                Validate all the fields on screen.
    ========================================================== */

    public boolean validateFields() {

        boolean valid = true;

        String firstname = _firstnameText.getText().toString();
        String surename = _surenameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();


        if (firstname.isEmpty()) {
            _firstnameText.setError(getString(R.string.please_fill_the_field));
            valid = false;
        } else {
            _firstnameText.setError(null);
        }
        if (surename.isEmpty()) {
            _surenameText.setError(getString(R.string.please_fill_the_field));
            valid = false;
        } else {
            _surenameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.enter_valid_email));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError(getString(R.string.between_four_to_ten_chars));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    /*=============================================
                Check internet connection
    ================================================ */

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}