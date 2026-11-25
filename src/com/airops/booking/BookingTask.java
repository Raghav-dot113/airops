
package com.airops.booking;

import java.util.concurrent.Callable;

public class BookingTask implements Callable<Boolean> {

    private final Booking booking;

    public BookingTask(Booking booking) {
        this.booking = booking;
    }

    @Override
    public Boolean call() throws Exception {

        BookingService bookingService = new BookingService();

        System.out.println(
                "Processing booking on thread: "
                        + Thread.currentThread().getName()
        );

        return bookingService.createBooking(booking);
    }
}
