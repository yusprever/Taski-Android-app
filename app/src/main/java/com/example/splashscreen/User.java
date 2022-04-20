package com.example.splashscreen;

public class User {
    public String dob,username,email;
    public String password,comfirmationpassword;
    private String UserID;

    public User(String dob, String username, String email, String password, String comfirmationpassword) {

        this.comfirmationpassword = comfirmationpassword;
        this.password = password;
        this.username = username;
        this.email = email;
        this.dob = dob;
    }

    // The user constructor // ********
    public User(String username, String userID) {
        this.UserID = userID;
        this.username = username;
    }



    public User() {
    }

    // getters and setters for the name and ID // ********
    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUsername() {
        return username;
    }

    public String getUserID() {
        return UserID;
    }


}
