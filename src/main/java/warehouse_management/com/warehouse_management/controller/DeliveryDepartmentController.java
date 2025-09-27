package warehouse_management.com.warehouse_management.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import warehouse_management.com.warehouse_management.model.DeliveryDepartment;
import warehouse_management.com.warehouse_management.service.DeliveryDepartmentService;

import java.util.List;

@RestController
@RequestMapping("/delivery-departments")
public class DeliveryDepartmentController {

    @Autowired
    private DeliveryDepartmentService deliveryDepartmentService;

    @GetMapping
    public ResponseEntity<List<DeliveryDepartment>> getAllDeliveryDepartments() {
        try {
            List<DeliveryDepartment> departments = deliveryDepartmentService.getAllDeliveryDepartments();
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<DeliveryDepartment> createDeliveryDepartment(@Valid @RequestBody DeliveryDepartment deliveryDepartment) {
        try {
            DeliveryDepartment createdDepartment = deliveryDepartmentService.createDeliveryDepartment(deliveryDepartment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
