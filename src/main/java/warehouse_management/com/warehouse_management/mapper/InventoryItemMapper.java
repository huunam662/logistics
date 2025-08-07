package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import warehouse_management.com.warehouse_management.dto.Inventory.response.*;
import warehouse_management.com.warehouse_management.dto.Inventory.view.InventoryWarehouseContainerView;
import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface InventoryItemMapper {

    InventoryItemMapper INSTANCE = Mappers.getMapper(InventoryItemMapper.class);

    InventoryProductionRes toInventoryProductionRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryProductionRes> toInventoryProductionResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    InventoryDepartureRes toInventoryDepartureRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryDepartureRes> toInventoryDepartureResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    InventoryDestinationRes toInventoryDestinationRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryDestinationRes> toInventoryDestinationResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    InventoryConsignmentRes toInventoryConsignmentRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryConsignmentRes> toInventoryConsignmentResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    InventoryDestinationSparePartsRes toInventoryDestinationSparePartsRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryDestinationSparePartsRes> toInventoryDestinationSparePartsResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    InventoryProductionSparePartsRes toInventoryProductionSparePartsRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryProductionSparePartsRes> toInventoryProductionSparePartsResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    InventoryConsignmentSparePartsRes toInventoryConsignmentSparePartsRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryConsignmentSparePartsRes> toInventoryConsignmentSparePartsResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    InventoryDepartureSparePartsRes toInventoryDepartureSparePartsRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryDepartureSparePartsRes> toInventoryDepartureSparePartsResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    InventoryCentralWarehouseRes toInventoryCentralWarehouseRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryCentralWarehouseRes> toInventoryCentralWarehouseResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);
}
