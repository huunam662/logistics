package warehouse_management.com.warehouse_management.integration;

import org.springframework.http.HttpStatus;

public class IntegrationException extends RuntimeException {
    private String integrationService = "";
    private String rawMessage;
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;


    protected IntegrationException(String rawMessage) {

        this.rawMessage = rawMessage;

    }

    public static IntegrationException of(String rawMessage) {
        return new IntegrationException(rawMessage);
    }

    protected IntegrationException(String rawMessage, String integrationService) {
        this.rawMessage = rawMessage;
        this.integrationService = integrationService;
    }

    public IntegrationException setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }


    public String getRawMessage() {
        return rawMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getIntegrationService() {
        return integrationService;
    }

    public void setIntegrationService(String integrationService) {
        this.integrationService = integrationService;
    }
}

