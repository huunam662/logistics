package warehouse_management.com.warehouse_management.module.warehouse.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.common.ResultApiRes;
import warehouse_management.com.warehouse_management.common.pagination.req.PageOptionsReq;
import warehouse_management.com.warehouse_management.common.pagination.res.PageInfoRes;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.mapper.WarehouseMapper;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.module.warehouse.dto.response.*;
import warehouse_management.com.warehouse_management.module.warehouse.dto.view.InventoryWarehouseContainerView;
import warehouse_management.com.warehouse_management.module.warehouse.dto.view.WarehouseView;
import warehouse_management.com.warehouse_management.service.WarehouseService;
import java.util.List;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    @Autowired private WarehouseService warehouseService;

    @GetMapping("/page")
    @Operation(
            summary = "GET data kho hàng. (phân trang)",
            description = "GET data kho hàng. (phân trang)"
    )
    public ResultApiRes getPageWarehouse(
            @ModelAttribute PageOptionsReq optionsReq,
            HttpServletRequest request
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
        return ResultApiRes.success(new PageInfoRes<>(warehouseUserResPage), request);
    }

    @GetMapping("/{warehouseId}/page/product/inventory-production")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho chờ sản xuất. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho chờ sản xuất. (phân trang)"
    )
    public ResultApiRes getPageInventoryProduction(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq,
            HttpServletRequest request
    ) throws BadRequestException {
        Page<InventoryWarehouseContainerView> inventoryWarehouseView = warehouseService.getPageInventoryProduction(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryWarehouseView.getContent();
        List<InventoryProductionRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryProductionResList(inventoryItems);
        Page<InventoryProductionRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryWarehouseView.getPageable(), inventoryWarehouseView.getTotalElements());
        return ResultApiRes.success(new PageInfoRes<>(pageRes), request);
    }

    @GetMapping("/{warehouseId}/page/product/inventory-departure")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho đi. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho đi. (phân trang)"
    )
    public ResultApiRes getPageInventoryDeparture(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq,
            HttpServletRequest request
    ) throws BadRequestException {
        Page<InventoryWarehouseContainerView> inventoryItemPage = warehouseService.getPageInventoryDeparture(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryItemPage.getContent();
        List<InventoryDepartureRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryDepartureResList(inventoryItems);
        Page<InventoryDepartureRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryItemPage.getPageable(), inventoryItemPage.getTotalElements());
        return ResultApiRes.success(new PageInfoRes<>(pageRes), request);
    }

    @GetMapping("/{warehouseId}/page/product/inventory-destination")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho đích. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho đích. (phân trang)"
    )
    public ResultApiRes getPageInventoryDestination(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq,
            HttpServletRequest request
    ) throws BadRequestException {
        Page<InventoryWarehouseContainerView> inventoryItemPage = warehouseService.getPageInventoryDestination(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryItemPage.getContent();
        List<InventoryDestinationRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryDestinationResList(inventoryItems);
        Page<InventoryDestinationRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryItemPage.getPageable(), inventoryItemPage.getTotalElements());
        return ResultApiRes.success(new PageInfoRes<>(pageRes), request);
    }

    @GetMapping("/{warehouseId}/page/product/inventory-consignment")
    @Operation(
            summary = "GET data Xe & Phụ kiện của kho ký gửi. (phân trang)",
            description = "GET data Xe & Phụ kiện của kho ký gửi. (phân trang)"
    )
    public ResultApiRes getPageInventoryConsignment(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq,
            HttpServletRequest request
    ) throws BadRequestException {
        Page<InventoryWarehouseContainerView> inventoryItemPage = warehouseService.getPageInventoryDestination(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryItemPage.getContent();
        List<InventoryConsignmentRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryConsignmentResList(inventoryItems);
        Page<InventoryConsignmentRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryItemPage.getPageable(), inventoryItemPage.getTotalElements());
        return ResultApiRes.success(new PageInfoRes<>(pageRes), request);
    }

    @GetMapping("/{warehouseId}/page/spare-parts/inventory-consignment")
    @Operation(
            summary = "GET data Phụ tùng của kho ký gửi. (phân trang)",
            description = "GET data Phụ tùng của kho ký gửi. (phân trang)"
    )
    public ResultApiRes getPageInventoryConsignmentSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq,
            HttpServletRequest request
    ) throws BadRequestException {
        Page<InventoryWarehouseContainerView> inventoryItemPage = warehouseService.getPageInventorySparePartsConsignment(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryItemPage.getContent();
        List<InventoryConsignmentSparePartsRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryConsignmentSparePartsResList(inventoryItems);
        Page<InventoryConsignmentSparePartsRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryItemPage.getPageable(), inventoryItemPage.getTotalElements());
        return ResultApiRes.success(new PageInfoRes<>(pageRes), request);
    }


    @GetMapping("/{warehouseId}/page/spare-parts/inventory-production")
    @Operation(
            summary = "GET data Phụ tùng của kho chờ sản xuất. (phân trang)",
            description = "GET data Phụ tùng của kho chờ sản xuất. (phân trang)"
    )
    public ResultApiRes getPageInventoryProductionSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq,
            HttpServletRequest request
    ) throws BadRequestException {
        Page<InventoryWarehouseContainerView> inventoryWarehouseView = warehouseService.getPageInventorySparePartsProduction(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryWarehouseView.getContent();
        List<InventoryProductionSparePartsRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryProductionSparePartsResList(inventoryItems);
        Page<InventoryProductionSparePartsRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryWarehouseView.getPageable(), inventoryWarehouseView.getTotalElements());
        return ResultApiRes.success(new PageInfoRes<>(pageRes), request);
    }

    @GetMapping("/{warehouseId}/page/spare-parts/inventory-departure")
    @Operation(
            summary = "GET data Phụ tùng của kho đi. (phân trang)",
            description = "GET data Phụ tùng của kho đi. (phân trang)"
    )
    public ResultApiRes getPageInventoryDepartureSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq,
            HttpServletRequest request
    ) throws BadRequestException {
        Page<InventoryWarehouseContainerView> inventoryItemPage = warehouseService.getPageInventorySparePartsDeparture(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryItemPage.getContent();
        List<InventoryDepartureSparePartsRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryDepartureSparePartsResList(inventoryItems);
        Page<InventoryDepartureSparePartsRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryItemPage.getPageable(), inventoryItemPage.getTotalElements());
        return ResultApiRes.success(new PageInfoRes<>(pageRes), request);
    }

    @GetMapping("/{warehouseId}/page/spare-parts/inventory-destination")
    @Operation(
            summary = "GET data Phụ tùng của kho đích. (phân trang)",
            description = "GET data Phụ tùng của kho đích. (phân trang)"
    )
    public ResultApiRes getPageInventoryDestinationSpareParts(
            @PathVariable("warehouseId") String warehouseId,
            @ModelAttribute PageOptionsReq optionsReq,
            HttpServletRequest request
    ) throws BadRequestException {
        Page<InventoryWarehouseContainerView> inventoryItemPage = warehouseService.getPageInventorySparePartsDestination(new ObjectId(warehouseId), optionsReq);
        List<InventoryWarehouseContainerView> inventoryItems = inventoryItemPage.getContent();
        List<InventoryDestinationSparePartsRes> warehouseInventoryResList = InventoryItemMapper.INSTANCE.toInventoryDestinationSparePartsResList(inventoryItems);
        Page<InventoryDestinationSparePartsRes> pageRes = new PageImpl<>(warehouseInventoryResList, inventoryItemPage.getPageable(), inventoryItemPage.getTotalElements());
        return ResultApiRes.success(new PageInfoRes<>(pageRes), request);
    }
}
