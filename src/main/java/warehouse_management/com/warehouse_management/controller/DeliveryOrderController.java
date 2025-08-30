package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.delivery_order.request.*;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliveryOrderItemsDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliveryOrderPageDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliveryProductDetailsDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliverySparePartDetailsDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.model.DeliveryOrder;
import warehouse_management.com.warehouse_management.service.DeliveryOrderService;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Delivery Order")
@RequestMapping("/v1/delivery-order")
@RequiredArgsConstructor
public class DeliveryOrderController {

    private final DeliveryOrderService deliveryOrderService;


    @Operation(
            summary = "POST Tạo đơn giao hàng.",
            description = "POST Tạo đơn giao hàng."
    )
    @PostMapping
    public ResponseEntity<?> createDeliveryOrder(@Valid @RequestBody CreateDeliveryOrderDto dto) {
        DeliveryOrder created = deliveryOrderService.createDeliveryOrder(dto);
        return ResponseEntity.ok(ApiResponse.success(Map.of("deliveryOrderId", created.getId())));
    }

    @Operation(
            summary = "POST Sửa đơn giao hàng.",
            description = "POST Sửa đơn giao hàng."
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> createDeliveryOrder(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateDeliveryOrderDto dto
    ) {
        DeliveryOrder updated = deliveryOrderService.updateDeliveryOrder(new ObjectId(id), dto);
        return ResponseEntity.ok(ApiResponse.success(Map.of("deliveryOrderId", updated.getId())));
    }

    @Operation(
            summary = "GET đơn giao hàng (Phân trang).",
            description = "GET đơn giao hàng (Phân trang)."
    )
    @GetMapping("/page")
    public ResponseEntity<?> getPageDeliveryOrder(@ModelAttribute PageOptionsDto optionsDto){
        Page<DeliveryOrderPageDto> pageDto = deliveryOrderService.getPageDeliveryOrder(optionsDto);
        return ResponseEntity.ok(ApiResponse.success(new PageInfoDto<>(pageDto)));
    }

    @Operation(
            summary = "GET Sản phẩm thêm vào đơn giao hàng.",
            description = "GET Sản phẩm thêm vào đơn giao hàng."
    )
    @GetMapping("/{id}/item-ticks")
    public ResponseEntity<?> getDeliveryOrderProductTicks(
            @PathVariable("id") String id,
            @RequestParam("isSparePart") Boolean isSparePart
    ){
        DeliveryOrderItemsDto dto = deliveryOrderService.getDeliveryOrderItemTicks(new ObjectId(id), isSparePart);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(
            summary = "POST Thêm mặt hàng vào đơn giao hàng.",
            description = "POST Thêm mặt hàng vào đơn giao hàng."
    )
    @PostMapping("/add-items")
    public ResponseEntity<?> addItemsToDeliveryOrder(@Valid @RequestBody PushItemsDeliveryDto dto){
        DeliveryOrder deliveryOrder = deliveryOrderService.addItemsToDeliveryOrder(dto);
        return ResponseEntity.ok(ApiResponse.success(Map.of("deliveryOrderId", deliveryOrder.getId())));
    }

    @Operation(
            summary = "POST Thêm ghi chú mặt hàng nợ vào đơn giao hàng.",
            description = "POST Thêm ghi chú mặt hàng nợ vào đơn giao hàng."
    )
    @PostMapping("/add-notes")
    public ResponseEntity<?> addNotesToDeliveryOrder(@Valid @RequestBody PushNotesOrderDto dto){
        DeliveryOrder deliveryOrder = deliveryOrderService.addNotesToDeliveryOrder(dto);
        return ResponseEntity.ok(ApiResponse.success(Map.of("deliveryOrderId", deliveryOrder.getId())));
    }

    @Operation(
            summary = "DELETE Xóa ghi chú mặt hàng nợ trong đơn giao.",
            description = "DELETE Xóa ghi chú mặt hàng nợ trong đơn giao."
    )
    @DeleteMapping("/notes")
    public ResponseEntity<?> removeNotesInDeliveryOrder(@RequestBody @Valid DeleteNotesOrderDto dto){
        DeliveryOrder deliveryOrder = deliveryOrderService.removeNotesInDeliveryOrder(dto);
        return ResponseEntity.ok(ApiResponse.success(Map.of("deliveryOrderId", deliveryOrder.getId())));
    }

    @Operation(
            summary = "GET Chi tiết sản phẩm trong đơn giao.",
            description = "GET Chi tiết sản phẩm trong đơn giao."
    )
    @GetMapping("/{id}/details/product")
    public ResponseEntity<?> getProductDetailInDeliveryOrder(@PathVariable("id") String id){
        List<DeliveryProductDetailsDto> productDetails = deliveryOrderService.getProductDetailInDeliveryOrder(new ObjectId(id));
        return ResponseEntity.ok(ApiResponse.success(productDetails));
    }

    @Operation(
            summary = "GET Chi tiết phụ tùng trong đơn giao.",
            description = "GET Chi tiết phụ tùng trong đơn giao."
    )
    @GetMapping("/{id}/details/spare-part")
    public ResponseEntity<?> getSparePartDetailInDeliveryOrder(@PathVariable("id") String id){
        List<DeliverySparePartDetailsDto> sparePartDetails = deliveryOrderService.getSparePartDetailInDeliveryOrder(new ObjectId(id));
        return ResponseEntity.ok(ApiResponse.success(sparePartDetails));
    }

    @Operation(
            summary = "DELETE Xóa hàng trong đơn giao.",
            description = "DELETE Xóa hàng trong đơn giao."
    )
    @DeleteMapping("/items")
    public ResponseEntity<?> removeItemsFromDeliveryOrder(@RequestBody @Valid DeleteItemsOrderDto dto){
        DeliveryOrder deliveryOrder = deliveryOrderService.removeItem(dto);
        return ResponseEntity.ok(ApiResponse.success(Map.of("deliveryOrderId", deliveryOrder.getId())));
    }

    @Operation(
            summary = "PATCH Cập nhật trạng thái đơn hàng.",
            description = "PATCH Cập nhật trạng thái đơn hàng."
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateDeliveryOrderStatus(
            @PathVariable("id") String id,
            @Valid @RequestBody ChangeStatusDeliveryOrderDto dto
    ){
        DeliveryOrder deliveryOrder = deliveryOrderService.changeStatusDeliveryOrder(new ObjectId(id), dto);
        return ResponseEntity.ok(ApiResponse.success(Map.of("deliveryOrderId", deliveryOrder.getId())));
    }
}
