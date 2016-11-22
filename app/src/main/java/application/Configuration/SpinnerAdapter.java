package application.Configuration;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import application.API.Company;

/**
 * Created by NivSwisa on 14/09/2016.
 */

public class SpinnerAdapter extends ArrayAdapter<String>   {

    private Context context;
    private ArrayList<String> companies;
    private ArrayList<Company> final_table;


    public SpinnerAdapter(Context context, int resource, ArrayList<String> companies ){
        super(context,resource,companies);

        this.context = context;
        this.companies = companies;
    }

    @Override
    public boolean isEnabled(int position){
        return true;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        if(position == 0){
            // Set the hint text color gray
            tv.setTextColor(Color.GRAY);
        }
        else {
            tv.setTextColor(Color.BLACK);
        }
        return view;
    }




}
