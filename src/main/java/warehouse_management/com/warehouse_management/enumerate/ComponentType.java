package warehouse_management.com.warehouse_management.enumerate;

import org.springframework.lang.Nullable;
import warehouse_management.com.warehouse_management.model.InventoryItem;


public enum ComponentType {

    // ACCESSORY
    LIFTING_FRAME("LIFTING_FRAME", "KHUNG NÂNG"),   // KHUNG NÂNG
    BATTERY("BATTERY", "BÌNH ĐIỆN"), // BÌNH ĐIỆN
    CHARGER("CHARGER", "SẠC"),      // SẠC

    // SPARE PART
    ENGINE("ENGINE", "ĐỘNG CƠ"),       // Động cơ
    FORK("FORK", "CÀNG NÂNG"),         // Càng nâng
    VALVE("VALVE", "VALVE"),        // Van
    WHEEL("WHEEL", "BÁNH XE"),      // Bánh xe
    SIDE_SHIFT("SIDE_SHIFT", "SIDE SHIFT");// Side shift

    private final String id;
    private final String value;

    ComponentType(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }
    public String getValue() {return  value;}

    @Nullable
    public static ComponentType fromId(String id) {
        for (ComponentType at : ComponentType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

    @Nullable
    public static InventoryType itemType(ComponentType componentType) {
        if(componentType == null) return null;
        return switch (componentType){
            case ENGINE, FORK, VALVE, SIDE_SHIFT -> InventoryType.SPARE_PART;
            default -> InventoryType.ACCESSORY;
        };
    }
}

