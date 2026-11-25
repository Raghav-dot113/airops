package com.airops.flight;

import com.airops.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the flights table.
 * This class is responsible ONLY for SQL operations.
 * All business rules live in FlightService.
 */
public class FlightDAO {

    // Insert a new flight and store the generated flight_id back into the object
    public boolean addFlight(Flight flight) throws SQLException {

        String sql = "INSERT INTO flights "
                + "(flight_number, airline_name, source, destination, "
                + " departure_time, arrival_time, status, gate_number, "
                + " delay_minutes, delay_reason) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, flight.getFlightNumber());
            ps.setString(2, flight.getAirlineName());
            ps.setString(3, flight.getSource());
            ps.setString(4, flight.getDestination());
            ps.setTimestamp(5, Timestamp.valueOf(flight.getDepartureTime()));
            ps.setTimestamp(6, Timestamp.valueOf(flight.getArrivalTime()));
            ps.setString(7, flight.getStatus());
            ps.setString(8, flight.getGateNumber());
            ps.setInt(9, flight.getDelayMinutes());
            ps.setString(10, flight.getDelayReason());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        flight.setFlightId(rs.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    // Update all editable details of an existing flight
    public boolean updateFlight(Flight flight) throws SQLException {

        String sql = "UPDATE flights SET "
                + "flight_number = ?, airline_name = ?, source = ?, destination = ?, "
                + "departure_time = ?, arrival_time = ?, status = ?, gate_number = ?, "
                + "delay_minutes = ?, delay_reason = ? "
                + "WHERE flight_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, flight.getFlightNumber());
            ps.setString(2, flight.getAirlineName());
            ps.setString(3, flight.getSource());
            ps.setString(4, flight.getDestination());
            ps.setTimestamp(5, Timestamp.valueOf(flight.getDepartureTime()));
            ps.setTimestamp(6, Timestamp.valueOf(flight.getArrivalTime()));
            ps.setString(7, flight.getStatus());
            ps.setString(8, flight.getGateNumber());
            ps.setInt(9, flight.getDelayMinutes());
            ps.setString(10, flight.getDelayReason());
            ps.setInt(11, flight.getFlightId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete a flight using its ID
    public boolean deleteFlight(int flightId) throws SQLException {

        String sql = "DELETE FROM flights WHERE flight_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, flightId);

            return ps.executeUpdate() > 0;
        }
    }

    // Find a single flight by its ID
    public Flight getFlightById(int flightId) throws SQLException {

        String sql = "SELECT * FROM flights WHERE flight_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, flightId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFlight(rs);
                }
            }
        }

        return null;
    }

    // Retrieve every flight ordered by flight number
    public List<Flight> getAllFlights() throws SQLException {

        List<Flight> flights = new ArrayList<>();

        String sql = "SELECT * FROM flights ORDER BY flight_number";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                flights.add(mapFlight(rs));
            }
        }

        return flights;
    }

    // Search a flight by its unique flight number
    public Flight searchFlightByNumber(String flightNumber) throws SQLException {

        String sql = "SELECT * FROM flights WHERE flight_number = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, flightNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFlight(rs);
                }
            }
        }

        return null;
    }

    // Search flights that operate on a given source -> destination route
    public List<Flight> searchFlightsByRoute(String source, String destination) throws SQLException {

        List<Flight> flights = new ArrayList<>();

        String sql = "SELECT * FROM flights WHERE source = ? AND destination = ? ORDER BY departure_time";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, source);
            ps.setString(2, destination);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    flights.add(mapFlight(rs));
                }
            }
        }

        return flights;
    }

    // Retrieve all flights that currently have a specific status
    public List<Flight> getFlightsByStatus(String status) throws SQLException {

        List<Flight> flights = new ArrayList<>();

        String sql = "SELECT * FROM flights WHERE status = ? ORDER BY departure_time";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    flights.add(mapFlight(rs));
                }
            }
        }

        return flights;
    }

    // Update only the status column of a flight
    public boolean updateFlightStatus(int flightId, String status) throws SQLException {

        String sql = "UPDATE flights SET status = ? WHERE flight_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, flightId);

            return ps.executeUpdate() > 0;
        }
    }

    // Update the delay minutes and reason, and mark the flight as DELAYED
    public boolean updateFlightDelay(int flightId, int delayMinutes, String reason) throws SQLException {

        String sql = "UPDATE flights SET delay_minutes = ?, delay_reason = ?, status = 'DELAYED' "
                + "WHERE flight_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, delayMinutes);
            ps.setString(2, reason);
            ps.setInt(3, flightId);

            return ps.executeUpdate() > 0;
        }
    }

    // Update only the gate number of a flight
    public boolean updateFlightGate(int flightId, String gateNumber) throws SQLException {

        String sql = "UPDATE flights SET gate_number = ? WHERE flight_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, gateNumber);
            ps.setInt(2, flightId);

            return ps.executeUpdate() > 0;
        }
    }

    // Check whether a flight with the given ID exists
    public boolean isFlightExists(int flightId) throws SQLException {

        String sql = "SELECT 1 FROM flights WHERE flight_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, flightId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Convert one database row into a Flight object
    private Flight mapFlight(ResultSet rs) throws SQLException {

        Timestamp departure = rs.getTimestamp("departure_time");
        Timestamp arrival = rs.getTimestamp("arrival_time");

        return new Flight(
                rs.getInt("flight_id"),
                rs.getString("flight_number"),
                rs.getString("airline_name"),
                rs.getString("source"),
                rs.getString("destination"),
                departure == null ? null : departure.toLocalDateTime(),
                arrival == null ? null : arrival.toLocalDateTime(),
                rs.getString("status"),
                rs.getString("gate_number"),
                rs.getInt("delay_minutes"),
                rs.getString("delay_reason")
        );
    }
}