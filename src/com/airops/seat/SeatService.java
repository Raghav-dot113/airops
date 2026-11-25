
package com.airops.seat;

import java.sql.SQLException;
import java.util.List;

public class SeatService {

    private final SeatDAO seatDAO;

    public SeatService() {
        this.seatDAO = new SeatDAO();
    }

    // Add a new seat
    public boolean addSeat(Seat seat) throws SQLException {

        if (seat == null) {
            throw new IllegalArgumentException("Seat cannot be empty!");
        }

        if (seat.getFlightId() <= 0 ||
                seat.getSeatNumber() == null ||
                seat.getSeatNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid seat details!");
        }

        return seatDAO.addSeat(seat);
    }

    // Find seat by ID
    public Seat findSeat(int seatId) throws SQLException {

        if (seatId <= 0) {
            throw new IllegalArgumentException("Invalid seat ID!");
        }

        return seatDAO.getSeatById(seatId);
    }

    // Get all seats of a flight
    public List<Seat> getSeatsByFlight(int flightId) throws SQLException {

        if (flightId <= 0) {
            throw new IllegalArgumentException("Invalid flight ID!");
        }

        return seatDAO.getSeatsByFlight(flightId);
    }

    // Get available seats
    public List<Seat> getAvailableSeats(int flightId) throws SQLException {

        if (flightId <= 0) {
            throw new IllegalArgumentException("Invalid flight ID!");
        }

        return seatDAO.getAvailableSeats(flightId);
    }

    // Reserve a seat
    public boolean reserveSeat(int seatId) throws SQLException {

        if (seatId <= 0) {
            throw new IllegalArgumentException("Invalid seat ID!");
        }

        return seatDAO.reserveSeat(seatId);
    }

    // Release a seat
    public boolean releaseSeat(int seatId) throws SQLException {

        if (seatId <= 0) {
            throw new IllegalArgumentException("Invalid seat ID!");
        }

        return seatDAO.releaseSeat(seatId);
    }

    // Update seat status
    public boolean updateSeatStatus(int seatId, String status) throws SQLException {

        if (seatId <= 0) {
            throw new IllegalArgumentException("Invalid seat ID!");
        }

        if (!"AVAILABLE".equals(status) && !"BOOKED".equals(status)) {
            throw new IllegalArgumentException("Invalid seat status!");
        }

        return seatDAO.updateSeatStatus(seatId, status);
    }
}
