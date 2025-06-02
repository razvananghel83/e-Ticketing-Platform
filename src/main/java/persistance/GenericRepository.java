package persistance;

import java.util.List;
import java.util.Optional;


public interface GenericRepository<T> {

    T save(T entity);
    List<T> findAll();
    Optional<T> findById(String id);

    void update(T entity);
    void delete(T entity);
}