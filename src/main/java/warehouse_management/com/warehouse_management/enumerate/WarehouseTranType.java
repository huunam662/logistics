package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;
import org.springframework.lang.Nullable;
@Getter
public enum WarehouseTranType {
    LOCAL_TRANSFER("LOCAL_TRANSFER"),
    WAREHOUSE_INOUT("WAREHOUSE_INOUT"),
    SALE_RIGHT_TRANSFER("SALE_RIGHT_TRANSFER"),
    WAREHOUSE_IN("WAREHOUSE_IN"),
    WAREHOUSE_OUT("WAREHOUSE_OUT");

    private final String id;

    WarehouseTranType(final String id) {
        this.id = id;
    }

    @Nullable
    public static WarehouseTranType fromId(String id) {
        for (WarehouseTranType at : WarehouseTranType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
