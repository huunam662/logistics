package warehouse_management.com.warehouse_management.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import warehouse_management.com.warehouse_management.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    @Aggregation(pipeline = {
            "{ $lookup: { from: 'user', localField: 'roleIds', foreignField: '_id', as: 'userRoles' } }",
            "{ $match: { 'userRoles._id': ?0 } }"
    })
    Optional<List<Role>> findRolesByUserId(String userId);
}
