
package com.airops.passenger;

import com.airops.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PassengerDAO {

    // Add a new passenger
    public boolean addPassenger(Passenger passenger) throws SQLException {

        String sql = "INSERT INTO passengers (full_name, email, phone) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, passenger.getFullName());
            ps.setString(2, passenger.getEmail());
            ps.setString(3, passenger.getPhone());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        passenger.setPassengerId(rs.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    // Find passenger using ID
    public Passenger getPassengerById(int passengerId) throws SQLException {

        String sql = "SELECT * FROM passengers WHERE passenger_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, passengerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPassenger(rs);
                }
            }
        }

        return null;
    }

    // Display all passengers
    public List<Passenger> getAllPassengers() throws SQLException {

        List<Passenger> passengers = new ArrayList<>();

        String sql = "SELECT * FROM passengers ORDER BY passenger_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                passengers.add(mapPassenger(rs));
            }
        }

        return passengers;
    }

    // Update passenger details
    public boolean updatePassenger(Passenger passenger) throws SQLException {

        String sql = "UPDATE passengers SET full_name = ?, email = ?, phone = ? WHERE passenger_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, passenger.getFullName());
            ps.setString(2, passenger.getEmail());
            ps.setString(3, passenger.getPhone());
            ps.setInt(4, passenger.getPassengerId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete passenger using ID
    public boolean deletePassenger(int passengerId) throws SQLException {

        String sql = "DELETE FROM passengers WHERE passenger_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, passengerId);

            return ps.executeUpdate() > 0;
        }
    }

    // Find passenger using email
    public Passenger getPassengerByEmail(String email) throws SQLException {

        String sql = "SELECT * FROM passengers WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPassenger(rs);
                }
            }
        }

        return null;
    }

    // Convert database row into Passenger object
    private Passenger mapPassenger(ResultSet rs) throws SQLException {

        return new Passenger(
                rs.getInt("passenger_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone")
        );
    }
}
