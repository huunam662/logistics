package warehouse_management.com.warehouse_management.enumerate;

import org.springframework.lang.Nullable;

public enum WarehouseType {

    PRODUCTION("PRODUCTION", "VPK_CHOSANXUAT"),      // Kho chờ sản xuất
    DEPARTURE("DEPARTURE", "VPK_KHODI"),             // Kho đi
    DESTINATION("DESTINATION", "VPK_KHODEN"),        // Kho đến
    CONSIGNMENT("CONSIGNMENT", "VPK_KYGUI");         // Kho ký gửi

    private final String id;
    private final String officeTypeCode;

    WarehouseType(String id, String officeTypeCode) {
        this.id = id;
        this.officeTypeCode = officeTypeCode;
    }

    public String getId() {
        return id;
    }

    public String getOfficeTypeCode() {
        return officeTypeCode;
    }

    @Nullable
    public static WarehouseType fromId(String id) {
        for (WarehouseType type : WarehouseType.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return null;
    }

    @Nullable
    public static String getOfficeTypeCodeByWarehouseType(String id) {
        WarehouseType type = fromId(id);
        return type != null ? type.getOfficeTypeCode() : null;
    }
}
