package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.common.pagination.req.PageOptionsReq;
import warehouse_management.com.warehouse_management.common.pagination.res.PageInfoRes;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.Inventory.response.*;
import warehouse_management.com.warehouse_management.dto.warehouse.request.CreateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.request.UpdateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.WarehouseResponseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.WarehouseUserRes;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.mapper.WarehouseMapper;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.dto.Inventory.view.InventoryWarehouseContainerView;
import warehouse_management.com.warehouse_management.service.WarehouseService;
import java.util.List;

@RestController
@RequestMapping("/v1/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Autowired
    public WarehouseController(WarehouseService service) {
        this.warehouseService = service;
    }

    @PostMapping
    public ApiResponse<?> createWarehouse(@Valid @RequestBody CreateWarehouseDto createDto) {
        WarehouseResponseDto createdWarehouse = warehouseService.createWarehouse(createDto);
        return ApiResponse.success(createdWarehouse);
    }

    @GetMapping
    public ApiResponse<?>  getAllWarehouses() {
        List<WarehouseResponseDto> warehouses = warehouseService.getAllWarehouses();
        return ApiResponse.success(warehouses);
    }

    @GetMapping("/{id}")
    public ApiResponse<?>  getWarehouseById(@PathVariable("id") String id) {
        WarehouseResponseDto warehouse = warehouseService.getWarehouseById(id);
        return ApiResponse.success(warehouse);
    }

    @PutMapping("/{id}")
    public ApiResponse<?>  updateWarehouse(@PathVariable("id") String id, @Valid @RequestBody UpdateWarehouseDto updateDto) {
        WarehouseResponseDto updatedWarehouse = warehouseService.updateWarehouse(id, updateDto);
        return ApiResponse.success(updatedWarehouse);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?>  deleteWarehouse(@PathVariable("id") String id) {
        warehouseService.deleteWarehouse(id);
        return ApiResponse.success();
    }

    @GetMapping("/page")
    @Operation(
            summary = "GET data kho hàng. (phân trang)",
            description = "GET data kho hàng. (phân trang)"
    )
    public ApiResponse<?> getPageWarehouse(
            @ModelAttribute PageOptionsReq optionsReq
    ) {
//        Page<WarehouseView> warehouseView = warehouseService.getPageWarehouse(optionsReq);
//        List<WarehouseView> warehouseViewsList = warehouseView.getContent();
//        List<WarehouseUserRes> warehouseResList = WarehouseMapper.INSTANCE.toWarehouseResList(warehouseViewsList);
//        Page<WarehouseUserRes> pageRes = new PageImpl<>(warehouseResList, warehouseView.getPageable(), warehouseView.getTotalElements());
//        return ResultApiRes.success(new PageInfoRes<>(pageRes), request);

        Page<Warehouse> warehousePage = warehouseService.getPageWarehouse(optionsReq);
        List<Warehouse> warehouseList = warehousePage.getContent();
        List<WarehouseUserRes> warehouseUserResList = WarehouseMapper.INSTANCE.toWarehouseResList(warehouseList);
        Page<WarehouseUserRes> warehouseUserResPage = new PageImpl<>(warehouseUserResList, warehousePage.getPageable(), warehousePage.getTotalElements());
        return ApiResponse.success(new PageInfoRes<>(warehouseUserResPage));
    }

    @GetMapping("/{warehouseId}/page/product/inventory-production")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho chờ sản xuất. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho chờ sản xuất. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryProduction(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryWarehouseContainerView> inventoryWarehouseView = warehouseService.getPageInventoryProduction(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryWarehouseView.getContent();
        List<InventoryProductionRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryProductionResList(inventoryItems);
        Page<InventoryProductionRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryWarehouseView.getPageable(), inventoryWarehouseView.getTotalElements());
        return ApiResponse.success(new PageInfoRes<>(pageRes));
    }

    @GetMapping("/{warehouseId}/page/product/inventory-departure")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho đi. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho đi. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryDeparture(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryWarehouseContainerView> inventoryItemPage = warehouseService.getPageInventoryDeparture(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryItemPage.getContent();
        List<InventoryDepartureRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryDepartureResList(inventoryItems);
        Page<InventoryDepartureRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryItemPage.getPageable(), inventoryItemPage.getTotalElements());
        return ApiResponse.success(new PageInfoRes<>(pageRes));
    }

    @GetMapping("/{warehouseId}/page/product/inventory-destination")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho đích. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho đích. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryDestination(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryWarehouseContainerView> inventoryItemPage = warehouseService.getPageInventoryDestination(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryItemPage.getContent();
        List<InventoryDestinationRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryDestinationResList(inventoryItems);
        Page<InventoryDestinationRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryItemPage.getPageable(), inventoryItemPage.getTotalElements());
        return ApiResponse.success(new PageInfoRes<>(pageRes));
    }

    @GetMapping("/{warehouseId}/page/product/inventory-consignment")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho ký gửi. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho ký gửi. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryConsignment(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryWarehouseContainerView> inventoryItemPage = warehouseService.getPageInventoryConsignment(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryItemPage.getContent();
        List<InventoryConsignmentRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryConsignmentResList(inventoryItems);
        Page<InventoryConsignmentRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryItemPage.getPageable(), inventoryItemPage.getTotalElements());
        return ApiResponse.success(new PageInfoRes<>(pageRes));
    }

    @GetMapping("/{warehouseId}/page/spare-parts/inventory-consignment")
    @Operation(
            summary = "GET data Phụ tùng của kho ký gửi. (phân trang)",
            description = "GET data Phụ tùng của kho ký gửi. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryConsignmentSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryWarehouseContainerView> inventoryItemPage = warehouseService.getPageInventorySparePartsConsignment(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryItemPage.getContent();
        List<InventoryConsignmentSparePartsRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryConsignmentSparePartsResList(inventoryItems);
        Page<InventoryConsignmentSparePartsRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryItemPage.getPageable(), inventoryItemPage.getTotalElements());
        return ApiResponse.success(new PageInfoRes<>(pageRes));
    }


    @GetMapping("/{warehouseId}/page/spare-parts/inventory-production")
    @Operation(
            summary = "GET data Phụ tùng của kho chờ sản xuất. (phân trang)",
            description = "GET data Phụ tùng của kho chờ sản xuất. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryProductionSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryWarehouseContainerView> inventoryWarehouseView = warehouseService.getPageInventorySparePartsProduction(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryWarehouseView.getContent();
        List<InventoryProductionSparePartsRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryProductionSparePartsResList(inventoryItems);
        Page<InventoryProductionSparePartsRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryWarehouseView.getPageable(), inventoryWarehouseView.getTotalElements());
        return ApiResponse.success(new PageInfoRes<>(pageRes));
    }

    @GetMapping("/{warehouseId}/page/spare-parts/inventory-departure")
    @Operation(
            summary = "GET data Phụ tùng của kho đi. (phân trang)",
            description = "GET data Phụ tùng của kho đi. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryDepartureSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryWarehouseContainerView> inventoryItemPage = warehouseService.getPageInventorySparePartsDeparture(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryItemPage.getContent();
        List<InventoryDepartureSparePartsRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryDepartureSparePartsResList(inventoryItems);
        Page<InventoryDepartureSparePartsRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryItemPage.getPageable(), inventoryItemPage.getTotalElements());
        return ApiResponse.success(new PageInfoRes<>(pageRes));
    }

    @GetMapping("/{warehouseId}/page/spare-parts/inventory-destination")
    @Operation(
            summary = "GET data Phụ tùng của kho đích. (phân trang)",
            description = "GET data Phụ tùng của kho đích. (phân trang)"
    )
    public ApiResponse<?> getPageInventoryDestinationSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq
    ) {
        Page<InventoryWarehouseContainerView> inventoryItemPage = warehouseService.getPageInventorySparePartsDestination(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryItemPage.getContent();
        List<InventoryDestinationSparePartsRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryDestinationSparePartsResList(inventoryItems);
        Page<InventoryDestinationSparePartsRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryItemPage.getPageable(), inventoryItemPage.getTotalElements());
        return ApiResponse.success(new PageInfoRes<>(pageRes));
    }
}
