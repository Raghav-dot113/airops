package com.airops.auth;

public class Admin extends User {

    public Admin(int userId, String username) {
        super(userId, username, "ADMIN");
    }
}
