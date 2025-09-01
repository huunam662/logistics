package warehouse_management.com.warehouse_management.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.container.response.ContainerDetailsProductDto;
import warehouse_management.com.warehouse_management.dto.container.response.ContainerDetailsSparePartDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.InventoryItemToContainerDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.container.request.BulkDeleteContainerDto;
import warehouse_management.com.warehouse_management.dto.container.request.CreateContainerDto;
import warehouse_management.com.warehouse_management.dto.container.response.ContainerResponseDto;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.service.ContainerService;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Container")
@RequestMapping("/v1/containers")
@RequiredArgsConstructor
public class ContainerController {
    private final ContainerService containerService;

    @GetMapping
    @Operation(
            summary = "GET Lấy danh sách containers",
            description = "GET Lấy danh sách containers"
    )
    public ApiResponse<?> getPageContainers(
            @ModelAttribute PageOptionsDto req
    ){
        Page<ContainerResponseDto> responseDtos = containerService.getContainers(req);
        return ApiResponse.success(new PageInfoDto<>(responseDtos));
    }

    @GetMapping("/{id}/check-code")
    @Operation(
            summary = "GET Kiểm tra container code.",
            description = "GET Kiểm tra container code."
    )
    public ApiResponse<?> getContainerCode(@PathVariable("id") String id, @RequestParam("containerCode") String containerCode){
        Map<String, Boolean> result = containerService.checkIsExistsContainerCode(new ObjectId(id), containerCode);
        return ApiResponse.success(result);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Container>> createContainer(@Valid @RequestBody CreateContainerDto createDto) {
        Container newContainer = containerService.createContainer(createDto);
        ApiResponse<Container> response = ApiResponse.success(newContainer);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateContainer(@PathVariable("id") String id, @Valid @RequestBody CreateContainerDto createDto) {
        Container container = containerService.updateContainer(new ObjectId(id), createDto);

        return ResponseEntity.ok(ApiResponse.success(Map.of("containerId", container.getId())));
    }

    @PostMapping("/delete-bulk")
    public ResponseEntity<ApiResponse<Boolean>> bulkDeleteContainers(
            @Valid @RequestBody BulkDeleteContainerDto bulkDeleteRequest) {
        boolean deletedCount = containerService.bulkSoftDeleteContainers(bulkDeleteRequest.getIds());

        return ResponseEntity.ok(ApiResponse.success(deletedCount));
    }

    @DeleteMapping("/{containerId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteContainers(@PathVariable("containerId") String containerId) {
        boolean deletedCount = containerService.softDeleteContainers(containerId);

        return ResponseEntity.ok(ApiResponse.success(deletedCount));
    }

    @PostMapping("/push-items")
    @Operation(
            summary = "POST Thêm hàng hóa vào container.",
            description = "POST Thêm hàng hóa vào container."
    )
    public ResponseEntity<ApiResponse<?>> pushItems(@RequestBody InventoryItemToContainerDto req){
        Map<String, ObjectId> result = containerService.pushItems(req);
        return ResponseEntity.ok().body(ApiResponse.success(result));
    }

    @GetMapping("/{containerId}/inventory-items/product")
    @Operation(
            summary = "GET Lấy các sản phẩm trong container.",
            description = "GET Lấy các sản phẩm trong container."
    )
    public ResponseEntity<?> getInventoryItemsProduct(@PathVariable("containerId") String containerId) {
        ContainerDetailsProductDto dtos = containerService.getInventoryItemsProductToContainerId(containerId);
        return ResponseEntity.ok().body(ApiResponse.success(dtos));
    }

    @GetMapping("/{containerId}/inventory-items/spare-part")
    @Operation(
            summary = "GET Lấy các hàng hóa trong container.",
            description = "GET Lấy các hàng hóa trong container."
    )
    public ResponseEntity<?> getInventoryItemsSparePart(@PathVariable("containerId") String containerId) {
        ContainerDetailsSparePartDto dtos = containerService.getInventoryItemsSparePartToContainerId(containerId);
        return ResponseEntity.ok().body(ApiResponse.success(dtos));
    }

    @PatchMapping("/{containerId}/status")
    @Operation(
            summary = "PATCH Cập nhật trạng thái cont hàng.",
            description = "PATCH Cập nhật trạng thái cont hàng."
    )
    public ResponseEntity<ApiResponse<?>> updateContainerStatus(
            @PathVariable("containerId") String containerId,
            @Parameter(description = "[REJECTED, PENDING, APPROVED, HAD_DATE, IN_TRANSIT, UN_INSPECTED, COMPLETED]")
            @RequestParam("status") String status
    ){
        Container container = containerService.updateContainerStatus(containerId, status);
        ApiResponse<?> response = ApiResponse.success();
        response.setMessage("Cập nhật trạng thái cho Cont hàng " + container.getContainerCode() + " thành công.");
        return ResponseEntity.ok().body(response);
    }
}
