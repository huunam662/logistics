package warehouse_management.com.warehouse_management.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import warehouse_management.com.warehouse_management.model.WarehouseTransferTicket;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WarehouseTransferTicketRepository extends MongoRepository<WarehouseTransferTicket, ObjectId> {

    // Tìm tất cả các phiếu theo trạng thái
    List<WarehouseTransferTicket> findByStatus(String status);

    // Tìm phiếu theo ID kho gửi
    List<WarehouseTransferTicket> findByOriginWarehouseId(ObjectId warehouseId);

    // Tìm phiếu theo ID kho nhận
    List<WarehouseTransferTicket> findByDestinationWarehouseId(ObjectId warehouseId);

    // Tìm phiếu theo requesterId
    List<WarehouseTransferTicket> findByRequesterId(ObjectId userId);

    // Tìm phiếu theo approverId
    List<WarehouseTransferTicket> findByApproverId(ObjectId userId);

    // Tìm tất cả phiếu tạo trong khoảng thời gian
    List<WarehouseTransferTicket> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Tìm các phiếu đã xử lý (đã có processedAt)
    List<WarehouseTransferTicket> findByProcessedAtNotNull();
}