package warehouse_management.com.warehouse_management.repository.repair;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.VehicleConfigurationPageDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairResponseDto;
import warehouse_management.com.warehouse_management.dto.repair.response.VehicleRepairPageDto;
import warehouse_management.com.warehouse_management.enumerate.RepairStatus;
import warehouse_management.com.warehouse_management.model.Repair;

import java.util.List;

public interface CustomRepairRepository {
    Page<RepairResponseDto> findItemWithFilter(PageOptionsDto pageOptionsDto);
    Repair updateStatus(ObjectId id, RepairStatus status);

    // Update complete cho đơn sửa chữa, có kèm theo item để update lại status IN_STOCK
    Repair updateStatus(ObjectId id, RepairStatus status, ObjectId itemId);

    void bulkUpdateStatus(List<Repair> repairs);

    void updateStatus(Repair repair);

    void updatePerformed(String repairCode, String performedBy);

    Page<VehicleRepairPageDto> findPageVehicleRepairPage(PageOptionsDto optionsReq);

}
