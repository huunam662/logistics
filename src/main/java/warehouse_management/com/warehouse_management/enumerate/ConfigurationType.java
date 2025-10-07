package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;

@Getter
public enum ConfigurationType {

    SWAP("SWAP", "Hoán đổi"),
    ASSEMBLE("ASSEMBLE", "Lắp ráp"),
    DISASSEMBLE("DISASSEMBLE", "Tháo rời");

    private final String id;
    private final String value;

    ConfigurationType(final String id, final String value) {
        this.id = id;
        this.value = value;
    }

    public static ConfigurationType fromId(final String id) {
        for (final ConfigurationType enumType : ConfigurationType.values()) {
            if (enumType.id.equals(id)) {
                return enumType;
            }
        }
        return null;
    }
}
