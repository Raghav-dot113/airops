package com.airops.flight;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console interface for the Flight Management Module.
 * It only handles input/output and calls FlightService for the real work.
 */
public class FlightMenu {

    private final FlightService flightService;
    private final Scanner scanner;

    public FlightMenu(Scanner scanner) {
        this.flightService = new FlightService();
        this.scanner = scanner;
    }

    // Main menu loop; runs until the admin chooses to go back
    public void showMenu() {

        while (true) {

            System.out.println("\n===== FLIGHT MANAGEMENT =====");
            System.out.println("1. Add New Flight");
            System.out.println("2. Update Flight Details");
            System.out.println("3. Delete Flight");
            System.out.println("4. View All Flights");
            System.out.println("5. Search Flight by Flight Number");
            System.out.println("6. Search Flights by Route");
            System.out.println("7. Update Flight Status");
            System.out.println("8. Manage Flight Delay");
            System.out.println("9. Change Flight Gate");
            System.out.println("10. Back to Admin Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {

                    case "1":
                        addFlight();
                        break;

                    case "2":
                        updateFlight();
                        break;

                    case "3":
                        deleteFlight();
                        break;

                    case "4":
                        viewAllFlights();
                        break;

                    case "5":
                        searchFlightByNumber();
                        break;

                    case "6":
                        searchFlightsByRoute();
                        break;

                    case "7":
                        updateFlightStatus();
                        break;

                    case "8":
                        manageFlightDelay();
                        break;

                    case "9":
                        changeFlightGate();
                        break;

                    case "10":
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }

            } catch (SQLException | IllegalArgumentException | DateTimeParseException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // Option 1: collect details and create a new flight
    private void addFlight() throws SQLException {

        System.out.print("Enter flight number: ");
        String flightNumber = scanner.nextLine();

        System.out.print("Enter airline name: ");
        String airlineName = scanner.nextLine();

        System.out.print("Enter source: ");
        String source = scanner.nextLine();

        System.out.print("Enter destination: ");
        String destination = scanner.nextLine();

        LocalDateTime departure = readDateTime("Enter departure time (yyyy-MM-ddTHH:mm): ");
        LocalDateTime arrival = readDateTime("Enter arrival time (yyyy-MM-ddTHH:mm): ");

        System.out.print("Enter gate number (leave blank if not assigned): ");
        String gate = scanner.nextLine();

        Flight flight = new Flight(flightNumber, airlineName, source, destination, departure, arrival);
        flight.setGateNumber(gate.trim().isEmpty() ? null : gate.trim());

        if (flightService.addFlight(flight)) {
            System.out.println("Flight added successfully!");
            System.out.println("Flight ID: " + flight.getFlightId());
        } else {
            System.out.println("Failed to add flight!");
        }
    }

    // Option 2: edit the details of an existing flight
    private void updateFlight() throws SQLException {

        System.out.print("Enter flight ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());

        Flight flight = flightService.findFlightById(id);

        if (flight == null) {
            System.out.println("Flight not found!");
            return;
        }

        System.out.println("Leave a field blank to keep its current value.");
        flight.displayFlightDetails();

        System.out.print("New flight number [" + flight.getFlightNumber() + "]: ");
        String input = scanner.nextLine();
        if (!input.trim().isEmpty()) {
            flight.setFlightNumber(input.trim());
        }

        System.out.print("New airline name [" + flight.getAirlineName() + "]: ");
        input = scanner.nextLine();
        if (!input.trim().isEmpty()) {
            flight.setAirlineName(input.trim());
        }

        System.out.print("New source [" + flight.getSource() + "]: ");
        input = scanner.nextLine();
        if (!input.trim().isEmpty()) {
            flight.setSource(input.trim());
        }

        System.out.print("New destination [" + flight.getDestination() + "]: ");
        input = scanner.nextLine();
        if (!input.trim().isEmpty()) {
            flight.setDestination(input.trim());
        }

        System.out.print("New departure time [" + flight.getDepartureTime() + "] (yyyy-MM-ddTHH:mm): ");
        input = scanner.nextLine();
        if (!input.trim().isEmpty()) {
            flight.setDepartureTime(LocalDateTime.parse(input.trim()));
        }

        System.out.print("New arrival time [" + flight.getArrivalTime() + "] (yyyy-MM-ddTHH:mm): ");
        input = scanner.nextLine();
        if (!input.trim().isEmpty()) {
            flight.setArrivalTime(LocalDateTime.parse(input.trim()));
        }

        System.out.print("New gate number [" + flight.getGateNumber() + "]: ");
        input = scanner.nextLine();
        if (!input.trim().isEmpty()) {
            flight.setGateNumber(input.trim());
        }

        if (flightService.updateFlight(flight)) {
            System.out.println("Flight updated successfully!");
        } else {
            System.out.println("Flight update failed!");
        }
    }

    // Option 3: remove a flight
    private void deleteFlight() throws SQLException {

        System.out.print("Enter flight ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        if (flightService.deleteFlight(id)) {
            System.out.println("Flight deleted successfully!");
        } else {
            System.out.println("Flight not found!");
        }
    }

    // Option 4: list every flight
    private void viewAllFlights() throws SQLException {

        List<Flight> flights = flightService.viewAllFlights();

        if (flights.isEmpty()) {
            System.out.println("No flights found!");
            return;
        }

        for (Flight flight : flights) {
            flight.displayFlightDetails();
        }
    }

    // Option 5: find one flight by its number
    private void searchFlightByNumber() throws SQLException {

        System.out.print("Enter flight number: ");
        String flightNumber = scanner.nextLine();

        Flight flight = flightService.searchFlight(flightNumber);

        if (flight != null) {
            flight.displayFlightDetails();
        } else {
            System.out.println("Flight not found!");
        }
    }

    // Option 6: find flights on a route
    private void searchFlightsByRoute() throws SQLException {

        System.out.print("Enter source: ");
        String source = scanner.nextLine();

        System.out.print("Enter destination: ");
        String destination = scanner.nextLine();

        List<Flight> flights = flightService.searchFlightsByRoute(source, destination);

        if (flights.isEmpty()) {
            System.out.println("No flights found on this route!");
            return;
        }

        for (Flight flight : flights) {
            flight.displayFlightDetails();
        }
    }

    // Option 7: change the status of a flight
    private void updateFlightStatus() throws SQLException {

        System.out.print("Enter flight ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.println("Valid statuses: SCHEDULED, DELAYED, BOARDING, DEPARTED, ARRIVED, CANCELLED");
        System.out.print("Enter new status: ");
        String status = scanner.nextLine();

        if (flightService.changeFlightStatus(id, status)) {
            System.out.println("Flight status updated successfully!");
        } else {
            System.out.println("Failed to update status!");
        }
    }

    // Option 8: mark a flight as delayed
    private void manageFlightDelay() throws SQLException {

        System.out.print("Enter flight ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter delay minutes: ");
        int minutes = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter delay reason: ");
        String reason = scanner.nextLine();

        if (flightService.markFlightDelayed(id, minutes, reason)) {
            System.out.println("Flight marked as delayed!");
        } else {
            System.out.println("Failed to update delay!");
        }
    }

    // Option 9: assign or change the gate
    private void changeFlightGate() throws SQLException {

        System.out.print("Enter flight ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter new gate number: ");
        String gate = scanner.nextLine();

        if (flightService.changeFlightGate(id, gate)) {
            System.out.println("Gate updated successfully!");
        } else {
            System.out.println("Failed to update gate!");
        }
    }

    // Small helper to read a date/time value safely
    private LocalDateTime readDateTime(String prompt) {

        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine();
            try {
                return LocalDateTime.parse(value.trim());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Please use yyyy-MM-ddTHH:mm (e.g. 2026-01-15T09:30).");
            }
        }
    }
}