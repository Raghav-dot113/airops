
package com.airops.booking;

public class Booking {

    private int bookingId;
    private String pnr;
    private int passengerId;
    private int flightId;
    private int seatId;
    private String status;

    // Constructor for creating a new booking
    public Booking(int passengerId, int flightId, int seatId) {
        this.passengerId = passengerId;
        this.flightId = flightId;
        this.seatId = seatId;
        this.status = "CONFIRMED";
    }

    // Constructor for retrieving an existing booking
    public Booking(int bookingId, String pnr, int passengerId,
                   int flightId, int seatId, String status) {
        this.bookingId = bookingId;
        this.pnr = pnr;
        this.passengerId = passengerId;
        this.flightId = flightId;
        this.seatId = seatId;
        this.status = status;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public int getFlightId() {
        return flightId;
    }

    public int getSeatId() {
        return seatId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
