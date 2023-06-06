package gr.athtech.babyfeedingmonitoringapp.service;

import gr.athtech.babyfeedingmonitoringapp.model.FeedingSession;

import java.util.List;

public interface FeedingSessionService {
    FeedingSession create(FeedingSession feedingSession);

    FeedingSession findById(Long id);

    List<FeedingSession> findAll();

    FeedingSession update(FeedingSession feedingSession);

    void delete(FeedingSession feedingSession);
}
