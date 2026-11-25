USE airops;

CREATE TABLE IF NOT EXISTS users (
    user_id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    password_salt BINARY(16) NOT NULL,
    password_hash BINARY(32) NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    PRIMARY KEY (user_id),
    UNIQUE KEY uq_users_username (username)
);

-- Initial credentials: username=admin, password=Admin123!
-- Password hash: SHA-256(salt || UTF-8(password)); salt was generated securely.
INSERT IGNORE INTO users (username, password_salt, password_hash, role)
VALUES (
    'admin',
    UNHEX('E44D0B1ED1D2FEE32BAD0262168644DD'),
    UNHEX('A7DC4F764091BC420BFEE8849B11EFAF0A6E541CBA7E61831C1FB60BCD002C00'),
    'ADMIN'
);
