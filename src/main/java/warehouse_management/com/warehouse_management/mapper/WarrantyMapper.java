package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import warehouse_management.com.warehouse_management.dto.warranty.response.WarrantyResponseDTO;
import warehouse_management.com.warehouse_management.model.Warranty;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface WarrantyMapper {

    @Mapping(source = "warrantyInventoryItem.productCode", target = "warrantyInventoryItemProductCode")
    @Mapping(source = "warrantyInventoryItem.model", target = "warrantyInventoryItemModel")
    @Mapping(source = "warrantyInventoryItem.serialNumber", target = "warrantyInventoryItemSerialNumber")
    @Mapping(source = "warrantyInventoryItem.logistics.arrivalDate", target = "arrivalDate")
    @Mapping(source = "client.name", target = "clientName")
    WarrantyResponseDTO toResponseDto(Warranty entity);
}
