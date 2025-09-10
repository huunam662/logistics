package warehouse_management.com.warehouse_management.service.report.impl;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.dto.report.PNKPXKInventoryItemDataSetIDto;
import warehouse_management.com.warehouse_management.enumerate.ItemType;
import warehouse_management.com.warehouse_management.enumerate.TransactionModule;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.model.WarehouseTransaction;
import warehouse_management.com.warehouse_management.repository.container.ContainerRepository;
import warehouse_management.com.warehouse_management.repository.warehouse.WarehouseRepository;
import warehouse_management.com.warehouse_management.repository.warehouse_transaction.WarehouseTransactionRepository;
import warehouse_management.com.warehouse_management.service.report.GenerateReportStrategy;
import warehouse_management.com.warehouse_management.utils.GeneralUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
public class PXKGenerateReport implements GenerateReportStrategy {
    private final MongoTemplate mongoTemplate;
    private final WarehouseTransactionRepository warehouseTransferTicketRepository;
    private final WarehouseRepository warehouseRepository;
    private final ContainerRepository containerRepository;

    public PXKGenerateReport(MongoTemplate mongoTemplate, WarehouseTransactionRepository warehouseTransferTicketRepository, WarehouseRepository warehouseRepository, ContainerRepository containerRepository) {
        this.mongoTemplate = mongoTemplate;
        this.warehouseTransferTicketRepository = warehouseTransferTicketRepository;
        this.warehouseRepository = warehouseRepository;
        this.containerRepository = containerRepository;
    }

    @Override
    public String getReportType() {
        return "PXK";
    }

    @Override
    public Map<String, Object> prepareContext(TransactionModule transactionModule, String ticketId) {
        Map<String, Object> result = new HashMap<>();
        switch (transactionModule) {
            case WAREHOUSE:
                Optional<WarehouseTransaction> warehouseTransaction = Optional.of(warehouseTransferTicketRepository.findById(new ObjectId(ticketId))
                        .orElseThrow());
                WarehouseTransaction transaction = warehouseTransaction.get();
                result.put("dataset", buildDataSetItems(transaction.getInventoryItems()));
                int totalQuantity = Optional.ofNullable(transaction.getInventoryItems())
                        .orElse(List.of())
                        .stream()
                        .mapToInt(item -> Optional.ofNullable(item.getQuantity()).orElse(0))
                        .sum();
                result.put("total1", totalQuantity);
                result.put("total2", totalQuantity);
                result.put("dayString", buildDayString(transaction.getCreatedAt()));

                WarehouseTransaction.Department outDept = transaction.getStockOutDepartment();
                if (outDept != null) {
                    result.put("outDeptName", outDept.getName());
                    result.put("outDeptAddress", outDept.getAddress());
                    result.put("outDeptPosition", outDept.getPosition());
                }
                result.put("reason", transaction.getReason());
                break;

            case CONTAINER:
                Container container = containerRepository.findById(new ObjectId(ticketId))
                        .orElseThrow(() -> new RuntimeException("Container not found with id: " + ticketId));
                int containerTotalQuantity = Optional.ofNullable(container.getInventoryItems())
                        .orElse(List.of())
                        .stream()
                        .mapToInt(item -> Optional.ofNullable(item.getQuantity()).orElse(0))
                        .sum();

                Optional<Warehouse> containerFromWH = warehouseRepository.findById(container.getFromWareHouseId());

                result.put("dataset", buildDataSetContainerTranItems(container.getInventoryItems()));
                result.put("total1", containerTotalQuantity);
                result.put("total2", containerTotalQuantity);
                result.put("dayString", buildDayString(container.getCreatedAt()));
                result.put("outDeptName", containerFromWH.map(Warehouse::getName).orElse(null));
                result.put("outDeptAddress", containerFromWH.map(Warehouse::getAddress).orElse(null));
                result.put("reason", "_");
                break;

            default:
                // Trường hợp không xác định
                result = new HashMap<>();
        }
        return result;
    }


    @Override
    public String getTemplateFileName() {
        return "PXK.xlsx";
    }

    @Override
    public void preprocessWorkbook(Workbook workbook, Map<String, Object> context) {
        List<?> items = (List<?>) context.get("dataset"); // Lấy danh sách item từ context
        if (items != null && items.size() > 1) {
            Sheet sheet = workbook.getSheetAt(0);
            int datasetRowIdx = GeneralUtil.PXK_PNK_DATASET_ROW_IDX;
            sheet.shiftRows(datasetRowIdx, sheet.getLastRowNum(), items.size() - 1);
        }
    }


    private List<PNKPXKInventoryItemDataSetIDto> buildDataSetItems(List<WarehouseTransaction.InventoryItemTicket> inventoryItems) {
        List<PNKPXKInventoryItemDataSetIDto> rs = new ArrayList<>();

        for (int i = 0; i < inventoryItems.size(); i++) {

            WarehouseTransaction.InventoryItemTicket item = inventoryItems.get(i);
            ItemType type = ItemType.fromId(item.getItemType());
            PNKPXKInventoryItemDataSetIDto dto = new PNKPXKInventoryItemDataSetIDto();
            dto.setIndex(i + 1); // STT
            if (type == ItemType.VEHICLE || type == ItemType.ACCESSORY) {
                dto.setCode(item.getProductCode());
            } else {
                dto.setCode(item.getCommodityCode());
            }

            // Set Unit theo itemType
            if (type == ItemType.VEHICLE) {
                dto.setUnit("Chiếc");
            } else {
                dto.setUnit("Cái");
            }

            dto.setQuantity(item.getQuantity());
            dto.setRealQuantity(item.getQuantity());

            // Build Name theo rule
            dto.setName(buildName(item));

            rs.add(dto);
        }

        return rs;
    }

    private List<PNKPXKInventoryItemDataSetIDto> buildDataSetContainerTranItems(List<Container.InventoryItemContainer> inventoryItems) {
        List<PNKPXKInventoryItemDataSetIDto> rs = new ArrayList<>();

        for (int i = 0; i < inventoryItems.size(); i++) {

            Container.InventoryItemContainer item = inventoryItems.get(i);
            ItemType type = ItemType.fromId(item.getItemType());
            PNKPXKInventoryItemDataSetIDto dto = new PNKPXKInventoryItemDataSetIDto();
            dto.setIndex(i + 1); // STT
            if (type == ItemType.VEHICLE || type == ItemType.ACCESSORY) {
                dto.setCode(item.getProductCode());
            } else {
                dto.setCode(item.getCommodityCode());
            }

            // Set Unit theo itemType
            if (type == ItemType.VEHICLE) {
                dto.setUnit("Chiếc");
            } else {
                dto.setUnit("Cái");
            }

            dto.setQuantity(item.getQuantity());
            dto.setRealQuantity(item.getQuantity());

            // Build Name theo rule
            dto.setName(buildName(item));

            rs.add(dto);
        }

        return rs;
    }

    private String buildName(WarehouseTransaction.InventoryItemTicket item) {
        ItemType type = ItemType.fromId(item.getItemType());
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
                sb.append(buildSpecs(item));
                return sb.toString();
            case ACCESSORY:
                return "PHỤ KIỆN " + nullToEmpty(item.getCategory()) + buildSpecs(item);
            case SPARE_PART:
                return "PHỤ TÙNG " + nullToEmpty(item.getNotes());
            default:
                return nullToEmpty(item.getDescription());
        }
    }

    private String buildName(Container.InventoryItemContainer item) {
    ItemType type = ItemType.fromId(item.getItemType());
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
                sb.append(buildSpecs(item));
                return sb.toString();
            case ACCESSORY:
                return "PHỤ KIỆN " + nullToEmpty(item.getCategory()) + buildSpecs(item);
            case SPARE_PART:
                return "PHỤ TÙNG " + nullToEmpty(item.getNotes());
            default:
                return nullToEmpty(item.getDescription());
        }
    }

    /**
     * In tất cả field specs, field nào != null thì show
     */
    private String buildSpecs(WarehouseTransaction.InventoryItemTicket specs) {
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
     * In tất cả field specs, field nào != null thì show
     */
    private String buildSpecs(Container.InventoryItemContainer specs) {
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


    private String buildDayString(LocalDateTime createdAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Ngày' dd, 'Tháng' MM, 'Năm' yyyy");
        LocalDate tranDate = LocalDate.now();
        if (createdAt != null) {
            tranDate = createdAt.toLocalDate();
        }
        return tranDate.format(formatter);
    }

}
