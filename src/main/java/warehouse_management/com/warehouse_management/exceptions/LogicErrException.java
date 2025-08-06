package warehouse_management.com.warehouse_management.exceptions;

public class LogicErrException extends RuntimeException {
    private final String messageKey;
    private final Object[] args;

    public LogicErrException(String messageKey, Object... args) {
        this.messageKey = messageKey;
        this.args = args;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }

}

