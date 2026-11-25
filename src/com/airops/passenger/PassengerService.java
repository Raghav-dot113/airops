
package com.airops.passenger;

import java.sql.SQLException;
import java.util.List;

public class PassengerService {

    private final PassengerDAO passengerDAO;

    public PassengerService() {
        this.passengerDAO = new PassengerDAO();
    }

    // Register a new passenger
    public boolean registerPassenger(Passenger passenger) throws SQLException {

        validatePassenger(passenger);

        Passenger existing = passengerDAO.getPassengerByEmail(passenger.getEmail());

        if (existing != null) {
            throw new IllegalArgumentException("Email is already registered!");
        }

        return passengerDAO.addPassenger(passenger);
    }

    // Find passenger by ID
    public Passenger findPassenger(int passengerId) throws SQLException {

        if (passengerId <= 0) {
            throw new IllegalArgumentException("Invalid passenger ID!");
        }

        return passengerDAO.getPassengerById(passengerId);
    }

    // Get all passengers
    public List<Passenger> getAllPassengers() throws SQLException {
        return passengerDAO.getAllPassengers();
    }

    // Update passenger details
    public boolean updatePassenger(Passenger passenger) throws SQLException {

        validatePassenger(passenger);

        Passenger existing = passengerDAO.getPassengerByEmail(passenger.getEmail());

        if (existing != null &&
                existing.getPassengerId() != passenger.getPassengerId()) {
            throw new IllegalArgumentException("Email is already used by another passenger!");
        }

        return passengerDAO.updatePassenger(passenger);
    }

    // Delete passenger
    public boolean deletePassenger(int passengerId) throws SQLException {

        if (passengerId <= 0) {
            throw new IllegalArgumentException("Invalid passenger ID!");
        }

        return passengerDAO.deletePassenger(passengerId);
    }

    // Validate passenger information
    private void validatePassenger(Passenger passenger) {

        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be empty!");
        }

        if (passenger.getFullName() == null ||
                passenger.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty!");
        }

        if (passenger.getEmail() == null ||
                !passenger.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email address!");
        }

        if (passenger.getPhone() == null ||
                !passenger.getPhone().matches("\\+?[0-9]{10,15}")) {
            throw new IllegalArgumentException("Phone must contain 10-15 digits!");
        }
    }
}
