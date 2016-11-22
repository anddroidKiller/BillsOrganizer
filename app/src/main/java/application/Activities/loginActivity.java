package application.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import application.API.Company;
import application.Configuration.Configuration;
import billsorganizer.billsorganizer.R;
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class loginActivity extends Activity {

    private static final int REQUEST_SIGNUP = 0;
    private ProgressDialog progressDialog;

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize the xml
        ButterKnife.inject(this);
        //Add oncklick listeners
        setListeners();

    }

    /*==============================================================================
             Add Listeners to screen buttons.
    ================================================================================= */

    private void setListeners(){
        //set on click listener for login
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onLogin();
            }
        });
        //set on click listener for a signup
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup_Activity activity
                Intent intent = new Intent(getApplicationContext(), signupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });


    }

    /*==============================================================================
            check all fields validity, connectivity and start progress dialog.
    ================================================================================= */

    public void onLogin() {

        if (!validateFields() && isNetworkConnected()) {
            onLoginFailed(getString(R.string.login_failed));
            return;
        }

        //onLoginSuccess
        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(loginActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.authenticating));
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        //Ask server
        volleyLoginRequest(email,password);

    }

    /*===================================================================================
               implemented if no connection or problems in fields' validation.
    ====================================================================================== */

    public void onLoginFailed(String msg) {

        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    /*===================================================================================
               implemented if connection is okay and no problems in fields' validation.
    ====================================================================================== */

    public void onLoginSuccess(String email) {

        Toast.makeText(getBaseContext(), getString(R.string.welcomeMsg), Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);

        storeSharedPreferences(email);

        //Starting profile activity
        Intent loading_intent = new Intent(loginActivity.this, loadingActivity.class);
        loading_intent.putExtra("login_load", true);
        loading_intent.putExtra("Email", email);
        startActivity(loading_intent);
        finish();

    }

    /*=======================================
        if user pressed to exit application
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

    /*=========================================================
        store Shared Preferences email for next connection
    ===========================================================*/

    public void storeSharedPreferences(String email){

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.PREF, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Email", email); // Storing string
        editor.commit(); // commit changes

    }

    /*=======================================================
                Validate all the fields on screen.
    ========================================================== */

    public boolean validateFields() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

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

   /*=========================================
      Check username and password in database.
    ========================================== */

    private final void volleyLoginRequest(final String email, final String password){

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Dissmiss progress dialog and enable button
                        progressDialog.dismiss();
                        _loginButton.setEnabled(true);

                        //If we are getting success from server
                        response = response.replace("\n", "").replace("\r", "");
                        response = response.replaceAll("\\s","");

                        if(response.equalsIgnoreCase(Configuration.SUCCESS)){
                            onLoginSuccess(email);
                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            onLoginFailed(getString(R.string.invalid_username_or_password));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Dissmiss progress dialog  and enable button for any problems
                        progressDialog.dismiss();
                        _loginButton.setEnabled(true);

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
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

    /*=============================================
                Check internet connection
    ================================================ */

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
