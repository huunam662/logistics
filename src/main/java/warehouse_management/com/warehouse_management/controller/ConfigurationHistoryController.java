package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.configuration_history.request.*;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.ConfigVehicleSpecPageDto;
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
    public ApiResponse<?> getComponentByVehicleId(
            @RequestParam("vehicleId") String vehicleId,
            @RequestParam("isSwap") Boolean isSwap
    ){
        return ApiResponse.success(configurationHistoryService.getComponentTypeToVehicleId(new ObjectId(vehicleId), isSwap));
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
            summary = "GET Các xe nâng đang sửa chữa sở hữu bộ phận.",
            description = "GET Các xe nâng đang sửa chữa sở hữu bộ phận."
    )
    @GetMapping("/component-vehicles")
    public ApiResponse<?> getVehicleByComponentTypeAndInRepair(@RequestParam("componentType") String componentType){
        return ApiResponse.success(configurationHistoryService.getVehicleByComponentTypeAndInRepair(componentType));
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
            summary = "GET Danh sách các xe nâng sẵn hàng chưa thêm vào cấu hình (Phân trang).",
            description = "GET Danh sách các xe nâng sẵn hàng chưa thêm vào cấu hình (Phân trang)."
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
            summary = "GET Lấy giá R0, R1 của Xe.",
            description = "GET Lấy giá R0, R1 của Xe."
    )
    @GetMapping("/vehicle-price")
    public ApiResponse<?> getVehiclePriceToVehicleId(
            @RequestParam("vehicleId") String vehicleId
    ){
        return ApiResponse.success(configurationHistoryService.getVehiclePriceToVehicleId(new ObjectId(vehicleId)));
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
            @RequestBody ConfigurationCompletedDto request
    ){
        configurationHistoryService.completedConfigurationVehicle(request);
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

    @Operation(
            summary = "POST Gửi yêu cầu lắp ráp bộ phận vào xe.",
            description = "POST Gửi yêu cầu lắp ráp bộ phận vào xe."
    )
    @PostMapping("/send-repair/assemble")
    public ApiResponse<?> sendAssembleVehicle(@RequestBody SendAssembleComponentDto dto){
        configurationHistoryService.sendAssembleVehicle(dto);
        return ApiResponse.success();
    }

    @Operation(
            summary = "POST Gửi yêu cầu tháo rời bộ phận ra khỏi xe.",
            description = "POST Gửi yêu cầu tháo rời bộ phận ra khỏi xe."
    )
    @PostMapping("/send-repair/disassemble")
    public ApiResponse<?> sendDisassembleVehicle(@RequestBody SendDisassembleComponentDto dto){
        configurationHistoryService.sendDisassembleVehicle(dto);
        return ApiResponse.success();
    }

    @Operation(
            summary = "POST Gửi yêu cầu thay đổi bộ phận của xe.",
            description = "POST Gửi yêu cầu thay đổi bộ phận của xe."
    )
    @PostMapping("/send-repair/swap")
    public ApiResponse<?> sendDisassembleVehicle(@RequestBody SendSwapComponentDto dto){
        configurationHistoryService.sendSwapVehicle(dto);
        return ApiResponse.success();
    }

    @Operation(
            summary = "GET Danh sách xe nâng đang sữa chữa cấu hình (Phân trang).",
            description = "GET Danh sách xe nâng đang sữa chữa cấu hình (Phân trang)."
    )
    @GetMapping("/page/repair-department")
    public ApiResponse<?> getPageVehicleConfigurationPage(@ModelAttribute PageOptionsDto optionsReq){
        return ApiResponse.success(new PageInfoDto<>(configurationHistoryService.getPageVehicleConfigurationPage(optionsReq)));
    }

    @Operation(
            summary = "GET Mã cấu hình và trạng thái cấu hình (Tháo rời).",
            description = "GET Mã cấu hình và trạng thái cấu hình (Tháo rời)."
    )
    @GetMapping("/code-status/disassemble")
    public ApiResponse<?> checkConfigurationDisassemble(
            @RequestParam("vehicleId") String vehicleId,
            @RequestParam("componentType") String componentType
    ){
        return ApiResponse.success(configurationHistoryService.checkConfigurationDisassemble(new ObjectId(vehicleId), componentType));
    }

    @Operation(
            summary = "GET Mã cấu hình và trạng thái cấu hình (Lắp ráp).",
            description = "GET Mã cấu hình và trạng thái cấu hình (Lắp ráp)."
    )
    @GetMapping("/code-status/assemble")
    public ApiResponse<?> checkConfigurationAssemble(
            @RequestParam("vehicleId") String vehicleId,
            @RequestParam("componentType") String componentType
    ){
        return ApiResponse.success(configurationHistoryService.checkConfigurationAssemble(new ObjectId(vehicleId), componentType));
    }

    @Operation(
            summary = "GET Mã cấu hình và trạng thái cấu hình (Hoán đổi).",
            description = "GET Mã cấu hình và trạng thái cấu hình (Hoán đổi)."
    )
    @GetMapping("/code-status/swap")
    public ApiResponse<?> checkConfigurationSwap(
            @RequestParam("vehicleId") String vehicleId,
            @RequestParam("componentType") String componentType
    ){
        return ApiResponse.success(configurationHistoryService.checkConfigurationSwap(new ObjectId(vehicleId), componentType));
    }

    @Operation(
            summary = "PATCH Thay đổi trạng thái cấu hình.",
            description = "PATCH Thay đổi trạng thái cấu hình."
    )
    @PatchMapping("/status")
    public ApiResponse<?> updateStatusConfiguration(
            @RequestBody UpdateStatusConfigurationDto dto
    ){
        configurationHistoryService.updateStatusConfiguration(dto);
        return ApiResponse.success();
    }
}
