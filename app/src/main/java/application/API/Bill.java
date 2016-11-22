package application.API;

import java.io.Serializable;

/**
 * The Menu item class.
 * this class presents the all the properties of one meal (mealName,description,price,type)
 */
public class Bill implements Serializable
{
    private String Bill_ID;
    private String Company_ID;
    private String From_Date;
    private Date From_Date_date;
    private String To_Date;
    private Date To_Date_date;
    private String Date_To_Pay;
    private Date Date_To_Pay_date;
    private String Amount;
    private String Paid;

    public Bill(String bill_ID, String company_ID, String from_Date,String to_Date, String date_To_Pay, String amount, String paid) {

        Bill_ID = bill_ID;
        Company_ID = company_ID;
        From_Date = from_Date;
        To_Date = to_Date;
        Date_To_Pay = date_To_Pay;
        Amount = amount;
        Paid = paid;

        this.From_Date_date = setDateInstance(from_Date);
        this.To_Date_date = setDateInstance(to_Date);

    }

    public Date getFrom_Date_date() {
        return From_Date_date;
    }

    public void setFrom_Date_date(Date from_Date_date) {
        From_Date_date = from_Date_date;
    }

    public Date getTo_Date_date() {
        return To_Date_date;
    }

    public void setTo_Date_date(Date to_Date_date) {
        To_Date_date = to_Date_date;
    }

    public Date getDate_To_Pay_date() {
        return Date_To_Pay_date;
    }

    public void setDate_To_Pay_date(Date date_To_Pay_date) {
        Date_To_Pay_date = date_To_Pay_date;
    }

    public Date setDateInstance(String date){

        date = date.replaceAll("\\s","");
        String[] parts = date.split(":");

        String month_s = parts[0]; // 004
        String year_s = parts[1]; // 034556

        int month = Integer.parseInt(month_s);
        int year = Integer.parseInt(year_s);

        return new Date(year,month,0);
    }

    public String getBill_ID() {
        return Bill_ID;
    }

    public void setBill_ID(String bill_ID) {
        Bill_ID = bill_ID;
    }

    public String getFrom_Date() {
        return From_Date;
    }

    public void setFrom_Date(String from_Date) {
        From_Date = from_Date;
    }

    public String getCompany_ID() {
        return Company_ID;
    }

    public void setCompany_ID(String company_ID) {
        Company_ID = company_ID;
    }

    public String getTo_Date() {
        return To_Date;
    }

    public void setTo_Date(String to_Date) {
        To_Date = to_Date;
    }

    public String getDate_To_Pay() {
        return Date_To_Pay;
    }

    public void setDate_To_Pay(String date_To_Pay) {
        Date_To_Pay = date_To_Pay;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getPaid() {
        return Paid;
    }

    public void setPaid(String paid) {
        Paid = paid;
    }


}
