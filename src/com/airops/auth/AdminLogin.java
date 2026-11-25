package com.airops.auth;

import com.airops.admin.AdminMenu;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;

public class AdminLogin {

    private final AuthService authService;
    private final Scanner scanner;

    public AdminLogin(Scanner scanner) {
        this.authService = AuthService.getInstance();
        this.scanner = scanner;
    }

    public boolean login() {
        System.out.print("Admin username: ");
        String username = scanner.nextLine();
        System.out.print("Admin password: ");
        char[] password = scanner.nextLine().toCharArray();

        try {
            User user = authService.authenticate(username, password);
            if (user == null) {
                System.out.println("Wrong username or password.");
                return false;
            }
            if (!user.isAdmin()) {
                authService.clearSession();
                System.out.println("Access denied: this account is not an admin.");
                return false;
            }

            System.out.println("Welcome, " + user.getUsername() + ".");
            new AdminMenu(scanner).showMenu();
            return true;
        } catch (SQLException e) {
            Arrays.fill(password, '\0');
            System.out.println("Login failed: database unavailable.");
            return false;
        }
    }
}
