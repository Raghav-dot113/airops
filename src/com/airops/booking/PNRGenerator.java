
package com.airops.booking;

import java.util.UUID;

public class PNRGenerator {

    public static String generatePNR() {

        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }
}
