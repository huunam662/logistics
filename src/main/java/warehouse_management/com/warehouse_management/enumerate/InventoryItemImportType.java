package warehouse_management.com.warehouse_management.enumerate;

public enum InventoryItemImportType {
    PRODUCTION_PRODUCT("PRODUCTION_PRODUCT", "Kho chờ_Xe-Phụ kiện"),
    DESTINATION_PRODUCT("DESTINATION_PRODUCT", "Kho đến_Xe-Phụ kiện"),
    PRODUCTION_SPARE_PART("PRODUCTION_SPARE_PART", "Kho chờ_Phụ tùng"),
    DESTINATION_SPARE_PART("DESTINATION_SPARE_PART", "Kho đến_Phụ tùng");

    private final String id;
    private final String title;

    InventoryItemImportType(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public static InventoryItemImportType fromId(String id) {
        for (InventoryItemImportType type : values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        return null;
    }
}
