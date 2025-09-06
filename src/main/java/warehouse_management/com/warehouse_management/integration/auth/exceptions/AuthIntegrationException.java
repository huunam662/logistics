package warehouse_management.com.warehouse_management.integration.auth.exceptions;

import warehouse_management.com.warehouse_management.integration.IntegrationException;

public class AuthIntegrationException extends IntegrationException {

    public AuthIntegrationException(String rawMessage) {
        super(rawMessage, "AUTH");
    }

    public static AuthIntegrationException of(String rawMessage) {
        return new AuthIntegrationException(rawMessage);
    }
}
