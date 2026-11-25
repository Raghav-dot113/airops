package com.airops.flight;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Business logic layer for flight management.
 * It validates input, enforces rules, and then delegates
 * the actual database work to FlightDAO.
 */
public class FlightService {

    private final FlightDAO flightDAO;

    // All status values the system accepts
    private static final Set<String> VALID_STATUSES = Set.of(
            "SCHEDULED", "DELAYED", "BOARDING",
            "DEPARTED", "ARRIVED", "CANCELLED"
    );

    public FlightService() {
        this.flightDAO = new FlightDAO();
    }

    // Create a new flight after validating the data
    public boolean addFlight(Flight flight) throws SQLException {

        validateFlight(flight);

        // Flight number must be unique
        if (flightDAO.searchFlightByNumber(flight.getFlightNumber()) != null) {
            throw new IllegalArgumentException("Flight number already exists!");
        }

        return flightDAO.addFlight(flight);
    }

    // Update an existing flight after validating the data
    public boolean updateFlight(Flight flight) throws SQLException {

        validateFlight(flight);

        if (flight.getFlightId() <= 0) {
            throw new IllegalArgumentException("Invalid flight ID!");
        }

        // Flight must exist before updating
        if (!flightDAO.isFlightExists(flight.getFlightId())) {
            throw new IllegalArgumentException("Flight not found!");
        }

        // Flight number must stay unique (ignore the current record itself)
        Flight existing = flightDAO.searchFlightByNumber(flight.getFlightNumber());
        if (existing != null && existing.getFlightId() != flight.getFlightId()) {
            throw new IllegalArgumentException("Flight number is used by another flight!");
        }

        return flightDAO.updateFlight(flight);
    }

    // Delete a flight, but only if it exists
    public boolean deleteFlight(int flightId) throws SQLException {

        if (flightId <= 0) {
            throw new IllegalArgumentException("Invalid flight ID!");
        }

        if (!flightDAO.isFlightExists(flightId)) {
            throw new IllegalArgumentException("Flight not found!");
        }

        return flightDAO.deleteFlight(flightId);
    }

    // Return every flight in the system
    public List<Flight> viewAllFlights() throws SQLException {
        return flightDAO.getAllFlights();
    }

    // Find a single flight by its ID
    public Flight findFlightById(int flightId) throws SQLException {

        if (flightId <= 0) {
            throw new IllegalArgumentException("Invalid flight ID!");
        }

        return flightDAO.getFlightById(flightId);
    }

    // Search a flight by its flight number
    public Flight searchFlight(String flightNumber) throws SQLException {

        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Flight number cannot be empty!");
        }

        return flightDAO.searchFlightByNumber(flightNumber.trim());
    }

    // Search flights by source and destination route
    public List<Flight> searchFlightsByRoute(String source, String destination) throws SQLException {

        if (source == null || source.trim().isEmpty()
                || destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("Source and destination cannot be empty!");
        }

        if (source.trim().equalsIgnoreCase(destination.trim())) {
            throw new IllegalArgumentException("Source and destination must be different!");
        }

        return flightDAO.searchFlightsByRoute(source.trim(), destination.trim());
    }

    // Change the status of a flight, respecting valid transitions
    public boolean changeFlightStatus(int flightId, String status) throws SQLException {

        if (flightId <= 0) {
            throw new IllegalArgumentException("Invalid flight ID!");
        }

        if (status == null || !VALID_STATUSES.contains(status.trim().toUpperCase())) {
            throw new IllegalArgumentException("Invalid flight status!");
        }

        String newStatus = status.trim().toUpperCase();

        Flight flight = flightDAO.getFlightById(flightId);
        if (flight == null) {
            throw new IllegalArgumentException("Flight not found!");
        }

        validateStatusTransition(flight.getStatus(), newStatus);

        return flightDAO.updateFlightStatus(flightId, newStatus);
    }

    // Mark a flight as delayed with a reason and delay duration
    public boolean markFlightDelayed(int flightId, int delayMinutes, String reason) throws SQLException {

        if (flightId <= 0) {
            throw new IllegalArgumentException("Invalid flight ID!");
        }

        if (delayMinutes < 0) {
            throw new IllegalArgumentException("Delay minutes cannot be negative!");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Delay reason cannot be empty!");
        }

        Flight flight = flightDAO.getFlightById(flightId);
        if (flight == null) {
            throw new IllegalArgumentException("Flight not found!");
        }

        // A flight that already departed, arrived or was cancelled cannot be delayed
        String current = flight.getStatus();
        if (current.equals("DEPARTED") || current.equals("ARRIVED")
                || current.equals("CANCELLED")) {
            throw new IllegalArgumentException(
                    "A " + current + " flight cannot be marked as delayed!");
        }

        return flightDAO.updateFlightDelay(flightId, delayMinutes, reason.trim());
    }

    // Change the gate number of a flight
    public boolean changeFlightGate(int flightId, String gateNumber) throws SQLException {

        if (flightId <= 0) {
            throw new IllegalArgumentException("Invalid flight ID!");
        }

        if (gateNumber == null || gateNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Gate number cannot be empty!");
        }

        if (!flightDAO.isFlightExists(flightId)) {
            throw new IllegalArgumentException("Flight not found!");
        }

        return flightDAO.updateFlightGate(flightId, gateNumber.trim());
    }

    // Validate all the core fields of a flight
    private void validateFlight(Flight flight) {

        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be empty!");
        }

        if (flight.getFlightNumber() == null || flight.getFlightNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Flight number cannot be empty!");
        }

        if (flight.getAirlineName() == null || flight.getAirlineName().trim().isEmpty()) {
            throw new IllegalArgumentException("Airline name cannot be empty!");
        }

        if (flight.getSource() == null || flight.getSource().trim().isEmpty()) {
            throw new IllegalArgumentException("Source cannot be empty!");
        }

        if (flight.getDestination() == null || flight.getDestination().trim().isEmpty()) {
            throw new IllegalArgumentException("Destination cannot be empty!");
        }

        // Source and destination must be different
        if (flight.getSource().trim().equalsIgnoreCase(flight.getDestination().trim())) {
            throw new IllegalArgumentException("Source and destination must be different!");
        }

        // Both times are required
        if (flight.getDepartureTime() == null || flight.getArrivalTime() == null) {
            throw new IllegalArgumentException("Departure and arrival times are required!");
        }

        // Arrival must be strictly after departure
        if (!flight.getArrivalTime().isAfter(flight.getDepartureTime())) {
            throw new IllegalArgumentException("Arrival time must be after departure time!");
        }

        // Delay cannot be negative
        if (flight.getDelayMinutes() < 0) {
            throw new IllegalArgumentException("Delay minutes cannot be negative!");
        }

        // Status (when provided) must be one of the accepted values
        if (flight.getStatus() != null
                && !VALID_STATUSES.contains(flight.getStatus().trim().toUpperCase())) {
            throw new IllegalArgumentException("Invalid flight status!");
        }
    }

    /**
     * Enforces which status changes are allowed.
     * Terminal states (ARRIVED, CANCELLED) cannot change further.
     */
    private void validateStatusTransition(String current, String next) {

        if (current == null) {
            current = "SCHEDULED";
        }

        // No change needed
        if (current.equals(next)) {
            return;
        }

        // Terminal states are final
        if (current.equals("ARRIVED")) {
            throw new IllegalArgumentException("An ARRIVED flight cannot change status!");
        }
        if (current.equals("CANCELLED")) {
            throw new IllegalArgumentException("A CANCELLED flight cannot change status!");
        }
        // A departed flight can only become arrived
        if (current.equals("DEPARTED")) {
            if (!next.equals("ARRIVED")) {
                throw new IllegalArgumentException(
                        "A DEPARTED flight can only be marked as ARRIVED!");
            }
            return;
        }

        // SCHEDULED / DELAYED / BOARDING may move to the states below
        Set<String> allowed = switch (current) {
            case "SCHEDULED" -> Set.of("DELAYED", "BOARDING", "CANCELLED");
            case "DELAYED" -> Set.of("SCHEDULED", "BOARDING", "CANCELLED");
            case "BOARDING" -> Set.of("DEPARTED", "CANCELLED");
            default -> Set.of();
        };

        if (!allowed.contains(next)) {
            throw new IllegalArgumentException(
                    "Invalid status change from " + current + " to " + next + "!");
        }
    }
}