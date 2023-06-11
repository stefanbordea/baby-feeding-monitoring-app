package gr.athtech.babyfeedingmonitoringapp.repository.implementation;

import gr.athtech.babyfeedingmonitoringapp.model.Role;
import gr.athtech.babyfeedingmonitoringapp.model.User;
import gr.athtech.babyfeedingmonitoringapp.repository.UserRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Named
public class UserRepositoryImpl implements UserRepository {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/baby_feeding_application";
    private static final String USER = "postgres";
    private static final String PASS = "password";

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("PostgreSQL JDBC Driver not found");
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    @Override
    public Role checkUserRole(String username, String password) {
        Role role = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT role FROM users WHERE username = ? AND password = ?")) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    role = Role.valueOf(rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving the role of the user: {}", e.getMessage());
        }
        return role;
    }

    @Override
    public Optional<User> create(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, String.valueOf(user.getRole()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    user.setId(generatedId);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while creating user: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String roleStr = rs.getString("role");
                Role role = Role.valueOf(roleStr);

                User user = new User();
                user.setId(id);
                user.setUsername(username);
                user.setPassword(password);
                user.setRole(role);

                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("Error while retrieving user with ID {}: {}", id, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username =?")) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id");
                String usernameDb = rs.getString("username");
                String password = rs.getString("password");
                String roleStr = rs.getString("role");
                Role role = Role.valueOf(roleStr);

                User user = new User();
                user.setId(id);
                user.setUsername(usernameDb);
                user.setPassword(password);
                user.setRole(role);

                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("Error while retrieving user with username {}: {}", username, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String roleStr = rs.getString("role");
                Role role = Role.valueOf(roleStr);

                User user = new User();
                user.setId(id);
                user.setUsername(username);
                user.setPassword(password);
                user.setRole(role);

                users.add(user);
            }
        } catch (SQLException e) {
            logger.error("Error while retrieving all users: {}", e.getMessage());
        }
        return users;
    }

    @Override
    public Optional<User> update(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?")) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().toString());
            stmt.setLong(4, user.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("Error updating user with ID {}: {}", user.getId(), e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void delete(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {

            stmt.setLong(1, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error removing user with ID {}: {}", user.getId(), e.getMessage());
        }
    }
}
