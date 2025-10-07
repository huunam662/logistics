package warehouse_management.com.warehouse_management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.model.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    @Query("{ 'toUser.id': ?0, 'unread': ?1 }")
    List<Notification> findByToUserIdAndUnreadOrderByTimeDesc(String toUserId, Boolean unread);
    
    @Query("{ 'toUser.id': ?0 }")
    List<Notification> findByToUserIdOrderByTimeDesc(String toUserId);
    
    @Query(value = "{ 'toUser.id': ?0, 'unread': ?1 }", count = true)
    long countByToUserIdAndUnread(String toUserId, Boolean unread);
}
