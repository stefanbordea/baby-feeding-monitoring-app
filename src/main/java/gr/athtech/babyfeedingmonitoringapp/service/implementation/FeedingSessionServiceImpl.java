package gr.athtech.babyfeedingmonitoringapp.service.implementation;

import gr.athtech.babyfeedingmonitoringapp.dto.AverageFeedingDurationDto;
import gr.athtech.babyfeedingmonitoringapp.dto.FeedingSessionDto;
import gr.athtech.babyfeedingmonitoringapp.model.FeedingSession;
import gr.athtech.babyfeedingmonitoringapp.repository.FeedingSessionRepository;
import gr.athtech.babyfeedingmonitoringapp.service.FeedingSessionService;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Named
public class FeedingSessionServiceImpl implements FeedingSessionService {

    @Inject
    private FeedingSessionRepository feedingSessionRepository;


    @Override
    public List<FeedingSessionDto> findByTimePeriod(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null) {
            startTime = LocalDateTime.of(1970, 1, 1, 0, 0, 1);
        }

        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        return feedingSessionRepository.findByTimePeriod(userId, startTime, endTime);
    }

    @Override
    public FeedingSessionDto calculateAverageMilkConsumed(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null) {
            startTime = LocalDateTime.of(1970, 1, 1, 0, 0, 1);
        }

        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        return feedingSessionRepository.calculateAverageMilkConsumed(userId, startTime, endTime);
    }

    @Override
    public AverageFeedingDurationDto calculateAverageFeedingDuration(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null) {
            startTime = LocalDateTime.of(1970, 1, 1, 0, 0, 1);
        }

        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        return feedingSessionRepository.calculateAverageFeedingDuration(userId, startTime, endTime);
    }

    @Override
    public Optional<FeedingSession> create(FeedingSession feedingSession) {
        return feedingSessionRepository.create(feedingSession);
    }

    @Override
    public Optional<FeedingSession> findById(Long id) {
        return feedingSessionRepository.findById(id);
    }

    @Override
    public List<FeedingSession> findAll() {
        return feedingSessionRepository.findAll();
    }

    @Override
    public Optional<FeedingSession> update(FeedingSession feedingSession) {
        return feedingSessionRepository.update(feedingSession);
    }

    @Override
    public void delete(FeedingSession feedingSession) {
        feedingSessionRepository.delete(feedingSession);
    }
}
