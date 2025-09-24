package warehouse_management.com.warehouse_management.enumerate;

import org.springframework.lang.Nullable;

import java.util.Arrays;

public enum WarehouseTransactionStatus  {

    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String id;

    WarehouseTransactionStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static WarehouseTransactionStatus fromId(String id) {
        for (WarehouseTransactionStatus at : WarehouseTransactionStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

    public static boolean contains(String id) {
        WarehouseTransactionStatus status = fromId(id);
        if(status == null) return false;
        return Arrays.stream(values()).toList().contains(status);
    }
}