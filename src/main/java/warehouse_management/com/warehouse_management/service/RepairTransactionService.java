package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.dto.repair.request.RepairTransactionIdListDto;
import warehouse_management.com.warehouse_management.dto.repair.request.UpdateTransactionStatusDto;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairTransactionDto;
import warehouse_management.com.warehouse_management.enumerate.ConfigurationStatus;
import warehouse_management.com.warehouse_management.enumerate.RepairStatus;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.RepairTransactionMapper;
import warehouse_management.com.warehouse_management.model.Repair;
import warehouse_management.com.warehouse_management.model.RepairTransaction;
import warehouse_management.com.warehouse_management.repository.repair_transaction.RepairTransactionRepository;
import warehouse_management.com.warehouse_management.security.CustomUserDetail;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class RepairTransactionService {

    private final RepairTransactionRepository repairTransactionRepository;
    private final CustomAuthentication customAuthentication;
    private final RepairService repairService;
    private final RepairTransactionMapper repairTransactionMapper;

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

        repairTransactionRepository.updateIsRepaired(repairTransaction.getId(), dto.getIsRepaired(), customUserDetail.getFullName());
    }

    public List<RepairTransactionDto> getRepairTransactionListToRepairCode(String repairCode) {

        Repair repair = repairService.getToRepairCode(repairCode);

        return repairTransactionRepository.findAllByRepairId(repair.getId())
                .stream()
                .map(repairTransactionMapper::toRepairTransactionDto)
                .toList();
    }

    @Transactional
    public void softDeleteRepairTransaction(RepairTransactionIdListDto dto) {

        repairTransactionRepository.bulkDelete(dto.getRepairTransactionIds());
    }
}
