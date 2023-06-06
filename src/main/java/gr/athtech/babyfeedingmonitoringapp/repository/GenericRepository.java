package gr.athtech.babyfeedingmonitoringapp.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, K> {
    Optional<T> create(T type);

    Optional<T> findById(K id);

    List<T> findAll();

    Optional<T> update(T type);

    void delete(T type);
}
