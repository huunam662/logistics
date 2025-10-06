package warehouse_management.com.warehouse_management.integration.customer.exceptions;

import warehouse_management.com.warehouse_management.integration.IntegrationException;

public class CustomerIntegrationException extends IntegrationException {

    public CustomerIntegrationException(String rawMessage) {
        super(rawMessage, "CUSTOMER");
    }

    public static CustomerIntegrationException of(String rawMessage) {
        return new CustomerIntegrationException(rawMessage);
    }
}
