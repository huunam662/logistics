package warehouse_management.com.warehouse_management.enumerate;

import lombok.Getter;

@Getter
public enum ConfigurationStatus {

    PENDING("PENDING"),
    REPAIRING("REPAIRING"),
    COMPLETED("COMPLETED");

    private final String value;

    ConfigurationStatus(String value){
        this.value = value;
    }

    public static ConfigurationStatus fromValue(String value){

        if(value == null) return null;

        for (ConfigurationStatus status : ConfigurationStatus.values()){
            if (status.value.equals(value)){
                return status;
            }
        }
        return null;
    }
}
