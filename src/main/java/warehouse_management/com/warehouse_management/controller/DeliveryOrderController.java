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
import warehouse_management.com.warehouse_management.dto.delivery_order.request.CreateDeliveryOrderDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.request.PushItemsDeliveryDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.request.UpdateDeliveryOrderDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliveryOrderPageDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliveryOrderProductTicksDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliveryOrderSparePartTicksDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.model.DeliveryOrder;
import warehouse_management.com.warehouse_management.service.DeliveryOrderService;

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
    @GetMapping("/{id}/product-ticks")
    public ResponseEntity<?> getDeliveryOrderProductTicks(@PathVariable("id") String id){
        DeliveryOrderProductTicksDto dto = deliveryOrderService.getDeliveryOrderProductTicks(new ObjectId(id));
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(
            summary = "GET Hàng hóa thêm vào đơn giao hàng.",
            description = "GET Hàng hóa thêm vào đơn giao hàng."
    )
    @GetMapping("/{id}/spare-part-ticks")
    public ResponseEntity<?> getDeliveryOrderSparePartTicks(@PathVariable("id") String id){
        DeliveryOrderSparePartTicksDto dto = deliveryOrderService.getDeliveryOrderSparePartTicks(new ObjectId(id));
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
}
