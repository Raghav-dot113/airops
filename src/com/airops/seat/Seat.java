
package com.airops.seat;

public class Seat {

    private int seatId;
    private int flightId;
    private String seatNumber;
    private String status;

    // Constructor for creating a new seat
    public Seat(int flightId, String seatNumber) {
        this.flightId = flightId;
        this.seatNumber = seatNumber;
        this.status = "AVAILABLE";
    }

    // Constructor for retrieving an existing seat
    public Seat(int seatId, int flightId, String seatNumber, String status) {
        this.seatId = seatId;
        this.flightId = flightId;
        this.seatNumber = seatNumber;
        this.status = status;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getFlightId() {
        return flightId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Seat ID: " + seatId +
                "\nFlight ID: " + flightId +
                "\nSeat Number: " + seatNumber +
                "\nStatus: " + status;
    }
}
