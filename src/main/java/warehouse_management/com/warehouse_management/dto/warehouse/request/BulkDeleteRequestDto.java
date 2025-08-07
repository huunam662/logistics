package warehouse_management.com.warehouse_management.dto.warehouse.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BulkDeleteRequestDto(
        @NotEmpty(message = "List of warehouse IDs cannot be empty")
        List<String> warehouseIds
) {}
