package gr.athtech.babyfeedingmonitoringapp.repository.implementation;

import gr.athtech.babyfeedingmonitoringapp.model.User;
import gr.athtech.babyfeedingmonitoringapp.repository.UserRepository;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@NoArgsConstructor
@Named
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    @Override
    public Optional<User> create(User user) {
        try {
            entityManager.persist(user);
            return Optional.of(user);
        } catch (Exception e) {
            logger.error(e.getMessage(), "Error while creating user");
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try {
            User user = entityManager.find(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error(e.getMessage(), "Error while retrieving user with ID: " + id);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        try {
            return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
        } catch (Exception e) {
            logger.error(e.getMessage(), "Error while retrieving all users");
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<User> update(User user) {
        try {
            User userToBeUpdated = entityManager.merge(user);
            return Optional.of(userToBeUpdated);
        } catch (Exception e) {
            logger.error(e.getMessage(), "Error updating user with ID: " + user.getId());
            return Optional.empty();
        }
    }

    @Override
    public void delete(User user) {
        try {
            entityManager.remove(entityManager.merge(user));
        } catch (Exception e) {
            logger.error(e.getMessage(), "Error removing user with ID: " + user.getId());
        }
    }
}
