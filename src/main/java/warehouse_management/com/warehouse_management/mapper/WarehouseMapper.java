package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import warehouse_management.com.warehouse_management.dto.warehouse.response.WarehouseRes;
import warehouse_management.com.warehouse_management.model.Warehouse;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface WarehouseMapper {

    WarehouseMapper INSTANCE = Mappers.getMapper(WarehouseMapper.class);

    WarehouseRes toWarehouseRes(Warehouse warehouse);

    List<WarehouseRes> toWarehouseResList(List<Warehouse> warehouses);

}
