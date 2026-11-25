
package com.airops.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/airops";

    private static final String USER = "root";

    private static final String PASSWORD ="Raghav@123456";


    private DBConnection() {
        
    }

    public static Connection getConnection() throws SQLException {

        if (PASSWORD == null || PASSWORD.isEmpty()) {
            throw new SQLException(
                    "Database password not found. Set AIROPS_DB_PASSWORD."
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
