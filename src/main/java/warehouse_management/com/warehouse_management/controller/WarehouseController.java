package warehouse_management.com.warehouse_management.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.warehouse.request.CreateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.request.UpdateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.WarehouseResponseDto;
import warehouse_management.com.warehouse_management.service.WarehouseService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
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
}
