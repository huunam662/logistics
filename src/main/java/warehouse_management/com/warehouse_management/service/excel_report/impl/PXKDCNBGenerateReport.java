package warehouse_management.com.warehouse_management.service.excel_report.impl;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.model.WarehouseTransaction;
import warehouse_management.com.warehouse_management.repository.warehouse_transaction.WarehouseTransactionRepository;
import warehouse_management.com.warehouse_management.service.excel_report.GenerateReportStrategy;
import warehouse_management.com.warehouse_management.utils.GeneralResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PXKDCNBGenerateReport implements GenerateReportStrategy {
    private final WarehouseTransactionRepository warehouseTransferTicketRepository;

    public PXKDCNBGenerateReport(WarehouseTransactionRepository warehouseTransferTicketRepository) {
        this.warehouseTransferTicketRepository = warehouseTransferTicketRepository;
    }

    @Override
    public String getReportType() {
        return "PXKDCNB";
    }

    @Override
    public Map<String, Object> prepareContext(String ticketId) {
        Optional<WarehouseTransaction> warehouseTransaction = Optional.of(warehouseTransferTicketRepository.findById(new ObjectId(ticketId))
                .orElseThrow());
        WarehouseTransaction transaction = warehouseTransaction.get();
        Map<String, Object> result = new HashMap<>();
        result.put("outDeptName", transaction.getStockOutDepartment().getName());
        result.put("outDeptAddress", transaction.getStockOutDepartment().getAddress());
        result.put("outDeptPhone", transaction.getStockOutDepartment().getPhone());
        result.put("outDeptPosition", transaction.getStockOutDepartment().getPosition());

        result.put("inDeptName", transaction.getStockInDepartment().getName());
        result.put("inDeptAddress", transaction.getStockInDepartment().getAddress());
        result.put("inDeptPhone", transaction.getStockInDepartment().getPhone());
        result.put("inDeptPosition", transaction.getStockInDepartment().getPosition());

        result.put("shipFullName", transaction.getShipUnitInfo().getFullName());
        result.put("shipLicensePlate", transaction.getShipUnitInfo().getLicensePlate());
        result.put("shipPhone", transaction.getShipUnitInfo().getPhone());
        result.put("shipIdentityCode", transaction.getShipUnitInfo().getIdentityCode());
        result.put("dataset", transaction.getInventoryItems());

        return result;
    }

    @Override
    public String getTemplateFileName() {
        return "PXKDCNB.xlsx";
    }

    @Override
    public void preprocessWorkbook(Workbook workbook, Map<String, Object> context) {
        List<?> items = (List<?>) context.get("dataset"); // Lấy danh sách item từ context
        if (items != null && items.size() > 1) {
            Sheet sheet = workbook.getSheetAt(0);
            int datasetRowIdx = GeneralResource.PXKDCNB_DATASET_ROW_IDX;
            sheet.shiftRows(datasetRowIdx, sheet.getLastRowNum(), items.size() - 1);
        }
    }
}
