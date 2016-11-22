package application.API;

import java.io.Serializable;
import java.util.ArrayList;

public class Company implements Serializable{

    private String Company_ID;
    private String 	Company_Name;

    public  ArrayList<Bill> children = new ArrayList<>();


    public Company(String company_ID, String company_Name) {
        Company_ID = company_ID;
        Company_Name = company_Name;
    }

    public ArrayList<Bill> getChildren() {
        return children;
    }

    public String getCompany_ID() {
        return Company_ID;
    }

    public void setCompany_ID(String company_ID) {
        Company_ID = company_ID;
    }

    public String getCompany_Name() {
        return Company_Name;
    }

    public void setCompany_Name(String company_Name) {
        Company_Name = company_Name;
    }
}