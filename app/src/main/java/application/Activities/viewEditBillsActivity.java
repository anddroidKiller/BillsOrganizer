package application.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import application.API.Date;
import application.Configuration.ExpandableListAdapter;
import application.API.Company;
import application.Configuration.SpinnerAdapter;
import billsorganizer.billsorganizer.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class viewEditBillsActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener{

    private ArrayList<Company> final_table;
    private ArrayAdapter<String> spinnerAdapter;
    private ExpandableListAdapter companiesAdapter;
    public static Date from_date,to_date;


    @InjectView(R.id.date_from_picker_viewbills) Button _from_dateButton;
    @InjectView(R.id.date_to_picker_viewbills) Button _to_dateButton;
    @InjectView(R.id.submit_viewbills) Button _submitButton;
    @InjectView(R.id.companies_list_viewbills_spinner) Spinner _list_companiesSpinner;
    @InjectView(R.id.bills_table_listview) ExpandableListView show_companiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_bills);
        //Initialize the xml
        ButterKnife.inject(this);

        //grab the table with all bills
        final_table = (ArrayList<Company>) getIntent().getSerializableExtra("user_information");
        //Add all items to table
        if (final_table != null)
            addItemsToTable("Show All Companies");
        //Add companies to spinner
        if  (final_table != null)
            addCompaniesToSpinner(final_table);
        //Add Action Bar
        setActionBar();
        //Add oncklick listeners
        setListeners();
    }

    /* =================================
        Add the items to the list view.
    ==================================== */

    private void addItemsToTable(String company){

            ArrayList<Company> companies = new ArrayList<>();

            for(int i=0; i< final_table.size() ; i++){
                companies.add(final_table.get(i));
            }

            companiesAdapter = new ExpandableListAdapter(this,companies);
            show_companiesList.setAdapter(companiesAdapter);

            for(int i=0; i < companiesAdapter.getGroupCount(); i++)
                show_companiesList.expandGroup(i);

    }

    /*=============================================
        Add companies to spinner from final_table
    =============================================== */

    private void addCompaniesToSpinner(ArrayList<Company> final_table){

        ArrayList<String> companies = new ArrayList<String>();

        companies.add("Show All Companies");

        for (int j=0; j<final_table.size();j++){

            companies.add(final_table.get(j).getCompany_Name());
        }

        spinnerAdapter = new SpinnerAdapter(this,R.layout.spinner_item,companies);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);

        _list_companiesSpinner.setOnItemSelectedListener(this);
        _list_companiesSpinner.setAdapter(spinnerAdapter);
    }

    /*===============================
        When user filter the view
    ================================= */

    private void refreshDisplayList(String company, Date fromdate, Date todate ){

        companiesAdapter.companies.clear();
        companiesAdapter.notifyDataSetChanged();

        //first we add company we want
        switch (company){
            case "Show All Companies":

                for(int i=0; i< final_table.size() ; i++){
                    companiesAdapter.companies.add(final_table.get(i));
                }
                break;
            default:
                for(int i=0; i< final_table.size() ; i++){
                    if(final_table.get(i).getCompany_Name().equals(company)) {
                        companiesAdapter.companies.add(final_table.get(i));
                    }
                }

                break;
        }

        //second we filter the dates we want


        companiesAdapter.notifyDataSetChanged();



    }

    /*=================================================
        Implemented when spinner item is selected.
    ==================================================== */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String company = (String) parent.getItemAtPosition(position);

        refreshDisplayList(company,from_date,to_date);

        for(int i=0; i < companiesAdapter.getGroupCount(); i++)
            show_companiesList.expandGroup(i);
        //
    }

    /*=================================================================
        Implemented when spinner item is opened and nothing changed.
    =================================================================== */

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
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

        _actionBarTitleText.setText(getString(R.string.view_edit_bills));


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

    /*=============================
       Set listeners to buttons
    ===============================*/

    private void setListeners(){

        _from_dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                from_date = new Date(0,0,0);
                DialogFragment dialogfragment = new fromDatePickerDialogClass();
                dialogfragment.show(getFragmentManager(), getString(R.string.date_picker_dialog));

            }
        });
        _to_dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                to_date = new Date(0,0,0);
                DialogFragment dialogfragment = new toDatePickerDialogClass();
                dialogfragment.show(getFragmentManager(), getString(R.string.date_picker_dialog));

            }
        });
        _submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (final_table != null) {

                    if (from_date != null && to_date != null) {
                        //if dates were chosen (not null )  we check their validity :
                        if (Date.isVaild(from_date, to_date)) {
                            refreshDisplayList(spinnerAdapter.getItem(0), from_date, to_date);
                        }
                        else
                        {
                            Toast.makeText(viewEditBillsActivity.this, getString(R.string.notVailddates), Toast.LENGTH_SHORT).show();
                        }
                    }//end if dates != null
                }//end if final_table != null
                else {
                    Toast.makeText(viewEditBillsActivity.this, getString(R.string.nodataavailable), Toast.LENGTH_SHORT).show();
                }
            }//end onClick
        });

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

            from_date = new Date(year,month+1,day);
            Button from_dateButton = (Button)getActivity().findViewById(R.id.date_from_picker_viewbills);
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

            to_date = new Date(year,month+1,day);
            Button to_dateButton = (Button)getActivity().findViewById(R.id.date_to_picker_viewbills);
            to_dateButton.setText("  " + (month+1) + " : " + year);


        }
    }

}
