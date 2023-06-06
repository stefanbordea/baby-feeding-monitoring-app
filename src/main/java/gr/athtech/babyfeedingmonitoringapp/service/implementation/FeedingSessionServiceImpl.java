package gr.athtech.babyfeedingmonitoringapp.service.implementation;

import gr.athtech.babyfeedingmonitoringapp.model.FeedingSession;
import gr.athtech.babyfeedingmonitoringapp.repository.FeedingSessionRepository;
import gr.athtech.babyfeedingmonitoringapp.service.FeedingSessionService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FeedingSessionServiceImpl implements FeedingSessionService {

    private final FeedingSessionRepository feedingSessionRepository;

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
