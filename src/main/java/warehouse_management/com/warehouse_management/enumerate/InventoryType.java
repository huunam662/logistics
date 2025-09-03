package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public enum InventoryType {

    SPARE_PART("SPARE_PART"),                   // Phụ tùng
//    VEHICLE_ACCESSORY("VEHICLE_ACCESSORY"), // XE PHỤ KIỆN
    VEHICLE("VEHICLE"),                         // XE
    ACCESSORY("ACCESSORY");                     // PHỤ KIỆN


    private final String id;

    InventoryType(final String id) {
        this.id = id;
    }

    @Nullable
    public static InventoryType fromId(String id) {
        for (InventoryType at : InventoryType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

}
