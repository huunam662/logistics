package warehouse_management.com.warehouse_management.enumerate;

import org.springframework.lang.Nullable;

public enum WarehouseType  {
    PRODUCTION("PRODUCTION"),               // Kho chờ sản xuất
    DEPARTURE("DEPARTURE"),                 // Kho Trung Quốc (Kho đi)
    DESTINATION("DESTINATION"),             // Kho đích (VN)
    CONSIGNMENT("CONSIGNMENT");           // Kho ký gửi

    private final String id;

    WarehouseType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static WarehouseType fromId(String id) {
        for (WarehouseType at : WarehouseType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}

