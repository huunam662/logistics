package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;

@Getter
public enum ChangeConfigurationType {

    SWAP("SWAP"),
    ASSEMBLE("ASSEMBLE"),
    DISASSEMBLE("DISASSEMBLE");

    private final String id;

    ChangeConfigurationType(final String id) {
        this.id = id;
    }

    public static ChangeConfigurationType fromId(final String id) {
        for (final ChangeConfigurationType enumType : ChangeConfigurationType.values()) {
            if (enumType.id.equals(id)) {
                return enumType;
            }
        }
        return null;
    }
}
