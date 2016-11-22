package application.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import application.API.Bill;
import application.API.Company;
import application.API.Date;
import application.Configuration.SpinnerAdapter;
import billsorganizer.billsorganizer.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class viewReportsActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    public static Date from_date,to_date;
    private ArrayList<Company> final_table;
    private ArrayAdapter<String> spinnerAdapter;
    private RadioButton radioButton;

    @InjectView(R.id.companies_list_viewreports_spinner) Spinner _list_companiesSpinner;
    @InjectView(R.id.date_from_picker_viewreports) Button _from_dateButton;
    @InjectView(R.id.date_to_picker_viewreports) Button _to_dateButton;
    @InjectView(R.id.radioGroupTables) RadioGroup radioGroup;
    @InjectView(R.id.submit_viewreports) Button _submitButton;
    @InjectView(R.id.chart1) BarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);

        //Initialize the xml
        ButterKnife.inject(this);
        //set the action bar
        setActionBar();
        //Add oncklick listeners
        setListeners();

        //grab the table with all bills
        final_table = (ArrayList<Company>) getIntent().getSerializableExtra("user_information");
        //Add companies to spinner
        if  (final_table != null){

            addCompaniesToSpinner(final_table);

            Calendar calendar = Calendar.getInstance(); // GET CURRENT YEAR
            int year = calendar.get(Calendar.YEAR);
            _from_dateButton.setText("01 : " + year);
            _to_dateButton.setText("12 : " + year);
            setChart(getUserEntries(getBillsFromYearAndCompany(year,final_table.get(0).getCompany_Name()))) ;
        }

    }

    /*=============================================
        Add companies to spinner from final_table
    =============================================== */

    private void addCompaniesToSpinner(ArrayList<Company> final_table){

        ArrayList<String> companies = new ArrayList<String>();

        for (int j=0; j<final_table.size();j++){

            companies.add(final_table.get(j).getCompany_Name());
        }

        spinnerAdapter = new SpinnerAdapter(this,R.layout.spinner_item,companies);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);

        _list_companiesSpinner.setOnItemSelectedListener(this);
        _list_companiesSpinner.setAdapter(spinnerAdapter);
    }

    /*=================================================
        Implemented when spinner item is selected.
    ==================================================== */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        parent.getSelectedItem();
        //
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

        _actionBarTitleText.setText(getString(R.string.view_reports));


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
       Set listeners to buttons.
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

                if (final_table !=null ) {

                    Date from_date_date = new Date(0, 0, 0);
                    Date to_date_date = new Date(0, 0, 0);

                    String from_date = _from_dateButton.getText().toString();
                    String to_date = _to_dateButton.getText().toString();

                    from_date_date.stringToDate(from_date);
                    to_date_date.stringToDate(to_date);

                    if (from_date_date.getYear() != to_date_date.getYear()){
                        Toast.makeText(viewReportsActivity.this, getString(R.string.annualalowed), Toast.LENGTH_SHORT).show();
                    }
                    else
                        setChart(getUserEntries(getBillsFromDateAndCompany(from_date_date, to_date_date,
                                _list_companiesSpinner.getSelectedItem().toString())));
                }else
                    Toast.makeText(viewReportsActivity.this, getString(R.string.nodataavailable), Toast.LENGTH_SHORT).show();
            }//end onClick
        });
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedId);

                if (R.id.chartRadioButton == selectedId){
                    mChart.setVisibility(View.VISIBLE);

                }
                else if(R.id.graphRadioButton == selectedId){
                    mChart.setVisibility(View.GONE);

                }

            }
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
            Button from_dateButton = (Button)getActivity().findViewById(R.id.date_from_picker_viewreports);
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
            Button to_dateButton = (Button)getActivity().findViewById(R.id.date_to_picker_viewreports);
            to_dateButton.setText("  " + (month+1) + " : " + year);


        }
    }


    /*==================================================
       Returns all the bills from company and full date
    ==================================================== */

    private List<Bill> getBillsFromDateAndCompany(Date from_date,Date to_date,String company){

        List<Bill> bills = new ArrayList<>();

        for(int i=0; i < final_table.size(); i++) {

            if (final_table.get(i).getCompany_Name().equals(company)) {

                for (int j = 0; j < final_table.get(i).children.size(); j++) {

                    Bill bill = final_table.get(i).children.get(j);

                    if (isDateBetween(bill.getFrom_Date_date(),from_date,to_date)) {
                        bills.add(bill);
                    }

                }
            }
        }


        return bills;
    }

    /*====================================================================================
       Checks if the billdate is in range between the dates: true if it is.
    ====================================================================================== */

    private boolean isDateBetween(Date billdate,Date from,Date to){

        boolean answer = false;

        int billdate_year = billdate.getYear();
        int billdate_month = billdate.getMonth();

        int from_year = from.getYear();
        int from_month = from.getMonth();

        int to_year = to.getYear();
        int to_month = to.getMonth();

        if ( ((billdate_year == from_year) && (billdate_month == from_month)  ) ||
                ((billdate_year == to_year) && (billdate_month == to_month)) ) {
            answer = true;
        }
        else if ( (billdate_year >= from_year) && (billdate_year <= to_year) ) {
            if (  (billdate_month >= from_month) && (billdate_month <= to_month)) {
                answer = true;
            }
        }


        return answer;


    }

    /*==============================================
       Returns all the bills from company and year
    =================================================== */

    private List<Bill> getBillsFromYearAndCompany(int year,String company){

        List<Bill> bills = new ArrayList<>();

        for(int i=0; i < final_table.size(); i++){

            if (final_table.get(i).getCompany_Name().equals(company)) {

                for (int j = 0; j < final_table.get(i).children.size(); j++) {

                    Bill bill = final_table.get(i).children.get(j);

                    if (bill.getFrom_Date_date().getYear() == year) {
                        bills.add(bill);
                    }
                }
            }
        }

            return bills;
    }

    /*==============================================================================
       Returns the entries for the chart by given a list of bills from company
    ================================================================================= */

    private List<BarEntry> getUserEntries(List<Bill> bills){

        List<BarEntry> entries = new ArrayList<>();
        int cnt = 0;

        float[] month_amounts = {0,0,0,0,0,0,0,0,0,0,0,0};

        //pass all the month
            for ( int j = 0 ; j < bills.size() ; j++){

                  int month = bills.get(j).getFrom_Date_date().getMonth();
                  float amount = Float.parseFloat( bills.get(j).getAmount() );

                    month_amounts[month-1] = amount;
            }

        for ( int j = 1 ; j <= 12 ; j++){
            entries.add(new BarEntry(j, month_amounts[j-1]));
        }

        return entries;
    }

    /*==============================================================================
       get the entries and set all the settings for the chart.
    ================================================================================= */

    private void setChart(List<BarEntry> entries){

        mChart.setPinchZoom(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);
        mChart.setDescription("");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        mChart.getAxisLeft().setDrawGridLines(false);


        // add a nice and smooth animation
        mChart.animateY(2500);
        mChart.getLegend().setEnabled(false);


        // mChart.getXAxis().setValueFormatter(new XAxisValueFormatter() {

        //     @Override
        //     public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
        //         //return number + "h" here
        //         // but you can do everything you want here. The string returned will be displayed on chart x label
        //         return original + "h";
        //     }
        // });


        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        BarData data = new BarData(set);

        data.setBarWidth(0.9f); // set custom bar width
        mChart.setData(data);
        mChart.setFitBars(true); // make the x-axis fit exactly all bars
        mChart.invalidate(); // refresh
    }


}
