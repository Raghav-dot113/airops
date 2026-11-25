package com.airops.auth;

import com.airops.database.DBConnection;
import com.airops.exceptions.AccessDeniedException;

import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class AuthService {

    private static final AuthService INSTANCE = new AuthService();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static volatile User currentUser;

    public static AuthService getInstance() {
        return INSTANCE;
    }

    public User authenticate(String username, char[] password) throws SQLException {
        if (username == null || username.trim().isEmpty() || password == null) {
            clearSession();
            return null;
        }

        String sql = "SELECT user_id, username, password_salt, password_hash, role "
                + "FROM users WHERE username = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.trim());
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    clearSession();
                    return null;
                }

                byte[] salt = result.getBytes("password_salt");
                byte[] expectedHash = result.getBytes("password_hash");
                byte[] actualHash = hashPassword(password, salt);
                if (!MessageDigest.isEqual(expectedHash, actualHash)) {
                    clearSession();
                    return null;
                }

                int userId = result.getInt("user_id");
                String foundUsername = result.getString("username");
                String role = result.getString("role");
                currentUser = "ADMIN".equalsIgnoreCase(role)
                        ? new Admin(userId, foundUsername)
                        : new User(userId, foundUsername, role);
                return currentUser;
            }
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    public static byte[] hashPassword(char[] password, byte[] salt) {
        ByteBuffer encodedPassword = StandardCharsets.UTF_8.encode(CharBuffer.wrap(password));
        byte[] passwordBytes = new byte[encodedPassword.remaining()];
        encodedPassword.get(passwordBytes);
        if (encodedPassword.hasArray()) {
            Arrays.fill(encodedPassword.array(), (byte) 0);
        }
        byte[] input = new byte[salt.length + passwordBytes.length];
        System.arraycopy(salt, 0, input, 0, salt.length);
        System.arraycopy(passwordBytes, 0, input, salt.length, passwordBytes.length);
        try {
            return MessageDigest.getInstance("SHA-256").digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        } finally {
            Arrays.fill(passwordBytes, (byte) 0);
            Arrays.fill(input, (byte) 0);
        }
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void clearSession() {
        currentUser = null;
    }

    public User requireAdmin() throws AccessDeniedException {
        User user = currentUser;
        if (user == null || !user.isAdmin()) {
            throw new AccessDeniedException("Access denied");
        }
        return user;
    }
}
