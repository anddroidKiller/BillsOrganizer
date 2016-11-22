package application.API;

import java.io.Serializable;

/**
 * Created by NivSwisa on 15/09/2016.
 */
public class Date implements Serializable {

    int year;
    int month;
    int day;

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public static final boolean isVaild(Date d1,Date d2){

        if (d1.getMonth() <= d2.getMonth() && d1.getYear() <= d2.getYear() )
            return true;
        else
            return false;
    }

    @Override
    public String toString(){
        return Integer.toString(getMonth()) + " : " + Integer.toString(getYear()) ;
    }

    public void stringToDate(String date){

        date = date.replaceAll("\\s","");
        String[] parts = date.split(":");

        String month_s = parts[0]; // 004
        String year_s = parts[1]; // 034556

        int month = Integer.parseInt(month_s);
        int year = Integer.parseInt(year_s);

        setMonth(month);
        setYear(year);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

}
