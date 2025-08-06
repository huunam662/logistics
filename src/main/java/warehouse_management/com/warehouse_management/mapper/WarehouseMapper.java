package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.module.warehouse.dto.response.WarehouseUserRes;
import warehouse_management.com.warehouse_management.module.warehouse.dto.view.WarehouseView;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface WarehouseMapper {

    WarehouseMapper INSTANCE = Mappers.getMapper(WarehouseMapper.class);

    WarehouseUserRes toWarehouseRes(Warehouse warehouse);

    List<WarehouseUserRes> toWarehouseResList(List<Warehouse> warehouses);

    WarehouseUserRes toWarehouseUserRes(WarehouseView warehouseView);

    List<WarehouseUserRes> toWarehouseUserResList(List<WarehouseView> warehouseViews);
}
