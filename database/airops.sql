-- ============================================================
-- AirOps - Airport Operations & Passenger Management System
-- Database creation script
-- ============================================================

CREATE DATABASE IF NOT EXISTS airops;
USE airops;

-- ------------------------------------------------------------
-- Flights table (Flight Management Module)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS flights (
    flight_id       INT PRIMARY KEY AUTO_INCREMENT,
    flight_number   VARCHAR(20)  NOT NULL UNIQUE,
    airline_name    VARCHAR(100) NOT NULL,
    source          VARCHAR(100) NOT NULL,
    destination     VARCHAR(100) NOT NULL,
    departure_time  DATETIME     NOT NULL,
    arrival_time    DATETIME     NOT NULL,
    status          VARCHAR(30)  DEFAULT 'SCHEDULED',
    gate_number     VARCHAR(20),
    delay_minutes   INT          DEFAULT 0,
    delay_reason    VARCHAR(255)
);