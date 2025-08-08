package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import warehouse_management.com.warehouse_management.dto.Inventory.request.CreateInventoryItemReq;
import warehouse_management.com.warehouse_management.dto.Inventory_item.response.*;
import warehouse_management.com.warehouse_management.dto.Inventory_item.view.InventoryWarehouseContainerView;
import warehouse_management.com.warehouse_management.model.InventoryItem;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface InventoryItemMapper {

    InventoryItemMapper INSTANCE = Mappers.getMapper(InventoryItemMapper.class);

    @Mapping(target = "orderDate", source = "logistics.orderDate")
    InventoryProductionRes toInventoryProductionRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryProductionRes> toInventoryProductionResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    @Mapping(target = "orderDate", source = "logistics.orderDate")
    @Mapping(target = "arrivalDate", source = "logistics.arrivalDate")
    @Mapping(target = "container.toWarehouse", source = "container.toWarehouse.name")
    InventoryDepartureRes toInventoryDepartureRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryDepartureRes> toInventoryDepartureResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    @Mapping(target = "arrivalDate", source = "logistics.arrivalDate")
    InventoryDestinationRes toInventoryDestinationRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryDestinationRes> toInventoryDestinationResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    @Mapping(target = "arrivalDate", source = "logistics.arrivalDate")
    @Mapping(target = "consignmentDate", source = "logistics.consignmentDate")
    InventoryConsignmentRes toInventoryConsignmentRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryConsignmentRes> toInventoryConsignmentResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    @Mapping(target = "orderDate", source = "logistics.orderDate")
    InventoryDestinationSparePartsRes toInventoryDestinationSparePartsRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryDestinationSparePartsRes> toInventoryDestinationSparePartsResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    @Mapping(target = "orderDate", source = "logistics.orderDate")
    InventoryProductionSparePartsRes toInventoryProductionSparePartsRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryProductionSparePartsRes> toInventoryProductionSparePartsResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    @Mapping(target = "warehouseName", source = "warehouse.name")
    @Mapping(target = "orderDate", source = "logistics.orderDate")
    @Mapping(target = "consignmentDate", source = "logistics.consignmentDate")
    InventoryConsignmentSparePartsRes toInventoryConsignmentSparePartsRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryConsignmentSparePartsRes> toInventoryConsignmentSparePartsResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    @Mapping(target = "orderDate", source = "logistics.orderDate")
    InventoryDepartureSparePartsRes toInventoryDepartureSparePartsRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryDepartureSparePartsRes> toInventoryDepartureSparePartsResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);

    @Mapping(target = "arrivalDate", source = "logistics.arrivalDate")
    @Mapping(target = "warehouseType", source = "warehouse.type")
    InventoryCentralWarehouseRes toInventoryCentralWarehouseRes(InventoryWarehouseContainerView inventoryWarehouseContainerView);

    List<InventoryCentralWarehouseRes> toInventoryCentralWarehouseResList(List<InventoryWarehouseContainerView> inventoryWarehouseContainerViews);


    InventoryItem toInventoryItemModel(CreateInventoryItemReq inventoryItemReq);

    InventoryItem.Specifications toInventoryItemModel(CreateInventoryItemReq.Specifications inventoryItemReq);

    InventoryItem.Pricing toInventoryItemModel(CreateInventoryItemReq.Pricing inventoryItemReq);

    InventoryItem.Logistics toInventoryItemModel(CreateInventoryItemReq.Logistics inventoryItemReq);
}
