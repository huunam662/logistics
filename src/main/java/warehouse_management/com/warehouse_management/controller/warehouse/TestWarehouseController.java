package warehouse_management.com.warehouse_management.controller.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.annotation.Validation;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.warehouse.request.TestValidationAnnotationRequestDto;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.exceptions.errormsg.LogicErrMsg;
import warehouse_management.com.warehouse_management.exceptions.errormsg.LogicErrCode;
import warehouse_management.com.warehouse_management.repository.WarehouseRepository;
import warehouse_management.com.warehouse_management.utils.Msg;

@Controller
@RequestMapping("/v1/testwarehouses")
@Validated
public class TestWarehouseController {
    private static final Logger logger = LogManager.getLogger(TestWarehouseController.class);
    private final WarehouseRepository warehouseRepository;

    public TestWarehouseController(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @GetMapping("/greet")
    public ResponseEntity<ApiResponse<?>> greetUser(@RequestParam(name = "name") String name) {
        return ResponseEntity.ok(ApiResponse.success("Hello, " + name));
    }

    @GetMapping("/check-age")
    public ResponseEntity<String> checkAge(@RequestParam @Validation(label = "age", min = 18) int age) {
        return ResponseEntity.ok("Valid age: " + age);
    }

    @GetMapping("/check-age1")
    public ResponseEntity<String> checkAge1(@RequestParam @Min(18) int age) {
        return ResponseEntity.ok("Valid age: " + age);
    }

    @PostMapping
    public ResponseEntity<?> createWarehouse1(@RequestBody TestValidationAnnotationRequestDto request) {
        logger.warn(Msg.get(LogicErrMsg.VALUE_DUPLICATE));
//        throw LogicErrException.ofKey(LogicErrMsg.VALUE_DUPLICATE, "ID KHO");
//        throw LogicErrException.of("fail raw message");
//        throw LogicErrException.ofCode(LogicErrCode.F001, "ID KHO");
        throw LogicErrException.ofCode(LogicErrCode.F001, "ID KHO").setHttpStatus(HttpStatus.FORBIDDEN);

        // 1. Validate input
//        if (request.getName() == null || request.getCode() == null || request.getType() == null || request.getStatus() == null) {
//            return ResponseEntity.badRequest().body("Thiếu trường bắt buộc.");
//        }
//
//        // 2. Check duplicate codes
//        if (warehouseRepository.existsByCode(request.getCode() + "-XP") ||
//                warehouseRepository.existsByCode(request.getCode() + "-PT")) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Mã kho đã tồn tại.");
//        }
//
//        // 3. Build child warehouses
//        Warehouse warehouseXP = Warehouse.builder()
//                .name(request.getName() + " – Xe & Phụ kiện")
//                .code(request.getCode() + "-XP")
//                .type(request.getType())
//                .status(request.getStatus())
//                .address(request.getAddress())
//                .area(request.getArea())
//                .managedBy(request.getManagedBy())
//                .note(request.getNote())
//                .build();
//
//        Warehouse warehousePT = Warehouse.builder()
//                .name(request.getName() + " – Phụ tùng")
//                .code(request.getCode() + "-PT")
//                .type(request.getType())
//                .status(request.getStatus())
//                .address(request.getAddress())
//                .area(request.getArea())
//                .managedBy(request.getManagedBy())
//                .note(request.getNote())
//                .build();
//
//        warehouseRepository.saveAll(List.of(warehouseXP, warehousePT));

//        return ResponseEntity.ok("✅ Đã tạo 2 kho con: " + warehouseXP.getCode() + " & " + warehousePT.getCode());
//        return ResponseEntity.ok("✅ Đã tạo 2 kho con: ");
    }

}


