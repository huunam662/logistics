package warehouse_management.com.warehouse_management.enumerate;

public enum InventoryItemStatus {
    IN_STOCK("IN_STOCK"),   // Sẵn hàng trong kho
    IN_TRANSIT("IN_TRANSIT"),   // Đang vận chuyển
    IN_REPAIR("IN_REPAIR"), // Đang sửa  chữa
    HOLD("HOLD"),   // Giữ hàng
    SOLD("SOLD"),   // Đã bán
    OTHER("OTHER"); // Khác

    private final String id;

    InventoryItemStatus(String id) {
        this.id = id;
    }
    
    public static InventoryItemStatus fromId(String id) {
        for (InventoryItemStatus status : values()) {
            if (status.name().equalsIgnoreCase(id)) return status;
        }
        return null;
    }

    public String getId() {
        return name();
    }
}