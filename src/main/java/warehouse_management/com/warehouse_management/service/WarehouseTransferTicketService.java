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
import warehouse_management.com.warehouse_management.dto.WarehouseTransferTicketDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.enumerate.TransferTicketStatus;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.model.WarehouseTransferTicket;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.warehouse_transfer_ticket.WarehouseTransferTicketRepository;
import warehouse_management.com.warehouse_management.utils.GeneralResource;
import warehouse_management.com.warehouse_management.utils.JsonUtils;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WarehouseTransferTicketService {

    private final MongoTemplate mongoTemplate;
    private final WarehouseTransferTicketRepository warehouseTransferTicketRepository;

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

        ticket.setJsonPrint(buildJsonPrint(ticket, inventoryItems));
        // TODO: Send message approval to admin

        return warehouseTransferTicketRepository.save(ticket);
    }

    private String buildJsonPrint(WarehouseTransferTicket ticket, List<InventoryItem> inventoryItems) {
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

    public WarehouseTransferTicket getById(String ticketId) {
        Optional<WarehouseTransferTicket> rs = warehouseTransferTicketRepository.findById(new ObjectId(ticketId));
        if (rs.isEmpty())
            throw LogicErrException.of("Ticket không tồn tại").setHttpStatus(HttpStatus.NOT_FOUND);
        return rs.get();
    }

    public Page<WarehouseTransferTicketDto> getPageWarehouseTransferTicket(PageOptionsDto optionsDto) {
        return warehouseTransferTicketRepository.findPageWarehouseTransferTicket(optionsDto);
    }

    public byte[] getReport(String ticketId, String type) {
        WarehouseTransferTicket ticket = getById(ticketId);
        int dataSetSize = ticket.getInventoryItemIds().size();
        // 2. Chọn template dựa vào type
        int datasetContentRowIdx = GeneralResource.PXK_PNK_DATASET_ROW_IDX;
        String templateFileName = "PXK.xlsx"; // default
        if ("in".equalsIgnoreCase(type)) {
            templateFileName = "PNK.xlsx";
        }
        // 3. Parse jsonPrint ra Map -> lấy dataset
        Map<String, Object> jsonMap = JsonUtils.parseJsonPrint(ticket.getJsonPrint());


        try (InputStream fis = new ClassPathResource("report_templates/" + templateFileName).getInputStream(); Workbook workbook = new XSSFWorkbook(fis); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.getSheetAt(0);
            // cái này set cứng đây luôn, có đổi trong template thì vào cập nhật nếu k thì tạo thêm table report_template
            // shift row nếu cần để tránh bị đè lên các ô phía dưới
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

}
