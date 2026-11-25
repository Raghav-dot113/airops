package com.airops.main;

import com.airops.auth.AdminLogin;
import com.airops.auth.AuthService;
import com.airops.passenger.PassengerMenu;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AuthService.getInstance().clearSession();

        while (true) {
            System.out.println("\n===== AIROPS =====");
            System.out.println("1. Passenger management");
            System.out.println("2. Admin login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    new PassengerMenu(scanner).showMenu();
                    break;
                case "2":
                    new AdminLogin(scanner).login();
                    break;
                case "3":
                    AuthService.getInstance().clearSession();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
