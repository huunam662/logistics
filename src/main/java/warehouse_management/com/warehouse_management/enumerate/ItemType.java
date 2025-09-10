package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public enum ItemType {

    SPARE_PART("SPARE_PART"),                   // Phụ tùng
//    VEHICLE_ACCESSORY("VEHICLE_ACCESSORY"), // XE PHỤ KIỆN
    VEHICLE("VEHICLE"),                         // XE
    ACCESSORY("ACCESSORY");                     // PHỤ KIỆN


    private final String id;

    ItemType(final String id) {
        this.id = id;
    }

    @Nullable
    public static ItemType fromId(String id) {
        for (ItemType at : ItemType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

}
