package warehouse_management.com.warehouse_management.controller.warehouse;

import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import warehouse_management.com.warehouse_management.dto.warehouse.request.TestValidationAnnotationRequestDto;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.exceptions.errormsg.LogicErrMsg;
import warehouse_management.com.warehouse_management.repository.WarehouseRepository;
import warehouse_management.com.warehouse_management.utils.Msg;

@Controller
@RequestMapping("/api/v1/testwarehouses")
public class TestWarehouseController {
    private static final Logger logger = LogManager.getLogger(TestWarehouseController.class);
    private final WarehouseRepository warehouseRepository;

    public TestWarehouseController(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @PostMapping
    public ResponseEntity<?> createWarehouse1(@Valid @RequestBody TestValidationAnnotationRequestDto request) {
        logger.warn(Msg.get(LogicErrMsg.VALUE_DUPLICATE));
        throw new LogicErrException(LogicErrMsg.NOT_FOUND_BY_CODE, "Hàng hóa", "123");
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


