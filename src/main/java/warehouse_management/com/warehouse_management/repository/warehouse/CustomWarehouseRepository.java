package warehouse_management.com.warehouse_management.repository.warehouse;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.GetDepartureWarehouseForContainerDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.WarehouseResponseDto;

import java.util.List;

public interface CustomWarehouseRepository {
    long bulkSoftDelete(List<ObjectId> warehouseIds, ObjectId deletedBy);
    boolean softDeleteById(ObjectId warehouseId, ObjectId deletedBy, String newStatus);
    Page<WarehouseResponseDto> findPageWarehouse(PageOptionsDto optionsReq);
    List<GetDepartureWarehouseForContainerDto> getDepartureWarehousesForContainer(String warehouseType);
}
