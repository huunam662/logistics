package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.warehouse_transaction.request.ApprovalTicketDto;
import warehouse_management.com.warehouse_management.dto.warehouse_transaction.request.CreateWarehouseTransactionDto;
import warehouse_management.com.warehouse_management.dto.warehouse_transaction.response.WarehouseTransactionPageDto;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseTranType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseTransactionStatus;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.WarehouseTransactionMapper;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.model.WarehouseTransaction;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.warehouse_transaction.WarehouseTransactionRepository;
import warehouse_management.com.warehouse_management.utils.TranUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseTransactionService {

    private final WarehouseService warehouseService;
    private final WarehouseTransactionRepository warehouseTransferTicketRepository;
    private final WarehouseTransactionMapper warehouseTransferTicketMapper;
    private final InventoryItemRepository inventoryItemRepository;
    private final TranUtils tranUtils;

    @Transactional
    public WarehouseTransaction createWarehouseTransaction(CreateWarehouseTransactionDto dto){

        WarehouseTransaction ticket = warehouseTransferTicketMapper.toWarehouseTransaction(dto);
        Warehouse originWarehouse = warehouseService.getWarehouseToId(new ObjectId(dto.getOriginWarehouseId()));
        Warehouse destinationWarehouse = warehouseService.getWarehouseToId(new ObjectId(dto.getDestinationWarehouseId()));
        ticket.setOriginWarehouseId(originWarehouse.getId());
        WarehouseTranType tranType = WarehouseTranType.DEST_TO_DEST_TRANSFER;
        ticket.setTranType(tranType);
        ticket.setDestinationWarehouseId(destinationWarehouse.getId());
        ticket.setTitle(tranUtils.generateTranTitle(tranType, null, originWarehouse, destinationWarehouse));
        ticket.setReason("Điều chuyển kho");
        ticket.setRequesterId(null);
        ticket.setApproverId(null);
        ticket.setStatus(WarehouseTransactionStatus.PENDING.getId());

        // TODO: Send message approval to admin

        return warehouseTransferTicketRepository.save(ticket);
    }

    public WarehouseTransaction getWarehouseTransactionToId(ObjectId ticketId) {
        WarehouseTransaction ticket = warehouseTransferTicketRepository.findById(ticketId).orElse(null);
        if(ticket == null || ticket.getDeletedAt() != null)
            throw LogicErrException.of("Ticket không tồn tại.");
        return ticket;
    }

    @Transactional
    public WarehouseTransaction approvalTransaction(String ticketId, ApprovalTicketDto dto){
        WarehouseTransaction ticket = getWarehouseTransactionToId(new ObjectId(ticketId));
        if(ticket.getStatus().equals(WarehouseTransactionStatus.APPROVED.getId()))
            throw LogicErrException.of("Phiếu đã được duyệt trước đó.");
        else if(ticket.getStatus().equals(WarehouseTransactionStatus.REJECTED.getId()))
            throw LogicErrException.of("Phiếu đã được hủy trước đó");
        else {
            if(!WarehouseTransactionStatus.contains(dto.getStatus()))
                throw LogicErrException.of("Trạng thái duyệt không hợp lệ");

            if(dto.getStatus().equals(WarehouseTransactionStatus.APPROVED.getId())){
                transactionApprovedAndRejectedLogic(ticket, ticket.getDestinationWarehouseId());
                List<ObjectId> itemIds = ticket.getInventoryItems().stream().map(WarehouseTransaction.InventoryItemTicket::getId).toList();
                inventoryItemRepository.updateStatusByIdIn(itemIds, InventoryItemStatus.IN_STOCK.getId());
                ticket.setApprovedAt(LocalDateTime.now());
                // TODO: Ghi log lý do duyệt phiếu
            }
            else if(dto.getStatus().equals(WarehouseTransactionStatus.REJECTED.getId())){
                transactionApprovedAndRejectedLogic(ticket, ticket.getOriginWarehouseId());
                // Cập nhật hàng hóa quay lại kho nguồn
                List<ObjectId> itemIds = ticket.getInventoryItems().stream().map(WarehouseTransaction.InventoryItemTicket::getId).toList();
                inventoryItemRepository.updateStatusAndWarehouseByIdIn(itemIds, ticket.getOriginWarehouseId(), InventoryItemStatus.IN_STOCK.getId());
                ticket.setApprovedAt(LocalDateTime.now());
                // TODO: Ghi log lý do hủy phiếu
            }
            ticket.setStatus(dto.getStatus());
            ticket.setReason(dto.getReason());
            return warehouseTransferTicketRepository.save(ticket);
        }
    }

    @Transactional
    public void transactionApprovedAndRejectedLogic(WarehouseTransaction ticket, ObjectId warehouseId){
        // Update items nếu giao dịch được duyệt
        // Nếu ở kho được chỉ định đã tồn tại phụ tùng với trạng thái đang IN_STOCK thì cập nhập số lượng
        Map<String, WarehouseTransaction.InventoryItemTicket> inventoryTicketSparePartMap = ticket.getInventoryItems().stream()
                .filter(item -> item.getInventoryType().equals(InventoryType.SPARE_PART.getId()) && item.getCommodityCode() != null)
                .collect(Collectors.toMap(WarehouseTransaction.InventoryItemTicket::getCommodityCode, item -> item));
        // Lấy ra các phụ tùng với mã sản phẩm đã tồn tại ở kho được chỉ định và trạng thái đang IN_STOCK
        List<InventoryItem> sparePartsInStockToWarehouse = inventoryItemRepository.findSparePartByCommodityCodeIn(inventoryTicketSparePartMap.keySet(), warehouseId, InventoryItemStatus.IN_STOCK.getId());
        // Danh sách lưu trữ các spare part cần xóa mềm thuộc giao dịch
        if(!sparePartsInStockToWarehouse.isEmpty()){
            List<ObjectId> sparePartsToDel = new ArrayList<>();
            // Cập nhật lại số lượng phụ tùng có sẵn trong kho được chỉ định (trùng mã hàng hóa)
            for(var sparePart : sparePartsInStockToWarehouse){
                WarehouseTransaction.InventoryItemTicket sparePartInTicket = inventoryTicketSparePartMap.get(sparePart.getCommodityCode());
                sparePart.setQuantity(sparePart.getQuantity() + sparePartInTicket.getQuantity());
                sparePartsToDel.add(sparePartInTicket.getId());
            }
            inventoryItemRepository.bulkUpdateTransfer(sparePartsInStockToWarehouse);
            // Xóa cứng các phụ tùng được clone trước đó ở kho nguồn (do trước đó chỉ lấy ra số lượng bé hơn số lượng tồn kho)
            inventoryItemRepository.bulkHardDelete(sparePartsToDel);
        }
    }

    public WarehouseTransaction getById(String ticketId) {
        Optional<WarehouseTransaction> rs = warehouseTransferTicketRepository.findById(new ObjectId(ticketId));
        if (rs.isEmpty())
            throw LogicErrException.of("Ticket không tồn tại").setHttpStatus(HttpStatus.NOT_FOUND);
        return rs.get();
    }

    public Page<WarehouseTransactionPageDto> getPageWarehouseTransferTicket(PageOptionsDto optionsDto) {
        return warehouseTransferTicketRepository.findPageWarehouseTransferTicket(optionsDto);
    }

    public Page<WarehouseTransactionPageDto> getPageWarehouseTransferTicket(
            PageOptionsDto optionsDto,
            WarehouseTranType tranType
    ) {
        return warehouseTransferTicketRepository.findPageWarehouseTransferTicket(optionsDto, tranType);
    }

}
