package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.delivery_order.request.CreateDeliveryOrderDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.request.UpdateDeliveryOrderDto;
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

}
