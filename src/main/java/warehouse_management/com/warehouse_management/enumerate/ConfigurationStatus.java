package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;

@Getter
public enum ConfigurationStatus {

    PENDING("PENDING", "Chờ xác nhận"),
    CONFIGURING("CONFIGURING", "Đang cấu hình"),
    COMPLETED("COMPLETED", "Hoàn tất");

    private final String id;
    private final String value;

    ConfigurationStatus(String id, String value){
        this.id = id;
        this.value = value;
    }

    public static ConfigurationStatus fromId(String id){

        if(id == null) return null;

        for (ConfigurationStatus status : ConfigurationStatus.values()){
            if (status.id.equals(id)){
                return status;
            }
        }
        return null;
    }
}
