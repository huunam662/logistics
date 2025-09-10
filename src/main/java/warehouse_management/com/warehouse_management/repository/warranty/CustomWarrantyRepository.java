package warehouse_management.com.warehouse_management.repository.warranty;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.warranty.response.WarrantyResponseDTO;
import warehouse_management.com.warehouse_management.enumerate.WarrantyStatus;
import warehouse_management.com.warehouse_management.model.Warranty;

public interface CustomWarrantyRepository {
    Page<WarrantyResponseDTO> findItemWithFilter(PageOptionsDto pageOptionsDto);

    Warranty updateStatus(ObjectId id, WarrantyStatus status);
}
