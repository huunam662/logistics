package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.repair.request.CreateRepairDTO;
import warehouse_management.com.warehouse_management.dto.repair.request.CreateRepairTransactionDTO;
import warehouse_management.com.warehouse_management.dto.repair.request.UpdateStatusRepairDTO;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairResponseDTO;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairTransactionResponseDTO;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.RepairStatus;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.exceptions.errormsg.LogicErrMsg;
import warehouse_management.com.warehouse_management.mapper.RepairMapper;
import warehouse_management.com.warehouse_management.mapper.RepairTransactionMapper;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Repair;
import warehouse_management.com.warehouse_management.model.RepairTransaction;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.repair.RepairRepository;
import warehouse_management.com.warehouse_management.repository.repair.RepairTransactionRepository;
import warehouse_management.com.warehouse_management.utils.Msg;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepairService {
    private final RepairRepository repairRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final RepairTransactionRepository repairTransactionRepository;

    private final RepairMapper repairMapper;
    private final RepairTransactionMapper repairTransactionMapper;

    private final CustomAuthentication customAuthentication;

    /**
     * Lấy danh sách đơn sửa chữa để show trên datagrid
     *
     * @return danh sách đơn sửa chữa
     */
    public Page<RepairResponseDTO> getListRepair(PageOptionsDto pageOptionsDto) {
        return repairRepository
                .findItemWithFilter(pageOptionsDto);
    }

    /**
     * Tạo đơn sửa chữa
     *
     * @param listCreateRepairDTO danh sách DTO của request, bao gồm id của item và ghi chú cho đơn sửa chữa
     * @return Đơn sửa chữa
     */
    @Transactional
    public List<RepairResponseDTO> createRepair(List<CreateRepairDTO> listCreateRepairDTO) {
        List<Repair> listRepair = new ArrayList<>();

        for (CreateRepairDTO createRepairDTO : listCreateRepairDTO) {
            Repair newRepair = new Repair();

            Optional<InventoryItem> inventoryItem = inventoryItemRepository
                    .findById(createRepairDTO.getRepairInventoryItemId());

            inventoryItem.ifPresent((item) -> {
                validateItemBeforeRepair(item);
                newRepair.setRepairInventoryItem(item);
            });

            newRepair.setNote(createRepairDTO.getNote());

            listRepair.add(newRepair);
        }

        return repairRepository.saveAll(listRepair).stream().map(repairMapper::toResponseDto).toList();
    }

    /**
     * Validate item trước khi add vào đơn sửa chữa
     * @param item inventoryItem thuộc loại xe và đã bán
     */
    private void validateItemBeforeRepair(InventoryItem item) {
        if (!InventoryItemStatus.SOLD.equals(item.getStatus()))
            throw LogicErrException.of(Msg.get(LogicErrMsg.REPAIR_ITEM_IS_NOT_IN_STOCK));

        if (repairRepository.findRepairByItemAndEqualStatus(item.getId(), RepairStatus.IN_REPAIR).isPresent()) {
            throw LogicErrException.of(Msg.get(LogicErrMsg.REPAIR_ITEM_IN_REPAIR));
        }
    }

    /**
     * Cập nhật tình trạng của đơn sửa chữa
     * @param updateStatusRepairDTO DTO truyền từ request
     * @return đơn sửa chữa sau khi cập nhật
     */
    public RepairResponseDTO updateStatus(UpdateStatusRepairDTO updateStatusRepairDTO) {
        checkExistRepairAndGet(updateStatusRepairDTO.getRepairId().toString());

        return repairMapper.toResponseDto(repairRepository.updateStatus(updateStatusRepairDTO.getRepairId(),
                updateStatusRepairDTO.getStatus()));
    }

    /**
     * Tạo phiếu sửa chữa cho đơn sửa chữa
     * @param createRepairTransactionDTO DTO nhận từ request
     * @return phiếu sửa chữa
     */
    @Transactional
    public RepairTransactionResponseDTO createRepairTransaction(CreateRepairTransactionDTO createRepairTransactionDTO) {
        checkExistRepairAndGet(createRepairTransactionDTO.getRepairId().toString());

        RepairTransaction repairTransaction = repairTransactionMapper
                .toRepairTransaction(createRepairTransactionDTO);
        repairTransaction.setCreateByName(customAuthentication.getUser().getFullName());

        return repairTransactionMapper.toRepairTransactionResponseDTO(
                repairTransactionRepository.save(repairTransaction));
    }

    /**
     * Kiểm tra xem đơn sửa chữa có tồn tại không
     * @param repairId id của đơn sửa chữa cần check
     * @return nếu như tồn tại thì trả về đơn sửa chữa
     */
    private Repair checkExistRepairAndGet(String repairId) {
        Optional<Repair> repair = repairRepository.findById(repairId);

        if (repair.isEmpty() || repair.get().getDeletedBy() != null) {
            throw LogicErrException.of(Msg.get(LogicErrMsg.REPAIR_NOT_FOUND));
        }
        return repair.get();
    }
}
