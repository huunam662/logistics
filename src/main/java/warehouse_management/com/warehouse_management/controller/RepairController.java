package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.configuration_history.request.AddVehicleToConfigurationDto;
import warehouse_management.com.warehouse_management.dto.configuration_history.request.ConfigurationCompletedDto;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.ConfigVehicleSpecPageDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.dto.repair.request.*;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairResponseDto;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairVehicleSpecPageDto;
import warehouse_management.com.warehouse_management.model.Repair;
import warehouse_management.com.warehouse_management.service.RepairService;
import warehouse_management.com.warehouse_management.service.RepairTransactionService;

import java.util.Map;

@RestController
@Tag(name = "Repair")
@RequestMapping("/v1/repair")
@RequiredArgsConstructor
public class RepairController {

    private final RepairService repairService;
    private final RepairTransactionService repairTransactionService;

    @Operation(
            summary = "GET Danh sách xe nâng được thêm vào sửa chữa (Phân trang).",
            description = "GET Danh sách xe nâng được thêm vào sửa chữa (Phân trang)."
    )
    @GetMapping("/page")
    public ApiResponse<?> getPageConfigVehicleSpec(@ModelAttribute PageOptionsDto optionsDto){
        return ApiResponse.success(new PageInfoDto<>(repairService.getPageRepairVehicleSpec(optionsDto)));
    }

    @Operation(
            summary = "GET Danh sách xe nâng đang sữa chữa (Phân trang).",
            description = "GET Danh sách xe nâng đang sữa chữa (Phân trang)."
    )
    @GetMapping("/page/repair-department")
    public ApiResponse<?> getPageVehicleConfigurationPage(@ModelAttribute PageOptionsDto optionsReq){
        return ApiResponse.success(new PageInfoDto<>(repairService.getPageVehicleRepairPage(optionsReq)));
    }

    @Operation(
            summary = "GET Danh sách các xe nâng sẵn hàng chưa thêm vào sửa chữa (Phân trang).",
            description = "GET Danh sách các xe nâng sẵn hàng chưa thêm vào sửa chữa (Phân trang)."
    )
    @GetMapping("/page/vehicles-to-repair")
    public ApiResponse<?> getPageVehicleInStock(@ModelAttribute PageOptionsDto optionsDto){
        return ApiResponse.success(new PageInfoDto<>(repairService.getPageVehicleInStock(optionsDto)));
    }

    @Operation(
            summary = "GET Danh sách lý do sửa chữa bộ phận.",
            description = "GET Danh sách lý do sửa chữa bộ phận."
    )
    @GetMapping("/repair-transactions")
    public ApiResponse<?> getPageVehicleConfigurationPage(
            @RequestParam("repairCode") String repairCode
    ){
        return ApiResponse.success(repairTransactionService.getRepairTransactionListToRepairCode(repairCode));
    }

    @GetMapping("/code-status/repair-or-disassemble")
    @Operation(
            summary = "GET Mã sửa chữa và trạng thái sửa chữa (Tháo rời).",
            description = "GET Mã sửa chữa và trạng thái sửa chữa (Tháo rời)."
    )
    public ApiResponse<?> checkRepairDisassemble(
            @RequestParam("vehicleId") String vehicleId,
            @RequestParam("componentType") String componentType,
            @RequestParam("isRepair") Boolean isRepair
    ) {
        return ApiResponse.success(repairService.checkRepairDisassembleOrRepair(new ObjectId(vehicleId), componentType, isRepair));
    }

    @GetMapping("/code-status/assemble")
    @Operation(
            summary = "GET Mã sửa chữa và trạng thái sửa chữa (Lắp ráp).",
            description = "GET Mã sửa chữa và trạng thái sửa chữa (Lắp ráp)."
    )
    public ApiResponse<?> checkRepairAssemble(
            @RequestParam("vehicleId") String vehicleId,
            @RequestParam("componentType") String componentType
    ) {
        return ApiResponse.success(repairService.checkRepairAssemble(new ObjectId(vehicleId), componentType));
    }

    @PostMapping("/send-repair/disassemble")
    @Operation(
            summary = "POST Gửi yêu cầu Tháo bộ phận.",
            description = "POST Gửi yêu Tháo bộ phận"
    )
    public ApiResponse<?> sendDisassembleComponent(@Valid @RequestBody SendRepairDisassembleDto dto) {
        Repair repair = repairService.sendDisassembleVehicle(dto);
        return ApiResponse.success(Map.of("repairId", repair.getId()));
    }

    @PostMapping("/send-repair/repair-component")
    @Operation(
            summary = "POST Gửi yêu cầu sửa chữa bộ phận.",
            description = "POST Gửi yêu cầu sửa chữa bộ phận."
    )
    public ApiResponse<?> sendRepairComponent(@Valid @RequestBody SendRepairComponentDto dto) {
        Repair repair = repairService.sendRepairComponentVehicle(dto);
        return ApiResponse.success(Map.of("repairId", repair.getId()));
    }

    @PostMapping("/send-repair/assemble")
    @Operation(
            summary = "POST Gửi yêu cầu Lắp ráp bộ phận.",
            description = "POST Gửi yêu cầu Lắp ráp bộ phận"
    )
    public ApiResponse<?> sendAssembleComponent(@Valid @RequestBody SendRepairAssembleDto dto) {
        Repair repair = repairService.sendAssembleVehicle(dto);
        return ApiResponse.success(Map.of("repairId", repair.getId()));
    }

    @PostMapping("/assemble")
    @Operation(
            summary = "POST Lắp ráp bộ phận.",
            description = "POST Lắp ráp bộ phận"
    )
    public ApiResponse<?> assembleComponent(@Valid @RequestBody RepairAssembleComponentDto dto) {
        repairService.repairAssembleComponent(dto);
        return ApiResponse.success();
    }

    @PostMapping("/disassemble")
    @Operation(
            summary = "POST Tháo bộ phận.",
            description = "POST Tháo bộ phận"
    )
    public ApiResponse<?> repairComponent(@Valid @RequestBody RepairDisassembleComponentDto dto) {
        repairService.repairDisassembleComponent(dto);
        return ApiResponse.success();
    }

    @PostMapping("/repair-component")
    @Operation(
            summary = "POST Sửa chữa bộ phận.",
            description = "POST Sửa chữa bộ phận"
    )
    public ApiResponse<?> disassembleComponent(@Valid @RequestBody RepairComponentDto dto) {
        repairService.repairComponentVehicle(dto);
        return ApiResponse.success();
    }

    @PostMapping("/transactions")
    @Operation(
            summary = "Tạo lý do sửa chữa",
            description = "Tạo lý do sửa chữa"
    )
    public ApiResponse<?> saveRepairTransaction(@Valid @RequestBody CreateRepairTransactionDto dto) {
        Repair repair = repairService.pushReasonForRepair(dto);
        return ApiResponse.success(Map.of("repairId", repair.getId()));
    }

    @PatchMapping("/status")
    @Operation(
            summary = "Cập nhật trạng thái đơn sửa chữa",
            description = "Cập nhật trạng thái đơn sửa chữa"
    )
    public ApiResponse<?> updateStatusRepair(@Valid @RequestBody UpdateStatusRepairDto dto) {
        repairService.updateStatusRepair(dto);
        return ApiResponse.success();
    }

    @PatchMapping("/transaction/status")
    @Operation(
            summary = "Cập nhật trạng thái lý do sửa chữa",
            description = "Cập nhật trạng thái lý do sửa chữa"
    )
    public ApiResponse<?> updateTransactionStatusRepair(@Valid @RequestBody UpdateTransactionStatusDto dto) {
        repairTransactionService.updateTransactionStatusRepair(dto);
        return ApiResponse.success();
    }

    @Operation(
            summary = "POST Thêm xe nâng vào sửa chữa.",
            description = "POST Thêm xe nâng vào sửa chữa."
    )
    @PostMapping("/add-vehicles-repair")
    public ApiResponse<?> addVehicleToRepair(@Valid @RequestBody AddVehicleToConfigurationDto request){
        repairService.addVehicleToRepair(request);
        return ApiResponse.success();
    }

    @Operation(
            summary = "POST Hoàn tất sửa chữa xe nâng.",
            description = "POST Hoàn tất sửa chữa xe nâng."
    )
    @PostMapping("/vehicle-repair-completed")
    public ApiResponse<?> completedConfigurationVehicle(
            @RequestBody ConfigurationCompletedDto request
    ){
        repairService.completedRepairVehicle(request);
        return ApiResponse.success();
    }

    @Operation(
            summary = "GET Các bộ phận bị thiếu của xe nâng.",
            description = "GET Các bộ phận bị thiếu của xe nâng."
    )
    @GetMapping("/vehicle-components-missing")
    public ApiResponse<?> getComponentMissingByVehicleId(@RequestParam("vehicleId") String vehicleId){
        return ApiResponse.success(repairService.getComponentTypeMissingToVehicleId(new ObjectId(vehicleId)));
    }

    @Operation(
            summary = "GET Các bộ phận có sẵn trong xe nâng.",
            description = "GET Các bộ phận có sẵn trong xe nâng."
    )
    @GetMapping("/vehicle-components-exists")
    public ApiResponse<?> getComponentByVehicleId(
            @RequestParam("vehicleId") String vehicleId,
            @RequestParam("isRepair") Boolean isRepair
    ){
        return ApiResponse.success(repairService.getComponentTypeToVehicleId(new ObjectId(vehicleId), isRepair));
    }

    @Operation(
            summary = "DELETE Lý do sưa chữa.",
            description = "DELETE Lý do sưa chữa."
    )
    @DeleteMapping("/transactions")
    public ApiResponse<?> deleteRepairTransaction(
            @Valid @RequestBody RepairTransactionIdListDto dto
    ){
        repairTransactionService.softDeleteRepairTransaction(dto);
        return ApiResponse.success();
    }

    @Operation(
            summary = "GET Lịch sử sửa chữa của một xe nâng.",
            description = "GET Lịch sử sửa chữa của một xe nâng."
    )
    @GetMapping("/vehicle-history")
    public ApiResponse<?> getRepairHistoryToVehicleId(@RequestParam("vehicleId") String vehicleId){
        return ApiResponse.success(repairService.getRepairHistoryToVehicleId(new ObjectId(vehicleId)));
    }
}
