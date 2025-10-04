package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.dto.repair.request.UpdateTransactionStatusDto;
import warehouse_management.com.warehouse_management.enumerate.ConfigurationStatus;
import warehouse_management.com.warehouse_management.enumerate.RepairStatus;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.model.RepairTransaction;
import warehouse_management.com.warehouse_management.repository.repair_transaction.RepairTransactionRepository;
import warehouse_management.com.warehouse_management.security.CustomUserDetail;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepairTransactionService {

    private final RepairTransactionRepository repairTransactionRepository;
    private final CustomAuthentication customAuthentication;

    public RepairTransaction getToId(ObjectId id) {
        RepairTransaction repairTransaction = repairTransactionRepository.findById(id).orElse(null);
        if(repairTransaction == null || repairTransaction.getDeletedAt() != null)
            throw LogicErrException.of("Lý do sửa chữa hiện không tồn tại");

        return repairTransaction;
    }

    @Transactional
    public void updateTransactionStatusRepair(UpdateTransactionStatusDto dto){

        RepairTransaction repairTransaction = getToId(new ObjectId(dto.getRepairTransactionId()));

        CustomUserDetail customUserDetail = customAuthentication.getUserOrThrow();

        repairTransactionRepository.updateIsRepaired(repairTransaction.getRepairId(), dto.getIsRepaired(), customUserDetail.getFullName());
    }

    public List<RepairTransaction> getRepairTransactionListToRepairId(ObjectId repairId) {
        return repairTransactionRepository.findAllByRepairId(repairId);
    }
}
