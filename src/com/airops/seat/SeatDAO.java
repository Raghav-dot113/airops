
package com.airops.seat;

import com.airops.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    // Add a new seat
    public boolean addSeat(Seat seat) throws SQLException {

        String sql = "INSERT INTO seats (flight_id, seat_number, status) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, seat.getFlightId());
            ps.setString(2, seat.getSeatNumber());
            ps.setString(3, seat.getStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        seat.setSeatId(rs.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    // Find seat using ID
    public Seat getSeatById(int seatId) throws SQLException {

        String sql = "SELECT * FROM seats WHERE seat_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, seatId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSeat(rs);
                }
            }
        }

        return null;
    }

    // Get all seats of a flight
    public List<Seat> getSeatsByFlight(int flightId) throws SQLException {

        List<Seat> seats = new ArrayList<>();

        String sql = "SELECT * FROM seats WHERE flight_id = ? ORDER BY seat_number";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, flightId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapSeat(rs));
                }
            }
        }

        return seats;
    }

    // Get available seats of a flight
    public List<Seat> getAvailableSeats(int flightId) throws SQLException {

        List<Seat> seats = new ArrayList<>();

        String sql = "SELECT * FROM seats WHERE flight_id = ? AND status = 'AVAILABLE' ORDER BY seat_number";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, flightId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapSeat(rs));
                }
            }
        }

        return seats;
    }

    // Update seat status
    public boolean updateSeatStatus(int seatId, String status) throws SQLException {

        String sql = "UPDATE seats SET status = ? WHERE seat_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, seatId);

            return ps.executeUpdate() > 0;
        }
    }

    // Reserve a seat only if it is currently available
    public boolean reserveSeat(int seatId) throws SQLException {

        String sql = "UPDATE seats SET status = 'BOOKED' WHERE seat_id = ? AND status = 'AVAILABLE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, seatId);

            return ps.executeUpdate() > 0;
        }
    }

    // Release a booked seat
    public boolean releaseSeat(int seatId) throws SQLException {

        String sql = "UPDATE seats SET status = 'AVAILABLE' WHERE seat_id = ? AND status = 'BOOKED'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, seatId);

            return ps.executeUpdate() > 0;
        }
    }

    // Convert database row into Seat object
    private Seat mapSeat(ResultSet rs) throws SQLException {

        return new Seat(
                rs.getInt("seat_id"),
                rs.getInt("flight_id"),
                rs.getString("seat_number"),
                rs.getString("status")
        );
    }
}
