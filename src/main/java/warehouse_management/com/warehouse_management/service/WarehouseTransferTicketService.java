package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.dto.WarehouseTransferTicketDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryItemPoNumberDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.enumerate.TransferTicketStatus;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.model.WarehouseTransferTicket;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.warehouse_transfer_ticket.WarehouseTransferTicketRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseTransferTicketService {

    private final WarehouseTransferTicketRepository warehouseTransferTicketRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Transactional
    public WarehouseTransferTicket createAndSendMessage(Warehouse originWarehouse, Warehouse destinationWarehouse, List<InventoryItem> inventoryItems){

        WarehouseTransferTicket ticket = new WarehouseTransferTicket();
        ticket.setStatus(TransferTicketStatus.PENDING.getId());
        ticket.setOriginWarehouseId(originWarehouse.getId());
        ticket.setDestinationWarehouseId(destinationWarehouse.getId());
        ticket.setInventoryItemIds(inventoryItems.stream().map(InventoryItem::getId).toList());
        ticket.setRequesterId(null);
        ticket.setApproverId(null);
        ticket.setRejectReason(null);

        // TODO: Send message approval to admin

        return warehouseTransferTicketRepository.save(ticket);
    }

    public WarehouseTransferTicket getTicketToId(ObjectId ticketId){
        WarehouseTransferTicket ticket = warehouseTransferTicketRepository.findById(ticketId).orElse(null);
        if(ticket == null || ticket.getDeletedAt() != null)
            throw LogicErrException.of("Ticket không tồn tại.");
        return ticket;
    }

    @Transactional
    public void approvalTransferTicket(String ticketId, String status){
        WarehouseTransferTicket ticket = getTicketToId(new ObjectId(ticketId));
        if(ticket.getStatus().equals(TransferTicketStatus.REJECTED.getId()))
            throw LogicErrException.of("Phiếu đã được hủy trước đó");
        else {
            ticket.setStatus(status);
            warehouseTransferTicketRepository.save(ticket);
            if(status.equals(TransferTicketStatus.APPROVED.getId())){
                if(ticket.getStatus().equals(TransferTicketStatus.APPROVED.getId()))
                    throw LogicErrException.of("Phiếu đã được duyệt trước đó.");
                // TODO: Sinh phiếu nhập xuất, lưu dữ liệu phiếu nhập xuất vào db trước khi sinh
                // TODO: Ghi log lý do duyệt phiếu
            }
            else if(status.equals(TransferTicketStatus.REJECTED.getId())){
                // TODO: Ghi log lý do hủy phiếu
                // TODO: Cập nhật lại hàng hóa
            }
            else throw LogicErrException.of("Trạng thái duyệt không hợp lệ");
        }
    }

    public List<InventoryItemPoNumberDto> getItemsInTicket(String ticketId){
        WarehouseTransferTicket ticket = getTicketToId(new ObjectId(ticketId));
        return inventoryItemRepository.findInventoryItemsInIds(ticket.getInventoryItemIds());
    }

    public Page<WarehouseTransferTicketDto> getPageWarehouseTransferTicket(PageOptionsDto optionsDto){
        return warehouseTransferTicketRepository.findPageWarehouseTransferTicket(optionsDto);
    }
}
