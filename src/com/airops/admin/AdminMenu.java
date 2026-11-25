package com.airops.admin;

import com.airops.auth.AuthService;
import com.airops.exceptions.AccessDeniedException;
import com.airops.seat.Seat;
import com.airops.seat.SeatService;

import java.sql.SQLException;

import java.util.Scanner;

public class AdminMenu {

    private final AuthService authService;
    private final Scanner scanner;
    private final SeatService seatService;

    public AdminMenu(Scanner scanner) {
        this.authService = AuthService.getInstance();
        this.scanner = scanner;
        this.seatService = new SeatService();
    }

    public void showMenu() {
        try {
            authService.requireAdmin();
        } catch (AccessDeniedException e) {
            System.out.println("Access denied");
            return;
        }

        while (true) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. Flight management");
            System.out.println("2. Gate management");
            System.out.println("3. Seat management");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            if ("4".equals(choice)) {
                authService.clearSession();
                System.out.println("Logged out.");
                return;
            }

            try {
                authService.requireAdmin();
                if ("1".equals(choice) || "2".equals(choice)) {
                    System.out.println("This management module has no actions implemented yet.");
                } else if ("3".equals(choice)) {
                    showSeatMenu();
                } else {
                    System.out.println("Invalid choice.");
                }
            } catch (AccessDeniedException e) {
                System.out.println("Access denied");
                return;
            }
        }
    }

    /** Guard for any admin-only action added to this menu. */
    public void runAdminAction(Runnable action) {
        try {
            authService.requireAdmin();
            action.run();
        } catch (AccessDeniedException e) {
            System.out.println("Access denied");
        }
    }

    private void showSeatMenu() {
        while (true) {
            try {
                authService.requireAdmin();
            } catch (AccessDeniedException e) {
                System.out.println("Access denied");
                return;
            }

            System.out.println("\n===== SEAT MANAGEMENT =====");
            System.out.println("1. Add seat");
            System.out.println("2. Update seat status");
            System.out.println("3. Back");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();
            if ("3".equals(choice)) {
                return;
            }

            try {
                if ("1".equals(choice)) {
                    addSeat();
                } else if ("2".equals(choice)) {
                    updateSeatStatus();
                } else {
                    System.out.println("Invalid choice.");
                }
            } catch (AccessDeniedException e) {
                System.out.println("Access denied");
                return;
            } catch (SQLException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void addSeat() throws SQLException, AccessDeniedException {
        authService.requireAdmin();
        System.out.print("Flight ID: ");
        int flightId = Integer.parseInt(scanner.nextLine());
        System.out.print("Seat number: ");
        String seatNumber = scanner.nextLine();
        if (seatService.addSeat(new Seat(flightId, seatNumber))) {
            System.out.println("Seat added.");
        } else {
            System.out.println("Seat was not added.");
        }
    }

    private void updateSeatStatus() throws SQLException, AccessDeniedException {
        authService.requireAdmin();
        System.out.print("Seat ID: ");
        int seatId = Integer.parseInt(scanner.nextLine());
        System.out.print("Status (AVAILABLE or BOOKED): ");
        String status = scanner.nextLine().trim().toUpperCase();
        if (seatService.updateSeatStatus(seatId, status)) {
            System.out.println("Seat updated.");
        } else {
            System.out.println("Seat was not found.");
        }
    }
}
