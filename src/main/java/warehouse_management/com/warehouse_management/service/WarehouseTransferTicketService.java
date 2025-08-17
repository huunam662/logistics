package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.annotation.AuditAction;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.warehouse_transfer_ticket.request.ApprovalTicketDto;
import warehouse_management.com.warehouse_management.dto.warehouse_transfer_ticket.request.CreateWarehouseTransferTicketDto;
import warehouse_management.com.warehouse_management.dto.warehouse_transfer_ticket.response.WarehouseTransferTicketPageDto;
import warehouse_management.com.warehouse_management.enumerate.TransferTicketStatus;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.WarehouseTransferTicketMapper;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.model.WarehouseTransferTicket;
import warehouse_management.com.warehouse_management.repository.warehouse_transfer_ticket.WarehouseTransferTicketRepository;
import warehouse_management.com.warehouse_management.utils.GeneralResource;
import warehouse_management.com.warehouse_management.utils.JsonUtils;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WarehouseTransferTicketService {

    private final MongoTemplate mongoTemplate;
    private final WarehouseService warehouseService;
    private final WarehouseTransferTicketRepository warehouseTransferTicketRepository;
    private final WarehouseTransferTicketMapper warehouseTransferTicketMapper;

    @Transactional
    public WarehouseTransferTicket createTransferTicket(CreateWarehouseTransferTicketDto dto){

        WarehouseTransferTicket ticket = warehouseTransferTicketMapper.toWarehouseTransferTicket(dto);
        Warehouse originWarehouse = warehouseService.getWarehouseToId(ticket.getOriginWarehouseId());
        Warehouse destinationWarehouse = warehouseService.getWarehouseToId(ticket.getDestinationWarehouseId());
        ticket.setOriginWarehouseId(originWarehouse.getId());
        ticket.setDestinationWarehouseId(destinationWarehouse.getId());
        ticket.setTitle("Chuyển hàng từ kho " + originWarehouse.getName() + " đến kho " + destinationWarehouse.getName());
        ticket.setReason("Điều chuyển kho");
        ticket.setRequesterId(null);
        ticket.setApproverId(null);
        ticket.setStatus(TransferTicketStatus.PENDING.getId());

        // TODO: Send message approval to admin

        return warehouseTransferTicketRepository.save(ticket);
    }


    public String buildJsonPrint(WarehouseTransferTicket ticket, List<InventoryItem> inventoryItems) {
        HashMap<String, Object> printData = new HashMap<>();
        Warehouse fromWh = GeneralResource.getWarehouseById(mongoTemplate, ticket.getOriginWarehouseId());
        Warehouse toWh = GeneralResource.getWarehouseById(mongoTemplate, ticket.getDestinationWarehouseId());
        printData.put("fromWarehouseName", fromWh.getName());
        printData.put("toWarehouseName", toWh.getName());
        printData.put("toUserName", "Tên người nhận hàng");
        printData.put("fromUserName", "Tên người gửi hàng");
        printData.put("address1", "Địa chỉ 1");
        printData.put("address2", "Địa chỉ 2");
        printData.put("dataset", inventoryItems);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Ngày' dd, 'Tháng' MM, 'Năm' yyyy");
        LocalDate tranDate = LocalDate.now();
        if (ticket.getCreatedAt() != null) {
            tranDate = ticket.getCreatedAt().toLocalDate();
        }
        String formattedDate = tranDate.format(formatter);
        printData.put("dayString", formattedDate);
        int totalQuantity = inventoryItems.stream()
                .mapToInt(InventoryItem::getQuantity)
                .sum();
        printData.put("total1", totalQuantity);
        printData.put("total2", totalQuantity);
        return JsonUtils.toJson(printData);

    }

    public WarehouseTransferTicket getTicketToId(ObjectId ticketId) {
        WarehouseTransferTicket ticket = warehouseTransferTicketRepository.findById(ticketId).orElse(null);
        if(ticket == null || ticket.getDeletedAt() != null)
            throw LogicErrException.of("Ticket không tồn tại.");
        return ticket;
    }

    @Transactional
    public void approvalTransferTicket(String ticketId, ApprovalTicketDto dto){
        WarehouseTransferTicket ticket = getTicketToId(new ObjectId(ticketId));
        if(ticket.getStatus().equals(TransferTicketStatus.APPROVED.getId()))
            throw LogicErrException.of("Phiếu đã được duyệt trước đó.");
        else if(ticket.getStatus().equals(TransferTicketStatus.REJECTED.getId()))
            throw LogicErrException.of("Phiếu đã được hủy trước đó");
        else {
            if(!TransferTicketStatus.contains(dto.getStatus()))
                throw LogicErrException.of("Trạng thái duyệt không hợp lệ");

            if(dto.getStatus().equals(TransferTicketStatus.APPROVED.getId())){
                // TODO: Ghi log lý do duyệt phiếu
            }
            if(dto.getStatus().equals(TransferTicketStatus.REJECTED.getId())){
                // TODO: Ghi log lý do hủy phiếu
                // TODO: Cập nhật lại hàng hóa
            }
            ticket.setApprovedAt(LocalDateTime.now());
            ticket.setStatus(dto.getStatus());
            ticket.setReason(dto.getReason());
            warehouseTransferTicketRepository.save(ticket);
        }
    }

    public WarehouseTransferTicket getById(String ticketId) {
        Optional<WarehouseTransferTicket> rs = warehouseTransferTicketRepository.findById(new ObjectId(ticketId));
        if (rs.isEmpty())
            throw LogicErrException.of("Ticket không tồn tại").setHttpStatus(HttpStatus.NOT_FOUND);
        return rs.get();
    }

    public Page<WarehouseTransferTicketPageDto> getPageWarehouseTransferTicket(PageOptionsDto optionsDto) {
        return warehouseTransferTicketRepository.findPageWarehouseTransferTicket(optionsDto);
    }

    @AuditAction(action = "GENERATE_REPORT")
    public byte[] getReport(String ticketId, String type) {
        WarehouseTransferTicket ticket = getById(ticketId);
        int dataSetSize = ticket.getInventoryItems().size();
        // 2. Chọn template dựa vào type
        int datasetContentRowIdx = getDatasetRowIdx(type);
        String templateFileName = type + ".xlsx";
        // 3. Parse jsonPrint ra Map -> lấy dataset
        Map<String, Object> jsonMap = JsonUtils.parseJsonPrint(ticket.getJsonPrint());


        try (InputStream fis = new ClassPathResource("report_templates/" + templateFileName).getInputStream(); Workbook workbook = new XSSFWorkbook(fis); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.getSheetAt(0);
            // shift row nếu cần để tránh bị đè lên các ô phía dưới tại phiếu PXKDCNB
            if (dataSetSize > 1) {
                sheet.shiftRows(datasetContentRowIdx, sheet.getLastRowNum(), dataSetSize - 1);
            }
            workbook.write(bos);


            try (InputStream templateStream = new ByteArrayInputStream(bos.toByteArray());
                 ByteArrayOutputStream os = new ByteArrayOutputStream()) {

                org.jxls.common.Context context = new org.jxls.common.Context();
                jsonMap.forEach(context::putVar); // put tất cả key từ jsonPrint
                org.jxls.util.JxlsHelper.getInstance().processTemplate(templateStream, os, context);

                return os.toByteArray();
            }

        } catch (IOException e) {
            throw LogicErrException.of("Failed to generate report: " + e.getMessage());
        }
    }

    public int getDatasetRowIdx(String type) {
        switch (type) {
            case "PNK":
                return GeneralResource.PXK_PNK_DATASET_ROW_IDX;
            case "PXK":
                return GeneralResource.PXK_PNK_DATASET_ROW_IDX;
            case "PXKDCNB":
                return GeneralResource.PXKDCNB_DATASET_ROW_IDX;

            default:
                return -1;
        }
    }

}
