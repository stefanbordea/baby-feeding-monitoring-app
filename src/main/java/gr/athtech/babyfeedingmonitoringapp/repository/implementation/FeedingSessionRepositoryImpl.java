package gr.athtech.babyfeedingmonitoringapp.repository.implementation;

import gr.athtech.babyfeedingmonitoringapp.model.FeedingSession;
import gr.athtech.babyfeedingmonitoringapp.repository.FeedingSessionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FeedingSessionRepositoryImpl implements FeedingSessionRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    private final Logger logger;

    @Override
    public Optional<FeedingSession> create(FeedingSession feedingSession) {
        try {
            entityManager.persist(feedingSession);
            return Optional.of(feedingSession);
        } catch (Exception e) {
            logger.error(e.getMessage(), "Error while creating feedingSession");
            return Optional.empty();
        }
    }

    @Override
    public Optional<FeedingSession> findById(Long id) {
        try {
            FeedingSession feedingSession = entityManager.find(FeedingSession.class, id);
            return Optional.ofNullable(feedingSession);
        } catch (Exception e) {
            logger.error(e.getMessage(), "Error while retrieving feedingSession with ID: " + id);
            return Optional.empty();
        }
    }

    @Override
    public List<FeedingSession> findAll() {
        try {
            return entityManager.createQuery("SELECT u FROM FeedingSession u", FeedingSession.class).getResultList();
        } catch (Exception e) {
            logger.error(e.getMessage(), "Error while retrieving feeding sessions");
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<FeedingSession> update(FeedingSession feedingSession) {
        try {
            FeedingSession feedingSessionToBeUpdated = entityManager.merge(feedingSession);
            return Optional.of(feedingSessionToBeUpdated);
        } catch (Exception e) {
            logger.error(e.getMessage(), "Error updating feedingSession with ID: " + feedingSession.getId());
            return Optional.empty();
        }
    }

    @Override
    public void delete(FeedingSession feedingSession) {
        try {
            entityManager.remove(entityManager.merge(feedingSession));
        } catch (Exception e) {
            logger.error(e.getMessage(), "Error removing feedingSession with ID: " + feedingSession.getId());
        }
    }
}
