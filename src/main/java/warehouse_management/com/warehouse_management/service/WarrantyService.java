package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.dto.warranty.request.CreateWarrantyDTO;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.warranty.request.CreateWarrantyTransactionDTO;
import warehouse_management.com.warehouse_management.dto.warranty.request.UpdateStatusWarrantyRequestDTO;
import warehouse_management.com.warehouse_management.dto.warranty.response.WarrantyResponseDTO;
import warehouse_management.com.warehouse_management.dto.warranty.response.WarrantyTransactionResponseDTO;
import warehouse_management.com.warehouse_management.enumerate.DeliveryOrderStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.WarrantyStatus;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.exceptions.errormsg.LogicErrMsg;
import warehouse_management.com.warehouse_management.mapper.WarrantyMapper;
import warehouse_management.com.warehouse_management.mapper.WarrantyTransactionMapper;
import warehouse_management.com.warehouse_management.model.DeliveryOrder;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warranty;
import warehouse_management.com.warehouse_management.model.WarrantyTransaction;
import warehouse_management.com.warehouse_management.repository.ClientRepository;
import warehouse_management.com.warehouse_management.repository.delivery_order.DeliveryOrderRepository;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.warranty.WarrantyRepository;
import warehouse_management.com.warehouse_management.repository.warranty.WarrantyTransactionRepository;
import warehouse_management.com.warehouse_management.utils.Msg;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WarrantyService {
    private final WarrantyRepository warrantyRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final DeliveryOrderRepository deliveryOrderRepository;
    private final ClientRepository clientRepository;
    private final WarrantyTransactionRepository warrantyTransactionRepository;

    private final WarrantyMapper warrantyMapper;
    private final WarrantyTransactionMapper warrantyTransactionMapper;

    private final CustomAuthentication customAuthentication;

    /**
     * Lấy danh sách đơn bảo hành để show trên datagrid
     *
     * @return danh sách đơn bảo hành
     */
    public Page<WarrantyResponseDTO> getListWarranty(PageOptionsDto pageOptionsDto) {
        return warrantyRepository
                .findItemWithFilter(pageOptionsDto);
    }

    /**
     * Tạo đơn bảo hành
     *
     * @param listCreateWarrantyDTO danh sách DTO của request, bao gồm id của item và ghi chú cho đơn bảo hành
     * @return Đơn bảo hành
     */
    @Transactional
    public List<WarrantyResponseDTO> createWarranty(List<CreateWarrantyDTO> listCreateWarrantyDTO) {
        List<Warranty> listWarranty = new ArrayList<>();

        for (CreateWarrantyDTO createWarrantyDTO : listCreateWarrantyDTO) {
            Warranty newWarranty = new Warranty();

            Optional<InventoryItem> inventoryItem = inventoryItemRepository
                    .findById(createWarrantyDTO.getWarrantyInventoryItemId());

            if (inventoryItem.isEmpty()) {
                throw LogicErrException.of(Msg.get(LogicErrMsg.INVENTORY_ITEM_NOT_FOUND));
            }

            inventoryItem.ifPresent((item) -> {
                validateItemBeforeWarranty(item);
                newWarranty.setWarrantyInventoryItem(item);

                Optional<DeliveryOrder> deliveryOrder = deliveryOrderRepository
                        .findDeliveryOrderByItemNotEqualDeliveryOrderStatus(item.getId(), DeliveryOrderStatus.REJECTED);

                if (deliveryOrder.isPresent()) {
                    if (deliveryOrder.get().getCustomerId() != null) {
                        clientRepository
                                .findById(deliveryOrder.get().getCustomerId())
                                .ifPresent(newWarranty::setClient);
                    } else {
                        throw LogicErrException.of(Msg.get(LogicErrMsg.WARRANTY_CLIENT_NOT_FOUND));
                    }
                }
            });

            newWarranty.setNote(createWarrantyDTO.getNote());
            newWarranty.setStatus(WarrantyStatus.IN_WARRANTY);

            listWarranty.add(newWarranty);
        }

        return warrantyRepository.saveAll(listWarranty).stream().map(warrantyMapper::toResponseDto).toList();
    }

    /**
     * Validate item trước khi add vào đơn bảo hành
     * @param item inventoryItem thuộc loại xe và đã bán
     */
    private void validateItemBeforeWarranty(InventoryItem item) {
        if (!InventoryItemStatus.SOLD.equals(item.getStatus()))
            throw LogicErrException.of(Msg.get(LogicErrMsg.WARRANTY_ITEM_HAVE_NOT_SOLD));

        if (warrantyRepository.findWarrantyByItemAndEqualStatus(item.getId(), WarrantyStatus.IN_WARRANTY).isPresent()) {
            throw LogicErrException.of(Msg.get(LogicErrMsg.WARRANTY_ITEM_IN_WARRANTY));
        }
    }

    /**
     * Cập nhật tình trạng của đơn bảo hành
     * @param updateStatusWarrantyRequestDTO DTO truyền từ request
     * @return đơn bảo hành sau khi cập nhật
     */
    public WarrantyResponseDTO updateStatus(UpdateStatusWarrantyRequestDTO updateStatusWarrantyRequestDTO) {
        checkExistWarrantyAndGet(updateStatusWarrantyRequestDTO.getWarrantyId().toString());

        return warrantyMapper.toResponseDto(warrantyRepository.updateStatus(updateStatusWarrantyRequestDTO.getWarrantyId(),
                updateStatusWarrantyRequestDTO.getStatus()));
    }

    /**
     * Tạo phiếu bảo hành cho đơn bảo hành
     * @param createWarrantyTransactionDTO DTO nhận từ request
     * @return phiếu bảo hành
     */
    @Transactional
    public WarrantyTransactionResponseDTO createWarrantyTransaction(CreateWarrantyTransactionDTO createWarrantyTransactionDTO) {
        checkExistWarrantyAndGet(createWarrantyTransactionDTO.getWarrantyId().toString());

        WarrantyTransaction warrantyTransaction = warrantyTransactionMapper
                .toWarrantyTransaction(createWarrantyTransactionDTO);
        warrantyTransaction.setCreateByName(customAuthentication.getUser().getFullName());

        return warrantyTransactionMapper.toWarrantyTransactionResponseDTO(
                warrantyTransactionRepository.save(warrantyTransaction));
    }

    /**
     * Kiểm tra xem đơn bảo hành có tồn tại không
     * @param warrantyId id của đơn bảo hành cần check
     * @return nếu như tồn tại thì trả về đơn bảo hành
     */
    private Warranty checkExistWarrantyAndGet(String warrantyId) {
        Optional<Warranty> warranty = warrantyRepository.findById(warrantyId);

        if (warranty.isEmpty() || warranty.get().getDeletedBy() != null) {
            throw LogicErrException.of(Msg.get(LogicErrMsg.WARRANTY_NOT_FOUND));
        }
        return warranty.get();
    }
}
