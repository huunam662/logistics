package warehouse_management.com.warehouse_management.repository.department;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.model.DeliveryDepartment;

import java.util.List;

@Repository
public interface DeliveryDepartmentRepository extends MongoRepository<DeliveryDepartment, String> {

    @Query("{'departmentName': {$regex: ?0, $options: 'i'}}")
    List<DeliveryDepartment> findByDepartmentNameContainingIgnoreCase(String departmentName);

    boolean existsByDepartmentName(String departmentName);
}
