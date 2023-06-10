package gr.athtech.babyfeedingmonitoringapp.repository;

import gr.athtech.babyfeedingmonitoringapp.dto.AverageFeedingDurationDto;
import gr.athtech.babyfeedingmonitoringapp.dto.FeedingSessionDto;
import gr.athtech.babyfeedingmonitoringapp.model.FeedingSession;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedingSessionRepository extends GenericRepository<FeedingSession, Long> {
    List<FeedingSessionDto> findByTimePeriod(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    FeedingSessionDto calculateAverageMilkConsumed(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    AverageFeedingDurationDto calculateAverageFeedingDuration(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
