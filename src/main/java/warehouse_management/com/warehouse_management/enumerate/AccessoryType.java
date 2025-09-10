package warehouse_management.com.warehouse_management.enumerate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.lang.Nullable;


public enum AccessoryType {
    LIFTING_FRAME("LIFTING_FRAME"),   // HOẠT ĐỘNG
    BATTERY("BATTERY"), // KHÔNG HOẠT ĐỘNG
    CHARGER("CHARGER");
    private final String id;

    AccessoryType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static AccessoryType fromId(String id) {
        for (AccessoryType at : AccessoryType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}

