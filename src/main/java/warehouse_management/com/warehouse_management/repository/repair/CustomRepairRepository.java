package warehouse_management.com.warehouse_management.repository.repair;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairResponseDTO;
import warehouse_management.com.warehouse_management.enumerate.RepairStatus;
import warehouse_management.com.warehouse_management.model.Repair;

public interface CustomRepairRepository {
    Page<RepairResponseDTO> findItemWithFilter(PageOptionsDto pageOptionsDto);
    Repair updateStatus(ObjectId id, RepairStatus status);

    // Update complete cho đơn sửa chữa, có kèm theo item để update lại status IN_STOCK
    Repair updateStatus(ObjectId id, RepairStatus status, ObjectId itemId);
}
