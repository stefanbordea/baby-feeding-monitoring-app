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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Named
public class FeedingSessionRepositoryImpl implements FeedingSessionRepository {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/baby_feeding_application";
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
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM FEEDING_SESSIONS WHERE user_id = ? AND startTime >= ? AND endTime <= ?")) {

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
             PreparedStatement stmt = conn.prepareStatement("SELECT user_id, AVG(milkConsumed) AS avg, MIN(startTime) AS starttime, MAX(endTime) AS endtime FROM FEEDING_SESSIONS WHERE user_id = ? AND startTime >= ? AND endTime <= ? GROUP BY  user_id")) {

            stmt.setLong(1, userId);
            stmt.setTimestamp(2, Timestamp.valueOf(startTime));
            stmt.setTimestamp(3, Timestamp.valueOf(endTime));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    feedingSession.setUserId(rs.getLong("user_id"));
                    feedingSession.setMilkConsumed(rs.getDouble("avg"));
                    feedingSession.setStartTime(rs.getTimestamp("startTime").toLocalDateTime());
                    feedingSession.setEndTime(rs.getTimestamp("endTime").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), "Error calculating average milk consumed");
        }
        return feedingSession;
    }

    @Override
    public AverageFeedingDurationDto calculateAverageFeedingDuration(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        AverageFeedingDurationDto feedDuration = new AverageFeedingDurationDto();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT user_id, AVG(endTime - startTime) as avg, MIN(startTime) AS starttime, MAX(endTime) AS endtime FROM FEEDING_SESSIONS WHERE user_id = ? AND startTime >= ? AND endTime <= ? GROUP BY user_id")) {

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
                    feedDuration.setStartTime(rs.getTimestamp("startTime").toLocalDateTime());
                    feedDuration.setEndTime(rs.getTimestamp("endTime").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), "Error calculating average feeding duration");
        }
        return feedDuration;
    }

    @Override
    public Optional<FeedingSession> create(FeedingSession feedingSession) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO feeding_sessions (milkconsumed, starttime, endtime, user_id) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDouble(1, feedingSession.getMilkConsumed());
            stmt.setTimestamp(2, Timestamp.valueOf(feedingSession.getStartTime()));
            stmt.setTimestamp(3, Timestamp.valueOf(feedingSession.getEndTime()));
            stmt.setLong(4, feedingSession.getUser().getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    feedingSession.setId(generatedId);
                    return Optional.of(feedingSession);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while creating feedingSession: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<FeedingSession> findById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM feeding_sessions WHERE id = ?")) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double milkConsumed = rs.getDouble("milkConsumed");
                java.sql.Timestamp startTime = rs.getTimestamp("startTime");
                java.sql.Timestamp endTime = rs.getTimestamp("endTime");
                long userId = rs.getLong("user_id");

                FeedingSession feedingSession = new FeedingSession();
                feedingSession.setId(id);
                feedingSession.setMilkConsumed(milkConsumed);
                feedingSession.setStartTime(startTime.toLocalDateTime());
                feedingSession.setEndTime(endTime.toLocalDateTime());

                User user = new User();
                user.setId(userId);
                feedingSession.setUser(user);

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
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM feeding_sessions");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                double milkConsumed = rs.getDouble("milkConsumed");
                Timestamp startTime = rs.getTimestamp("startTime");
                Timestamp endTime = rs.getTimestamp("endTime");
                long userId = rs.getLong("user_id");

                FeedingSession feedingSession = new FeedingSession();
                feedingSession.setId(id);
                feedingSession.setMilkConsumed(milkConsumed);
                feedingSession.setStartTime(startTime.toLocalDateTime());
                feedingSession.setEndTime(endTime.toLocalDateTime());

                User user = new User();
                user.setId(userId);
                feedingSession.setUser(user);

                feedingSessions.add(feedingSession);
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
                     "UPDATE feeding_sessions SET milkconsumed = ?, starttime = ?, endtime = ?, user_id = ? WHERE id = ?")) {

            stmt.setDouble(1, feedingSession.getMilkConsumed());
            stmt.setTimestamp(2, Timestamp.valueOf(feedingSession.getStartTime()));
            stmt.setTimestamp(3, Timestamp.valueOf(feedingSession.getEndTime()));
            stmt.setLong(4, feedingSession.getUser().getId());
            stmt.setLong(5, feedingSession.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.of(feedingSession);
            }
        } catch (SQLException e) {
            logger.error("Error updating feedingSession with ID {}: {}", feedingSession.getId(), e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void delete(FeedingSession feedingSession) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM feeding_sessions WHERE id = ?")) {

            stmt.setLong(1, feedingSession.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error removing feedingSession with ID {}: {}", feedingSession.getId(), e.getMessage());
        }
    }
}
