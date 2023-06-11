package gr.athtech.babyfeedingmonitoringapp.repository.implementation;

import gr.athtech.babyfeedingmonitoringapp.dto.AverageFeedingDurationDto;
import gr.athtech.babyfeedingmonitoringapp.dto.FeedingSessionDto;
import gr.athtech.babyfeedingmonitoringapp.model.FeedingSession;
import gr.athtech.babyfeedingmonitoringapp.model.User;
import gr.athtech.babyfeedingmonitoringapp.repository.FeedingSessionRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PGInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Named
public class FeedingSessionRepositoryImpl implements FeedingSessionRepository {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "password";
    private final Logger logger = LoggerFactory.getLogger(FeedingSessionRepositoryImpl.class);

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("PostgreSQL JDBC Driver not found");
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    @Override
    public List<FeedingSessionDto> findByTimePeriod(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        List<FeedingSessionDto> feedingSessions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ufs.user_id as user_id, fs.id as id, fs.milkConsumed, fs.startTime, fs.endTime " +
                             "FROM feeding_sessions fs " +
                             "JOIN feeding_session_users ufs ON fs.id = ufs.feeding_session_id " +
                             "WHERE ufs.user_id = ? AND fs.startTime >= ? AND fs.endTime <= ?")) {

            stmt.setLong(1, userId);
            stmt.setTimestamp(2, Timestamp.valueOf(startTime));
            stmt.setTimestamp(3, Timestamp.valueOf(endTime));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FeedingSessionDto feedingSession = new FeedingSessionDto();
                    feedingSession.setId(rs.getLong("id"));
                    feedingSession.setUserId(rs.getLong("user_id"));
                    feedingSession.setMilkConsumed(rs.getDouble("milkConsumed"));
                    feedingSession.setStartTime(rs.getTimestamp("startTime").toLocalDateTime());
                    feedingSession.setEndTime(rs.getTimestamp("endTime").toLocalDateTime());
                    feedingSessions.add(feedingSession);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving feeding sessions by time period: {}", e.getMessage());
        }

        return feedingSessions;
    }

    @Override
    public FeedingSessionDto calculateAverageMilkConsumed(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        FeedingSessionDto feedingSession = new FeedingSessionDto();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ufs.user_id AS user_id, AVG(fs.milkConsumed) AS avg, MIN(fs.startTime) AS starttime, MAX(fs.endTime) AS endtime " +
                             "FROM feeding_sessions fs " +
                             "JOIN feeding_session_users ufs ON fs.id = ufs.feeding_session_id " +
                             "WHERE ufs.user_id = ? " +
                             "  AND fs.startTime >= ?::timestamp " +
                             "  AND fs.endTime <= ?::timestamp " +
                             "GROUP BY ufs.user_id")) {

            stmt.setLong(1, userId);
            stmt.setTimestamp(2, Timestamp.valueOf(startTime));
            stmt.setTimestamp(3, Timestamp.valueOf(endTime));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    feedingSession.setUserId(rs.getLong("user_id"));
                    feedingSession.setMilkConsumed(rs.getDouble("avg"));
                    feedingSession.setStartTime(rs.getTimestamp("starttime").toLocalDateTime());
                    feedingSession.setEndTime(rs.getTimestamp("endtime").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            logger.error("Error calculating average milk consumed: {}", e.getMessage());
        }
        return feedingSession;
    }


    @Override
    public AverageFeedingDurationDto calculateAverageFeedingDuration(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        AverageFeedingDurationDto feedDuration = new AverageFeedingDurationDto();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ufs.user_id, AVG(fs.endTime - fs.startTime) AS avg, " +
                             "MIN(fs.startTime) AS starttime, MAX(fs.endTime) AS endtime " +
                             "FROM feeding_sessions fs " +
                             "JOIN feeding_session_users ufs ON fs.id = ufs.feeding_session_id " +
                             "WHERE ufs.user_id = ? AND fs.startTime >= ? AND fs.endTime <= ? " +
                             "GROUP BY ufs.user_id")) {

            stmt.setLong(1, userId);
            stmt.setTimestamp(2, Timestamp.valueOf(startTime));
            stmt.setTimestamp(3, Timestamp.valueOf(endTime));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    feedDuration.setUserId(rs.getLong("user_id"));
                    PGInterval pgInterval = (PGInterval) rs.getObject("avg");
                    Duration duration = Duration.ofDays(pgInterval.getDays())
                            .plusHours(pgInterval.getHours())
                            .plusMinutes(pgInterval.getMinutes())
                            .plusSeconds((long) pgInterval.getSeconds());
                    feedDuration.setAverageFeedingDuration(duration);
                    feedDuration.setStartTime(rs.getTimestamp("starttime").toLocalDateTime());
                    feedDuration.setEndTime(rs.getTimestamp("endtime").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            logger.error("Error calculating average feeding duration: {}", e.getMessage());
        }
        return feedDuration;
    }


    @Override
    public Optional<FeedingSession> create(FeedingSession feedingSession) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO feeding_sessions (milkconsumed, starttime, endtime) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDouble(1, feedingSession.getMilkConsumed());
            stmt.setTimestamp(2, Timestamp.valueOf(feedingSession.getStartTime()));
            stmt.setTimestamp(3, Timestamp.valueOf(feedingSession.getEndTime()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    feedingSession.setId(generatedId);
                    addUsersToFeedingSession(conn, feedingSession);
                    return Optional.of(feedingSession);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while creating feedingSession: {}", e.getMessage());
        }
        return Optional.empty();
    }

    private void addUsersToFeedingSession(Connection conn, FeedingSession feedingSession) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO feeding_session_users (user_id, feeding_session_id) VALUES (?, ?)")) {

            for (User user : feedingSession.getUsers()) {
                stmt.setLong(1, user.getId());
                stmt.setLong(2, feedingSession.getId());
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public Optional<FeedingSession> findById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT fs.id, fs.milkConsumed, fs.startTime, fs.endTime, u.id AS user_id " +
                             "FROM feeding_sessions fs " +
                             "JOIN feeding_session_users ufs ON fs.id = ufs.feeding_session_id " +
                             "JOIN users u ON u.id = ufs.user_id " +
                             "WHERE fs.id = ?")) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double milkConsumed = rs.getDouble("milkConsumed");
                LocalDateTime startTime = rs.getTimestamp("startTime").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("endTime").toLocalDateTime();
                long userId = rs.getLong("user_id");

                FeedingSession feedingSession = new FeedingSession();
                feedingSession.setUsers(new ArrayList<>());
                feedingSession.setId(id);
                feedingSession.setMilkConsumed(milkConsumed);
                feedingSession.setStartTime(startTime);
                feedingSession.setEndTime(endTime);

                User user = new User();
                user.setId(userId);
                feedingSession.getUsers().add(user);

                return Optional.of(feedingSession);
            }
        } catch (SQLException e) {
            logger.error("Error while retrieving feedingSession with ID {}: {}", id, e.getMessage());
        }
        return Optional.empty();
    }


    @Override
    public List<FeedingSession> findAll() {
        List<FeedingSession> feedingSessions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT fs.id, fs.milkConsumed, fs.startTime, fs.endTime, u.id AS user_id " +
                             "FROM feeding_sessions fs " +
                             "JOIN feeding_session_users ufs ON fs.id = ufs.feeding_session_id " +
                             "JOIN users u ON u.id = ufs.user_id");
             ResultSet rs = stmt.executeQuery()) {

            Map<Long, FeedingSession> feedingSessionMap = new HashMap<>();

            while (rs.next()) {
                long feedingSessionId = rs.getLong("id");
                double milkConsumed = rs.getDouble("milkConsumed");
                LocalDateTime startTime = rs.getTimestamp("startTime").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("endTime").toLocalDateTime();
                long userId = rs.getLong("user_id");

                // Check if the feeding session already exists in the map
                FeedingSession feedingSession = feedingSessionMap.get(feedingSessionId);

                // If the feeding session doesn't exist, create a new one and add it to the map
                if (feedingSession == null) {
                    feedingSession = new FeedingSession();
                    feedingSession.setId(feedingSessionId);
                    feedingSession.setMilkConsumed(milkConsumed);
                    feedingSession.setStartTime(startTime);
                    feedingSession.setEndTime(endTime);
                    feedingSessions.add(feedingSession);
                    feedingSessionMap.put(feedingSessionId, feedingSession);
                }

                // Create a new user and add it to the feeding session
                User user = new User();
                user.setId(userId);
                feedingSession.getUsers().add(user);
            }

        } catch (SQLException e) {
            logger.error("Error while retrieving feeding sessions: {}", e.getMessage());
        }

        return feedingSessions;
    }

    @Override
    public Optional<FeedingSession> update(FeedingSession feedingSession) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE feeding_sessions SET milkconsumed = ?, starttime = ?, endtime = ? WHERE id = ?")) {

            stmt.setDouble(1, feedingSession.getMilkConsumed());
            stmt.setTimestamp(2, Timestamp.valueOf(feedingSession.getStartTime()));
            stmt.setTimestamp(3, Timestamp.valueOf(feedingSession.getEndTime()));
            stmt.setLong(4, feedingSession.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                deleteUsersFromFeedingSession(conn, feedingSession.getId());
                addUsersToFeedingSession(conn, feedingSession);
                return Optional.of(feedingSession);
            }
        } catch (SQLException e) {
            logger.error("Error updating feedingSession with ID {}: {}", feedingSession.getId(), e.getMessage());
        }
        return Optional.empty();
    }

    private void deleteUsersFromFeedingSession(Connection conn, Long feedingSessionId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM feeding_session_users WHERE feeding_session_id = ?")) {

            stmt.setLong(1, feedingSessionId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(FeedingSession feedingSession) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM feeding_sessions WHERE id = ?")) {
            deleteUsersFromFeedingSession(conn, feedingSession.getId());
            stmt.setLong(1, feedingSession.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error removing feedingSession with ID {}: {}", feedingSession.getId(), e.getMessage());
        }
    }
}
