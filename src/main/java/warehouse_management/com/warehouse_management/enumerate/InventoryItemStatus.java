package warehouse_management.com.warehouse_management.enumerate;

public enum InventoryItemStatus {
    IN_STOCK("IN_STOCK"),
    IN_TRANSIT("IN_TRANSIT");

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