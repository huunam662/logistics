package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;

@Getter
public enum QuotationType {

    MANUAL("MANUAL", "Thủ công"),
    FROM_WAREHOUSE("FROM_WAREHOUSE", "Từ kho");

    private final String id;
    private final String value;

    QuotationType(final String id, final String value){
        this.id = id;
        this.value = value;
    }

    public static QuotationType fromId(final String id){
        for(QuotationType type : QuotationType.values()){
            if(type.id.equals(id)){
                return type;
            }
        }
        return null;
    }

}
