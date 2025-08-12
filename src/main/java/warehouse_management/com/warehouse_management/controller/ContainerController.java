package warehouse_management.com.warehouse_management.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.container.request.BulkDeleteContainerDto;
import warehouse_management.com.warehouse_management.dto.container.request.CreateContainerDto;
import warehouse_management.com.warehouse_management.dto.container.response.ContainerResponseDto;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.service.ContainerService;

@RestController
@Tag(name = "Container")
@RequestMapping("/v1/containers")
@RequiredArgsConstructor
public class ContainerController {
    private final ContainerService containerService;

    @GetMapping()
    @Operation(
            summary = "GET Lấy danh sách containers",
            description = "GET Lấy danh sách containers"
    )
    public ApiResponse<?> getInventoryInStockPoNumbers(
            @ModelAttribute PageOptionsDto req
    ){
        Page<ContainerResponseDto> responseDtos = containerService.getContainers(req);
        return ApiResponse.success(new PageInfoDto<>(responseDtos));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Container>> createContainer(@Valid @RequestBody CreateContainerDto createDto) {
        Container newContainer = containerService.createContainer(createDto);
        ApiResponse<Container> response = ApiResponse.success(newContainer);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContainer(@PathVariable String id) {
        containerService.softDeleteContainer(new ObjectId(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete-bulk")
    public ResponseEntity<ApiResponse<Boolean>> bulkDeleteContainers(
            @Valid @RequestBody BulkDeleteContainerDto bulkDeleteRequest) {
        boolean deletedCount = containerService.bulkSoftDeleteContainers(bulkDeleteRequest.getIds());

        return ResponseEntity.ok(ApiResponse.success(deletedCount));
    }
}
