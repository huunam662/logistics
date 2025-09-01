package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum WarehouseStatus {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String value;

    WarehouseStatus(final String value) {
        this.value = value;
    }

    public static WarehouseStatus fromValue(final String value) {
        return Arrays.stream(values())
                .filter(v -> v.value.equals(value))
                .findFirst()
                .orElse(null);
    }
}
