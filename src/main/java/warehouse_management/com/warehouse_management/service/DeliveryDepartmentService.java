package warehouse_management.com.warehouse_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.model.DeliveryDepartment;
import warehouse_management.com.warehouse_management.repository.department.DeliveryDepartmentRepository;

import java.util.List;

@Service
public class DeliveryDepartmentService {

    @Autowired
    private DeliveryDepartmentRepository deliveryDepartmentRepository;

    public List<DeliveryDepartment> getAllDeliveryDepartments() {
        return deliveryDepartmentRepository.findAll();
    }

    public DeliveryDepartment createDeliveryDepartment(DeliveryDepartment deliveryDepartment) {
        if (deliveryDepartmentRepository.existsByDepartmentName(deliveryDepartment.getDepartmentName())) {
            throw LogicErrException.of("Department name already exists");
        }
        return deliveryDepartmentRepository.save(deliveryDepartment);
    }
}