package warehouse_management.com.warehouse_management.exceptions;

import warehouse_management.com.warehouse_management.exceptions.errormsg.LogicErrCode;

public class LogicErrException extends RuntimeException {
    private final String rawMessage;
    private final String messageKey;
    private final Object[] args;
    private final String code;

    private LogicErrException(String code, String messageKey, String rawMessage, Object... args) {
        this.code = code;
        this.messageKey = messageKey;
        this.rawMessage = rawMessage;
        this.args = args;
    }

    public static LogicErrException of(String message, Object... args) {
        return new LogicErrException(null, null, message, args);
    }

    public static LogicErrException ofKey(String messageKey, Object... args) {
        return new LogicErrException(null, messageKey, null, args);
    }

    public static LogicErrException ofCode(String code, Object... args) {
        return new LogicErrException(code, LogicErrCode.getMessageKey(code), null, args);
    }

    public String getCode() {
        return code;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getRawMessage() {
        return rawMessage;
    }

}

