package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.configuration_vehicle.request.AssemblePartRequest;
import warehouse_management.com.warehouse_management.dto.configuration_vehicle.request.DropPartRequest;
import warehouse_management.com.warehouse_management.dto.configuration_vehicle.request.VehiclePartSwapRequest;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryProductDetailsDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.model.ConfigurationHistory;
import warehouse_management.com.warehouse_management.repository.configuration_history.ConfigurationHistoryRepository;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.service.ConfigurationVehicleService;

import java.util.List;

@RestController
@Tag(name = "Configurations Vehicle")
@RequestMapping("/v1/configurations")

public class ConfigurationVehicleController {
    private final ConfigurationVehicleService configurationHistoryService;
    private final ConfigurationHistoryRepository configurationHistoryRepository;
    private final InventoryItemRepository inventoryItemRepository;

    public ConfigurationVehicleController(ConfigurationVehicleService configurationHistoryService, ConfigurationHistoryRepository configurationHistoryRepository, InventoryItemRepository inventoryItemRepository) {
        this.configurationHistoryService = configurationHistoryService;
        this.configurationHistoryRepository = configurationHistoryRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }


    @GetMapping()
    public ApiResponse<?> getAllConfigurations(@ModelAttribute PageOptionsDto optionsReq) {
        Page<ConfigurationHistory> lst = configurationHistoryRepository.findPageCH(optionsReq);
        return ApiResponse.success(new PageInfoDto<>(lst));
    }
    @GetMapping("/currents")
    public ApiResponse<?> getAllCurrentConfigurations(@ModelAttribute PageOptionsDto optionsReq) {
        Page<ConfigurationHistory> lst = configurationHistoryRepository.findPageCHCurrent(optionsReq);
        return ApiResponse.success(new PageInfoDto<>(lst));
    }

    @GetMapping("/vehicle-part-swap/items")
    public ApiResponse<?> getAllVehicle(@ModelAttribute PageOptionsDto optionsReq) {
        List<InventoryProductDetailsDto> lst = inventoryItemRepository.findVehicles(optionsReq);
        return ApiResponse.success(lst);
    }

    @PostMapping("/vehicle-part-swap")
    public ApiResponse<?> swapItems(@RequestBody  VehiclePartSwapRequest vehiclePartSwapRequest) {
        return ApiResponse.success(configurationHistoryService.swapItems(vehiclePartSwapRequest));
    }

    @PostMapping("/drop-part")
    public ApiResponse<?> dropPart(@RequestBody DropPartRequest dropPartRequest) {
        return ApiResponse.success(configurationHistoryService.dropComponent(dropPartRequest));
    }

    @PostMapping("/drop-part")
    public ApiResponse<?> assemblePart(@RequestBody AssemblePartRequest assemblePartRequest) {
        return ApiResponse.success(configurationHistoryService.assembleComponent(assemblePartRequest));
    }

}
