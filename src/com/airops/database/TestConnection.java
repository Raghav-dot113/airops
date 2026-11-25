
package com.airops.database;

import java.sql.*;

public class TestConnection {

    public static void main(String[] args) {

        try (Connection connection = DBConnection.getConnection()) {

            System.out.println("AIROPS DATABASE CONNECTED!");
            System.out.println("Database: " + connection.getCatalog());

        } catch (SQLException e) {

            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
