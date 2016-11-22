package application.API;

import java.io.Serializable;

/**
 * Created by NivSwisa on 12/09/2016.
 */
public class User implements Serializable{
    private String Email;
    private String Password;
    private String Firstname;
    private String Surename;

    public User(String email, String password, String firstname, String surename) {
        Email = email;
        Password = password;
        Firstname = firstname;
        Surename = surename;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getSurename() {
        return Surename;
    }

    public void setSurename(String surename) {
        Surename = surename;
    }


}
