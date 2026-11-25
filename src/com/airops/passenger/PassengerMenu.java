
package com.airops.passenger;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class PassengerMenu {

    private final PassengerService passengerService;
    private final Scanner scanner;

    public PassengerMenu(Scanner scanner) {
        this.passengerService = new PassengerService();
        this.scanner = scanner;
    }

    public void showMenu() {

        while (true) {

            System.out.println("\n===== PASSENGER MANAGEMENT =====");
            System.out.println("1. Register Passenger");
            System.out.println("2. Search Passenger");
            System.out.println("3. Display All Passengers");
            System.out.println("4. Update Passenger");
            System.out.println("5. Delete Passenger");
            System.out.println("6. Back");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {

                    case "1":
                        registerPassenger();
                        break;

                    case "2":
                        searchPassenger();
                        break;

                    case "3":
                        displayAllPassengers();
                        break;

                    case "4":
                        updatePassenger();
                        break;

                    case "5":
                        deletePassenger();
                        break;

                    case "6":
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }

            } catch (SQLException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void registerPassenger() throws SQLException {

        System.out.print("Enter full name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();

        Passenger passenger = new Passenger(name, email, phone);

        if (passengerService.registerPassenger(passenger)) {
            System.out.println("Passenger registered successfully!");
            System.out.println("Passenger ID: " + passenger.getPassengerId());
        }
    }

    private void searchPassenger() throws SQLException {

        System.out.print("Enter passenger ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        Passenger passenger = passengerService.findPassenger(id);

        if (passenger != null) {
            System.out.println(passenger);
        } else {
            System.out.println("Passenger not found!");
        }
    }

    private void displayAllPassengers() throws SQLException {

        List<Passenger> passengers = passengerService.getAllPassengers();

        if (passengers.isEmpty()) {
            System.out.println("No passengers found!");
            return;
        }

        for (Passenger passenger : passengers) {
            System.out.println("\n--------------------");
            System.out.println(passenger);
        }
    }

    private void updatePassenger() throws SQLException {

        System.out.print("Enter passenger ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        Passenger passenger = passengerService.findPassenger(id);

        if (passenger == null) {
            System.out.println("Passenger not found!");
            return;
        }

        System.out.print("Enter new name: ");
        passenger.setFullName(scanner.nextLine());

        System.out.print("Enter new email: ");
        passenger.setEmail(scanner.nextLine());

        System.out.print("Enter new phone: ");
        passenger.setPhone(scanner.nextLine());

        if (passengerService.updatePassenger(passenger)) {
            System.out.println("Passenger updated successfully!");
        } else {
            System.out.println("Passenger update failed!");
        }
    }

    private void deletePassenger() throws SQLException {

        System.out.print("Enter passenger ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        if (passengerService.deletePassenger(id)) {
            System.out.println("Passenger deleted successfully!");
        } else {
            System.out.println("Passenger not found!");
        }
    }
}
