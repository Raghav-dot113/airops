
package com.airops.booking;

import java.sql.SQLException;
import java.util.List;

public class BookingService {

    private final BookingDAO bookingDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
    }

    // Create a new booking
    public boolean createBooking(Booking booking) throws SQLException {

        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be empty!");
        }

        if (booking.getPassengerId() <= 0 ||
                booking.getFlightId() <= 0 ||
                booking.getSeatId() <= 0) {
            throw new IllegalArgumentException("Invalid passenger, flight or seat ID!");
        }

        String pnr = PNRGenerator.generatePNR();
        booking.setPnr(pnr);

        return bookingDAO.addBooking(booking);
    }

    // Find booking using PNR
    public Booking findBooking(String pnr) throws SQLException {

        if (pnr == null || pnr.trim().isEmpty()) {
            throw new IllegalArgumentException("PNR cannot be empty!");
        }

        return bookingDAO.getBookingByPNR(pnr.trim());
    }

    // Get bookings of a passenger
    public List<Booking> getPassengerBookings(int passengerId) throws SQLException {

        if (passengerId <= 0) {
            throw new IllegalArgumentException("Invalid passenger ID!");
        }

        return bookingDAO.getBookingsByPassenger(passengerId);
    }

    // Cancel booking
    public boolean cancelBooking(String pnr) throws SQLException {

        if (pnr == null || pnr.trim().isEmpty()) {
            throw new IllegalArgumentException("PNR cannot be empty!");
        }

        return bookingDAO.cancelBooking(pnr.trim());
    }
}
