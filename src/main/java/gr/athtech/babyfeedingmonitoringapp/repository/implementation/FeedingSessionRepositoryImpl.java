package gr.athtech.babyfeedingmonitoringapp.repository.implementation;

import gr.athtech.babyfeedingmonitoringapp.model.FeedingSession;
import gr.athtech.babyfeedingmonitoringapp.repository.FeedingSessionRepository;

import java.util.List;
import java.util.Optional;

public class FeedingSessionRepositoryImpl implements FeedingSessionRepository {
    @Override
    public Optional<FeedingSession> create(FeedingSession type) {
        return Optional.empty();
    }

    @Override
    public Optional<FeedingSession> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<FeedingSession> findAll() {
        return null;
    }

    @Override
    public Optional<FeedingSession> update(FeedingSession type) {
        return Optional.empty();
    }

    @Override
    public void delete(FeedingSession type) {

    }
}
