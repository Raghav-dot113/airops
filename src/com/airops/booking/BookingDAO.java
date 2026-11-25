
package com.airops.booking;

import com.airops.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    // Save a new booking
    public boolean addBooking(Booking booking) throws SQLException {

        String sql = "INSERT INTO bookings (pnr, passenger_id, flight_id, seat_id, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, booking.getPnr());
            ps.setInt(2, booking.getPassengerId());
            ps.setInt(3, booking.getFlightId());
            ps.setInt(4, booking.getSeatId());
            ps.setString(5, booking.getStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        booking.setBookingId(rs.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    // Find booking using PNR
    public Booking getBookingByPNR(String pnr) throws SQLException {

        String sql = "SELECT * FROM bookings WHERE pnr = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pnr);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBooking(rs);
                }
            }
        }

        return null;
    }

    // Get all bookings of a passenger
    public List<Booking> getBookingsByPassenger(int passengerId) throws SQLException {

        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT * FROM bookings WHERE passenger_id = ? ORDER BY booking_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, passengerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapBooking(rs));
                }
            }
        }

        return bookings;
    }

    // Cancel booking
    public boolean cancelBooking(String pnr) throws SQLException {

        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE pnr = ? AND status = 'CONFIRMED'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pnr);

            return ps.executeUpdate() > 0;
        }
    }

    // Convert database row into Booking object
    private Booking mapBooking(ResultSet rs) throws SQLException {

        return new Booking(
                rs.getInt("booking_id"),
                rs.getString("pnr"),
                rs.getInt("passenger_id"),
                rs.getInt("flight_id"),
                rs.getInt("seat_id"),
                rs.getString("status")
        );
    }
}
