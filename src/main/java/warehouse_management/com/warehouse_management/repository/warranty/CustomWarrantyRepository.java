package warehouse_management.com.warehouse_management.repository.warranty;

import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.warranty.response.WarrantyResponseDTO;

public interface CustomWarrantyRepository {
    Page<WarrantyResponseDTO> findItemWithFilter(PageOptionsDto pageOptionsDto);
}
