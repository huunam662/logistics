package warehouse_management.com.warehouse_management.service.report.impl;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import warehouse_management.com.warehouse_management.dto.report.InventoryItemDataSetIDto;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;

import warehouse_management.com.warehouse_management.model.WarehouseTransaction;
import warehouse_management.com.warehouse_management.repository.warehouse_transaction.WarehouseTransactionRepository;
import warehouse_management.com.warehouse_management.service.report.GenerateReportStrategy;
import warehouse_management.com.warehouse_management.utils.GeneralResource;


import java.util.*;

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


        result.put("dataset", buildDataSetItems(transaction.getInventoryItems()));


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


    private List<InventoryItemDataSetIDto> buildDataSetItems(List<WarehouseTransaction.InventoryItemTicket> inventoryItems) {
        List<InventoryItemDataSetIDto> rs = new ArrayList<>();

        for (int i = 0; i < inventoryItems.size(); i++) {
            WarehouseTransaction.InventoryItemTicket item = inventoryItems.get(i);

            InventoryItemDataSetIDto dto = new InventoryItemDataSetIDto();
            dto.setIndex(i + 1); // STT
            dto.setSerialNumber(item.getSerialNumber());

            // Set Unit theo inventoryType
            InventoryType type = InventoryType.fromId(item.getInventoryType());
            if (type == InventoryType.VEHICLE) {
                dto.setUnit("Chiếc");
            } else {
                dto.setUnit("Cái");
            }

            dto.setQuantity(item.getQuantity());
            dto.setNote(item.getNotes());

            // Build Name theo rule
            dto.setName(buildName(item));

            rs.add(dto);
        }

        return rs;
    }

    private String buildName(WarehouseTransaction.InventoryItemTicket item) {
        InventoryType type = InventoryType.fromId(item.getInventoryType());
        if (type == null) {
            return nullToEmpty(item.getModel()); // fallback nếu type null
        }

        switch (type) {
            case VEHICLE:
                StringBuilder sb = new StringBuilder();
                sb.append("XE NÂNG ")
                        .append(nullToEmpty(item.getModel()));

                if (item.getInitialCondition() != null) {
                    sb.append(", ").append(item.getInitialCondition());
                }

                // specs xuống dòng
                sb.append(buildSpecs(item.getSpecifications()));
                return sb.toString();
            case ACCESSORY:
                return "PHỤ KIỆN " + nullToEmpty(item.getCategory()) + buildSpecs(item.getSpecifications());
            case SPARE_PART:
                return "PHỤ TÙNG " + nullToEmpty(item.getNotes());
            default:
                return nullToEmpty(item.getDescription());
        }
    }

    /**
     * In tất cả field specs, field nào != null thì show
     */
    private String buildSpecs(WarehouseTransaction.InventoryItemTicket.Specifications specs) {
        if (specs == null) return "";

        StringBuilder sb = new StringBuilder();

        if (specs.getLiftingCapacityKg() != null) {
            sb.append("\nSức nâng (kg): ").append(specs.getLiftingCapacityKg());
        }
        if (specs.getChassisType() != null) {
            sb.append("\nLoại khung: ").append(specs.getChassisType());
        }
        if (specs.getLiftingHeightMm() != null) {
            sb.append("\nChiều cao nâng (mm): ").append(specs.getLiftingHeightMm());
        }
        if (specs.getEngineType() != null) {
            sb.append("\nĐộng cơ: ").append(specs.getEngineType());
        }
        if (specs.getBatteryInfo() != null) {
            sb.append("\nThông tin bình điện: ").append(specs.getBatteryInfo());
        }
        if (specs.getBatterySpecification() != null) {
            sb.append("\nThông số bình điện: ").append(specs.getBatterySpecification());
        }
        if (specs.getChargerSpecification() != null) {
            sb.append("\nThông số bộ sạc: ").append(specs.getChargerSpecification());
        }
        if (specs.getForkDimensions() != null) {
            sb.append("\nKích thước càng: ").append(specs.getForkDimensions());
        }
        if (specs.getValveCount() != null) {
            sb.append("\nSố lượng van: ").append(specs.getValveCount());
        }
        if (specs.getHasSideShift() != null) {
            sb.append("\nCó side shift: ").append(specs.getHasSideShift() ? "Có" : "Không");
        }
        if (specs.getOtherDetails() != null) {
            sb.append("\nChi tiết khác: ").append(specs.getOtherDetails());
        }

        return sb.toString();
    }


    /**
     * Nếu string null thì trả về "", tránh NullPointerException
     */
    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }


}
