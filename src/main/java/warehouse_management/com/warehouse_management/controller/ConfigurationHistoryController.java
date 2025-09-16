package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.configuration_history.request.AddVehicleToConfigurationDto;
import warehouse_management.com.warehouse_management.dto.configuration_history.request.AssemblePartDto;
import warehouse_management.com.warehouse_management.dto.configuration_history.request.DropPartDto;
import warehouse_management.com.warehouse_management.dto.configuration_history.request.VehiclePartSwapDto;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.ConfigVehicleSpecPageDto;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.SwapVehiclePricingDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.service.ConfigurationHistoryService;
import java.util.List;

@RestController
@Tag(name = "Configurations Vehicle")
@RequestMapping("/v1/configurations")
@RequiredArgsConstructor
public class ConfigurationHistoryController {

    private final ConfigurationHistoryService configurationHistoryService;


    @Operation(
            summary = "POST Hoán đổi cấu hình xe nâng.",
            description = "POST Hoán đổi cấu hình xe nâng."
    )
    @PostMapping("/vehicle-part-swap")
    public ApiResponse<?> swapItems(@RequestBody VehiclePartSwapDto vehiclePartSwapRequest) {
        return ApiResponse.success(configurationHistoryService.swapItems(vehiclePartSwapRequest));
    }

    @Operation(
            summary = "POST Tháo rời / Rớt bộ phận xe nâng.",
            description = "POST Tháo rời / Rớt bộ phận xe nâng."
    )
    @PostMapping("/drop-part")
    public ApiResponse<?> dropPart(@RequestBody DropPartDto dropPartRequest) {
        return ApiResponse.success(configurationHistoryService.dropComponent(dropPartRequest));
    }

    @Operation(
            summary = "POST Lắp ráp bộ phận xe nâng.",
            description = "POST Lắp ráp bộ phận xe nâng."
    )
    @PostMapping("/assemble-part")
    public ApiResponse<?> assemblePart(@RequestBody AssemblePartDto assemblePartRequest) {
        return ApiResponse.success(configurationHistoryService.assembleComponent(assemblePartRequest));
    }

    @Operation(
            summary = "GET Lịch sử cấu hình của một xe nâng.",
            description = "GET Lịch sử cấu hình của một xe nâng."
    )
    @GetMapping("/vehicle-history")
    public ApiResponse<?> getConfigurationHistoryToVehicleId(@RequestParam("vehicleId") String vehicleId){
        return ApiResponse.success(configurationHistoryService.getConfigurationHistoryToVehicleId(new ObjectId(vehicleId)));
    }

    @Operation(
            summary = "GET Danh sách xe nâng được thêm vào cấu hình (Phân trang).",
            description = "GET Danh sách xe nâng được thêm vào cấu hình (Phân trang)."
    )
    @GetMapping("/page")
    public ApiResponse<?> getPageConfigVehicleSpec(@ModelAttribute PageOptionsDto optionsDto){
        Page<ConfigVehicleSpecPageDto> page = configurationHistoryService.getPageConfigVehicleSpec(optionsDto);
        return ApiResponse.success(new PageInfoDto<>(page));
    }

    @Operation(
            summary = "GET Các bộ phận có sẵn trong xe nâng.",
            description = "GET Các bộ phận có sẵn trong xe nâng."
    )
    @GetMapping("/vehicle-components-exists")
    public ApiResponse<?> getComponentByVehicleId(@RequestParam("vehicleId") String vehicleId){
        return ApiResponse.success(configurationHistoryService.getComponentTypeToVehicleId(new ObjectId(vehicleId)));
    }

    @Operation(
            summary = "GET Các bộ phận bị thiếu của xe nâng.",
            description = "GET Các bộ phận bị thiếu của xe nâng."
    )
    @GetMapping("/vehicle-components-missing")
    public ApiResponse<?> getComponentMissingByVehicleId(@RequestParam("vehicleId") String vehicleId){
        return ApiResponse.success(configurationHistoryService.getComponentTypeMissingToVehicleId(new ObjectId(vehicleId)));
    }

    @Operation(
            summary = "GET Các kho chứa bộ phận bị thiếu của xe nâng.",
            description = "GET Các kho chứa bộ phận bị thiếu của xe nâng."
    )
    @GetMapping("/warehouse-components")
    public ApiResponse<?> getWarehouseContainsComponent(
           @RequestParam("componentType") String componentType
    ){
        return ApiResponse.success(configurationHistoryService.getWarehouseContainsComponent(componentType));
    }

    @Operation(
            summary = "GET Các bộ phận chung của 2 xe nâng.",
            description = "GET Các bộ phận chung của 2 xe nâng."
    )
    @GetMapping("/components-common")
    public ApiResponse<?> getComponentsTypeCommon(
            @RequestParam("vehicleLeftId") String vehicleLeftId,
            @RequestParam("vehicleRightId") String vehicleRightId
    ){
        return ApiResponse.success(configurationHistoryService.getComponentsTypeCommon(new ObjectId(vehicleLeftId), new ObjectId(vehicleRightId)));
    }

    @Operation(
            summary = "GET Các xe nâng sở hữu bộ phận.",
            description = "GET Các xe nâng sở hữu bộ phận."
    )
    @GetMapping("/component-vehicles")
    public ApiResponse<?> getVehicleByComponentType(@RequestParam("componentType") String componentType){
        return ApiResponse.success(configurationHistoryService.getVehicleByComponentType(componentType));
    }

    @Operation(
            summary = "POST Thêm xe nâng vào cấu hình.",
            description = "POST Thêm xe nâng vào cấu hình."
    )
    @PostMapping("/add-vehicles-configuration")
    public ApiResponse<?> addVehicleToConfiguration(@RequestBody AddVehicleToConfigurationDto request){
        configurationHistoryService.addVehicleToConfiguration(request);
        return ApiResponse.success();
    }

    @Operation(
            summary = "GET Danh sách các xe nâng sẵn hàng chưa thêm vào cấu hình.",
            description = "GET Danh sách các xe nâng sẵn hàng chưa thêm vào cấu hình."
    )
    @GetMapping("/page/vehicles-to-configuration")
    public ApiResponse<?> getPageVehicleInStock(@ModelAttribute PageOptionsDto optionsDto){
        return ApiResponse.success(new PageInfoDto<>(configurationHistoryService.getPageVehicleInStock(optionsDto)));
    }

    @Operation(
            summary = "GET Lấy mã sản phẩm và giá R0, R1, bán thực tế của bộ phận.",
            description = "GET Lấy mã sản phẩm và giá R0, R1, bán thực tế của bộ phận."
    )
    @GetMapping("/component-code-price")
    public ApiResponse<?> getCodeAndPriceToVehicleIdAndComponentType(
            @RequestParam("vehicleId") String vehicleId,
            @RequestParam("componentType") String componentType
    ){
        return ApiResponse.success(configurationHistoryService.getCodeAndPriceToVehicleIdAndComponentType(new ObjectId(vehicleId), componentType));

    }

    @Operation(
            summary = "POST Hoán đổi cấu hình cho nhiều xe nâng.",
            description = "POST Hoán đổi cấu hình cho nhiều xe nâng."
    )
    @PostMapping("/vehicle-part-swap/multiple")
    public ApiResponse<?> swapMultipleVehicle(@RequestBody List<VehiclePartSwapDto> request){
        return ApiResponse.success(configurationHistoryService.swapMultipleVehicle(request));
    }

    @Operation(
            summary = "POST Hoàn tất cấu hình xe nâng.",
            description = "POST Hoàn tất cấu hình xe nâng."
    )
    @PostMapping("/vehicle-configuration-completed")
    public ApiResponse<?> completedConfigurationVehicle(
            @RequestParam("vehicleId") String vehicleId
    ){
        configurationHistoryService.completedConfigurationVehicle(new ObjectId(vehicleId));
        return ApiResponse.success();
    }

    @Operation(
            summary = "GET Lấy giá R0, R1 của xe trái và phải.",
            description = "GET Lấy giá R0, R1 của xe trái và phải."
    )
    @GetMapping("/vehicles-pricing")
    public ApiResponse<?> getSwapVehiclePricing(
            @RequestParam("vehicleLeftId") String vehicleLeftId,
            @RequestParam("vehicleRightId") String vehicleRightId
    ){
        return ApiResponse.success(configurationHistoryService.getSwapVehiclePricing(new ObjectId(vehicleLeftId), new ObjectId(vehicleRightId)));
    }
}
