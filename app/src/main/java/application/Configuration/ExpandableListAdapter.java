package application.Configuration;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import java.io.Serializable;
import java.util.ArrayList;
import application.API.Bill;
import application.API.Company;
import billsorganizer.billsorganizer.R;


public class ExpandableListAdapter extends BaseExpandableListAdapter implements Serializable {

    public ArrayList<Company> companies;
    public LayoutInflater inflater;
    public Activity activity;


    public ExpandableListAdapter(Activity act, ArrayList<Company> companies) {
        activity = act;
        this.companies = companies;
        inflater = act.getLayoutInflater();

    }

    @Override
    public Object getChild(int companyPosition, int childPosition) {
        return (companies.get(companyPosition)).children.get(childPosition);
    }

    @Override
    public long getChildId(int companyPosition, int billPosition) {
        return 0;
    }

    @Override
    public View getChildView(int companyPosition, final int billPosition, boolean isLastBill, View convertView, ViewGroup parent) {

        final Bill child = (Bill) getChild(companyPosition, billPosition);
        final Company company = (Company) getGroup(companyPosition);

        TextView comany_name_textview;
        TextView from_date_textview;
        TextView amount_textview;
        TextView paid_textview;


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }

        comany_name_textview = (TextView) convertView.findViewById(R.id.comany_name_textview);
        from_date_textview = (TextView) convertView.findViewById(R.id.from_date_textview);
        amount_textview = (TextView) convertView.findViewById(R.id.amount_textview);
        paid_textview = (TextView) convertView.findViewById(R.id.paid_textview);

        comany_name_textview.setText(company.getCompany_Name());
        from_date_textview.setText(child.getFrom_Date());
        amount_textview.setText("₪" + child.getAmount() );

        if (child.getPaid().equals("0")){
            paid_textview.setTextColor(Color.RED);
            paid_textview.setText(activity.getString(R.string.notpaid));
        }
        else {
            paid_textview.setTextColor(Color.GREEN);
            paid_textview.setText(activity.getString(R.string.paid));
        }


        return convertView;
    }

    @Override
    public int getChildrenCount(int companyPosition) {
        return companies.get(companyPosition).children.size();
    }

    @Override
    public Object getGroup(int companyPosition) {
        return companies.get(companyPosition);
    }

    @Override
    public int getGroupCount() {
        return companies.size();
    }

    @Override
    public void onGroupCollapsed(int companyPosition) {
        super.onGroupCollapsed(companyPosition);
    }

    @Override
    public void onGroupExpanded(int companyPosition) {
        super.onGroupExpanded(companyPosition);
    }

    @Override
    public long getGroupId(int companyPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int companyPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }//group description
        Company company = (Company) getGroup(companyPosition);

        TextView con;

        con = (TextView) convertView.findViewById(R.id.groupdesc);
        con.setText(company.getCompany_Name());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int companyPosition, int billPosition) {
        return false;
    }


}