package application.Configuration;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

import billsorganizer.billsorganizer.R;

/**
 * Created by NivSwisa on 24/08/2016.
 */
public class Configuration {

    /*==================
       Static variables
    =====================*/

    public static final String HOST = "http://easyvisa2usa.com/";
    public static final String FILES_DIR = "BillsOrganizer/";

    //URLs to files
    public static final String LOGIN_URL = HOST +  FILES_DIR + "Login.php";
    public static final String SIGNUP_URL = HOST +  FILES_DIR + "Signup.php";
    public static final String GET_USER_PROFILE = HOST +  FILES_DIR + "GetUserProfile.php";
    public static final String GET_USER_COMPANIES = HOST +  FILES_DIR + "GetUserCompanies.php";
    public static final String GET_USER_BILLS = HOST +  FILES_DIR + "GetUserBills.php";
    public static final String INSERT_BILL = HOST +  FILES_DIR + "InsertNewBill.php";;


    //Keys to send for database (POST)
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_PASSWORD = "Password";
    public static final String KEY_FIRSTNAME = "Firstname";
    public static final String KEY_SURENAME = "Surename";
    public static final String KEY_FROM_DATE = "From_Date";
    public static final String KEY_TO_DATE = "To_Date";
    public static final String KEY_DATE_TO_PAY = "Date_To_Pay";
    public static final String KEY_AMOUNT = "Amount";
    public static final String KEY_PAID = "Paid";


    //Keys returns from database
    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    //Keys for Shared Preferences
    public static final String PREF = "UserPreferencesBillsOrganizer";


    /*==============================================================================
                        Check if a string is a vaild integer
    ================================================================================= */

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }



}
