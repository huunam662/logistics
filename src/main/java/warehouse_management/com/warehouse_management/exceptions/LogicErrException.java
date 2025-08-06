package warehouse_management.com.warehouse_management.exceptions;

import warehouse_management.com.warehouse_management.exceptions.errormsg.LogicErrCode;

public class LogicErrException extends RuntimeException {
    private final String messageKey;
    private final Object[] args;
    private final String code;

    private LogicErrException(String code, String messageKey, Object... args) {
        this.code = code;
        this.messageKey = messageKey;
        this.args = args;
    }

    public static LogicErrException of(String messageKey, Object... args) {
        return new LogicErrException(null, messageKey, args);
    }

    public static LogicErrException ofCode(String code, Object... args) {
        return new LogicErrException(code, LogicErrCode.getMessageKey(code), args);
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

}

