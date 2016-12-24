package application.Activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import application.API.Bill;
import application.API.Company;
import application.API.User;
import application.Configuration.Configuration;
import billsorganizer.billsorganizer.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class insertNewBillActivity extends ActionBarActivity {

    private ArrayList<Company> final_table;
    private String m_Text = "";
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> spinnerArrayAdapter;
    public  TextView tv_spinner = null;
    private User user_profile;

    @InjectView(R.id.companies_list_insertbill_spinner) Spinner _list_companiesSpinner;
    @InjectView(R.id.date_from_picker_insertbill) Button _from_dateButton;
    @InjectView(R.id.date_to_picker_insertbill) Button _to_dateButton;
    @InjectView(R.id.amount_EditText) EditText _amountText;
    @InjectView(R.id.checkbox_paid) CheckBox _paidCheckbox;
    @InjectView(R.id.submit_insert_new_bill_button) Button _submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_new_bill);
        //Initialize the xml
        ButterKnife.inject(this);

        //grab the table with all bills
        final_table = (ArrayList<Company>) getIntent().getSerializableExtra("user_information");
        user_profile = (User) getIntent().getSerializableExtra("user_profile");

        //Add companies to spinner
        if (final_table != null)
            setCompaniesToSpinner();

        //Add oncklick listeners
        setListeners();
        //Add Action Bar
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

        _actionBarTitleText.setText(getString(R.string.insert_new_bill));


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

    /*===========================================================================
      Use the array list (companies) to update the list to the spinner adapter.
    ============================================================================= */

    private void setCompaniesToSpinner(){

        final ArrayList<String> companies = new ArrayList<String>();

        companies.add("Choose Company");

        for (int j=0; j<final_table.size();j++){

            companies.add(final_table.get(j).getCompany_Name());
        }

        companies.add("Add New Company");

        spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,companies){

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv_spinner = tv;
                    tv.setTextColor(Color.GRAY);
                }
                if(position == companies.size()-1){
                    // Set the hint text color gray
                    tv.setTextColor(Color.RED);
                    tv.setTypeface(null, Typeface.BOLD);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        _list_companiesSpinner.setAdapter(spinnerArrayAdapter);

    }

    /*===========================
      Set listeners to buttons.
    =============================*/
    
    private void setListeners(){

        _list_companiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("Add New Company"))
                {
                    addUserNewCompanyTextDialog();
                }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        _from_dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DialogFragment dialogfragment = new fromDatePickerDialogClass();
                dialogfragment.show(getFragmentManager(), getString(R.string.date_picker_dialog));

            }
        });
        _to_dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DialogFragment dialogfragment = new toDatePickerDialogClass();
                dialogfragment.show(getFragmentManager(), getString(R.string.date_picker_dialog));

            }
        });

        _submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                addUserNewCompany();
            }
        });
    }

    /*==================================================================================================
        Try to add company to users database: check all fields, connectivity and start progress dialog.
    ====================================================================================================*/

    private void addUserNewCompany(){

        String email = user_profile.getEmail();
        String company_name = _list_companiesSpinner.getSelectedItem().toString();
        String from_date = _from_dateButton.getText().toString();
        String to_date = _to_dateButton.getText().toString();
        String date_to_pay = "-";
        String amount = _amountText.getText().toString();
        String paid = _paidCheckbox.isChecked()? "1":"0";

        Bill bill = new Bill("0","0",from_date,to_date,date_to_pay,amount,paid);

        if (!validateFields() ) {
            onInsertBillFailed(getString(R.string.please_fill_all_the_fields));
            return;
        }

        //_submitButton.setEnabled(false);

        progressDialog = new ProgressDialog(insertNewBillActivity.this, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.updateServer));

        //add to server here



    }

    private final void volleyInsertBillRequest(final Bill bill){

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.INSERT_BILL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Dissmiss progress dialog and enable button
                        progressDialog.dismiss();
                        _submitButton.setEnabled(true);

                        //If we are getting success from server
                        response = response.replace("\n", "").replace("\r", "");
                        response = response.replaceAll("\\s","");

                        if(response.equalsIgnoreCase(Configuration.SUCCESS)){
                            onInsertBillSuccess(getString(R.string.newbillwasaddedsucc));
                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            onInsertBillFailed(getString(R.string.faildToAddBill));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Dissmiss progress dialog  and enable button for any problems
                        progressDialog.dismiss();
                        _submitButton.setEnabled(true);

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                //String bill_ID, String company_ID, String from_Date,String to_Date, String date_To_Pay, String amount, String paid) {

                params.put(Configuration.KEY_FROM_DATE, bill.getDate_To_Pay());
                params.put(Configuration.KEY_DATE_TO_PAY,bill.getDate_To_Pay());

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    /*====================================================================
       Implemented if no connection or problems in fields' validation.
    ======================================================================*/

    public void onInsertBillFailed(String msg) {

        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        _submitButton.setEnabled(true);
    }

    /*===========================================================================
       Implemented if connection is okay and no problems in fields' validation.
    =============================================================================*/

    public void onInsertBillSuccess(String msg) {

        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        _submitButton.setEnabled(true);


    }


    /*=====================================
        Validate all the fields on screen.
    =======================================*/

    public boolean validateFields() {

        boolean valid = true;
        String from_date = _from_dateButton.getText().toString();
        String to_date = _to_dateButton.getText().toString();
        String company_spinner = _list_companiesSpinner.getSelectedItem().toString();
        String amount = _amountText.getText().toString();
        String myData = _list_companiesSpinner.getSelectedItem().toString();
        int position = spinnerArrayAdapter.getPosition(myData);

        //check company spinner
        if (company_spinner.equals("Choose Company") ) {
            ((TextView)_list_companiesSpinner.getSelectedView()).setError(getString(R.string.error));

            valid = false;
        } else {
        }

        //check from date button
        if (from_date.equals(getString(R.string.from_date_text) )) {
            _from_dateButton.setError(getString(R.string.notVailddates));
            valid = false;
        } else {
            _from_dateButton.setError(null);
        }

        //check to date button
        if (to_date.equals(getString(R.string.to_date_text)) ) {
            _to_dateButton.setError(getString(R.string.notVailddates));
            valid = false;
        } else {
            _to_dateButton.setError(null);
        }

        //check amount field

        if (amount.isEmpty() || !Configuration.isInteger(amount) ) {
            _amountText.setError(getString(R.string.enteramount));
            valid = false;
        } else {
            if(!(Integer.parseInt(amount) < 10000 && Integer.parseInt(amount) > 0)){
                _amountText.setError(getString(R.string.amountbetween));
                valid = false;
            }
            else {
                _amountText.setError(null);
            }
        }

        return valid;
    }

    /*==========================================
       Add user a new company from the spinner.
    ============================================ */

    public void addUserNewCompanyTextDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter New Company Name");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                //send request to add company
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /*==============================================================================
                        DatePicker dialog classes.
    ================================================================================= */

    public static class fromDatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);

            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){

            Button from_dateButton = (Button)getActivity().findViewById(R.id.date_from_picker_insertbill);
            from_dateButton.setText("  " + (month+1) + " : " + year);


        }
    }

    public static class toDatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);

            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){

            Button to_dateButton = (Button)getActivity().findViewById(R.id.date_to_picker_insertbill);
            to_dateButton.setText("  " + (month+1) + " : " + year);


        }
    }


}
