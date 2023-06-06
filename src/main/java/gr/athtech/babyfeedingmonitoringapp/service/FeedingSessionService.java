package gr.athtech.babyfeedingmonitoringapp.service;

import gr.athtech.babyfeedingmonitoringapp.model.FeedingSession;

import java.util.List;
import java.util.Optional;

public interface FeedingSessionService {
    Optional<FeedingSession> create(FeedingSession feedingSession);

    Optional<FeedingSession> findById(Long id);

    List<FeedingSession> findAll();

    Optional<FeedingSession> update(FeedingSession feedingSession);

    void delete(FeedingSession feedingSession);
}
