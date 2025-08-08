package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.*;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.dto.inventory_item.InventoryWarehouseContainer;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface InventoryItemMapper {

    InventoryItemMapper INSTANCE = Mappers.getMapper(InventoryItemMapper.class);

    @Mapping(target = "orderDate", source = "logistics.orderDate")
    InventoryProductionRes toInventoryProductionRes(InventoryWarehouseContainer inventoryWarehouseContainerView);

    List<InventoryProductionRes> toInventoryProductionResList(List<InventoryWarehouseContainer> inventoryWarehouseContainerViews);

    @Mapping(target = "orderDate", source = "logistics.orderDate")
    @Mapping(target = "arrivalDate", source = "logistics.arrivalDate")
    @Mapping(target = "container.toWarehouse", source = "container.toWarehouse.name")
    InventoryDepartureRes toInventoryDepartureRes(InventoryWarehouseContainer inventoryWarehouseContainerView);

    List<InventoryDepartureRes> toInventoryDepartureResList(List<InventoryWarehouseContainer> inventoryWarehouseContainerViews);

    @Mapping(target = "arrivalDate", source = "logistics.arrivalDate")
    InventoryDestinationRes toInventoryDestinationRes(InventoryWarehouseContainer inventoryWarehouseContainerView);

    List<InventoryDestinationRes> toInventoryDestinationResList(List<InventoryWarehouseContainer> inventoryWarehouseContainerViews);

    @Mapping(target = "arrivalDate", source = "logistics.arrivalDate")
    @Mapping(target = "consignmentDate", source = "logistics.consignmentDate")
    InventoryConsignmentRes toInventoryConsignmentRes(InventoryWarehouseContainer inventoryWarehouseContainerView);

    List<InventoryConsignmentRes> toInventoryConsignmentResList(List<InventoryWarehouseContainer> inventoryWarehouseContainerViews);

    @Mapping(target = "orderDate", source = "logistics.orderDate")
    InventoryDestinationSparePartsRes toInventoryDestinationSparePartsRes(InventoryWarehouseContainer inventoryWarehouseContainerView);

    List<InventoryDestinationSparePartsRes> toInventoryDestinationSparePartsResList(List<InventoryWarehouseContainer> inventoryWarehouseContainerViews);

    @Mapping(target = "orderDate", source = "logistics.orderDate")
    InventoryProductionSparePartsRes toInventoryProductionSparePartsRes(InventoryWarehouseContainer inventoryWarehouseContainerView);

    List<InventoryProductionSparePartsRes> toInventoryProductionSparePartsResList(List<InventoryWarehouseContainer> inventoryWarehouseContainerViews);

    @Mapping(target = "warehouseName", source = "warehouse.name")
    @Mapping(target = "orderDate", source = "logistics.orderDate")
    @Mapping(target = "consignmentDate", source = "logistics.consignmentDate")
    InventoryConsignmentSparePartsRes toInventoryConsignmentSparePartsRes(InventoryWarehouseContainer inventoryWarehouseContainerView);

    List<InventoryConsignmentSparePartsRes> toInventoryConsignmentSparePartsResList(List<InventoryWarehouseContainer> inventoryWarehouseContainerViews);

    @Mapping(target = "orderDate", source = "logistics.orderDate")
    InventoryDepartureSparePartsRes toInventoryDepartureSparePartsRes(InventoryWarehouseContainer inventoryWarehouseContainerView);

    List<InventoryDepartureSparePartsRes> toInventoryDepartureSparePartsResList(List<InventoryWarehouseContainer> inventoryWarehouseContainerViews);

    @Mapping(target = "arrivalDate", source = "logistics.arrivalDate")
    @Mapping(target = "warehouseType", source = "warehouse.type")
    InventoryCentralWarehouseRes toInventoryCentralWarehouseRes(InventoryWarehouseContainer inventoryWarehouseContainerView);

    List<InventoryCentralWarehouseRes> toInventoryCentralWarehouseResList(List<InventoryWarehouseContainer> inventoryWarehouseContainerViews);

    InventoryItem toInventoryItemModel(CreateInventoryItemReq inventoryItemReq);

    InventoryItem.Specifications toInventoryItemModel(CreateInventoryItemReq.Specifications inventoryItemReq);

    InventoryItem.Pricing toInventoryItemModel(CreateInventoryItemReq.Pricing inventoryItemReq);

    InventoryItem.Logistics toInventoryItemModel(CreateInventoryItemReq.Logistics inventoryItemReq);
}
