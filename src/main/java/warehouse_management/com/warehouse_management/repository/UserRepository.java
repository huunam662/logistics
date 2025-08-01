package warehouse_management.com.warehouse_management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import warehouse_management.com.warehouse_management.model.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}
