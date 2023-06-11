package gr.athtech.babyfeedingmonitoringapp.service;

import gr.athtech.babyfeedingmonitoringapp.dto.AverageFeedingDurationDto;
import gr.athtech.babyfeedingmonitoringapp.dto.FeedingSessionDto;
import gr.athtech.babyfeedingmonitoringapp.model.FeedingSession;
import org.jfree.chart.JFreeChart;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeedingSessionService {
    Optional<FeedingSession> create(FeedingSession feedingSession);

    Optional<FeedingSession> findById(Long id);

    List<FeedingSession> findAll();

    Optional<FeedingSession> update(FeedingSession feedingSession);

    void delete(FeedingSession feedingSession);

    List<FeedingSessionDto> findByTimePeriod(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    FeedingSessionDto calculateAverageMilkConsumed(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    AverageFeedingDurationDto calculateAverageFeedingDuration(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    JFreeChart generateMilkConsumedChart(List<FeedingSessionDto> feedingSessions);

    JFreeChart generateFeedingDurationChart(List<FeedingSessionDto> feedingSessions);

    String generateBase64Image(JFreeChart chart);
}
