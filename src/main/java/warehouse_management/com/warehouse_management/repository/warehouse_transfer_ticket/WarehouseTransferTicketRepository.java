package warehouse_management.com.warehouse_management.repository.warehouse_transfer_ticket;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.model.WarehouseTransferTicket;

import java.util.List;

@Repository
public interface WarehouseTransferTicketRepository extends MongoRepository<WarehouseTransferTicket, ObjectId> {
    List<WarehouseTransferTicket> findByStatus(String status);

    List<WarehouseTransferTicket> findByRequesterId(ObjectId userId);

    List<WarehouseTransferTicket> findByApproverId(ObjectId userId);
}