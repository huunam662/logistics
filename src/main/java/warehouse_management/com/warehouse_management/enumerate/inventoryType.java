package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public enum inventoryType {

    SPARE_PART("SPARE_PART"),                   // Phụ tùng
//    VEHICLE_ACCESSORY("VEHICLE_ACCESSORY"), // XE PHỤ KIỆN
    VEHICLE("VEHICLE"),                         // XE
    ACCESSORY("ACCESSORY");                     // PHỤ KIỆN


    private final String id;

    inventoryType(final String id) {
        this.id = id;
    }

    @Nullable
    public static inventoryType fromId(String id) {
        for (inventoryType at : inventoryType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

}
