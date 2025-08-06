package warehouse_management.com.warehouse_management.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class Msg {

    private static MessageSource messageSource;

    // Được gọi từ config để inject MessageSource một lần
    public static void setSource(MessageSource source) {
        messageSource = source;
    }


    public static String get(String key, Object... args) {
        if (messageSource == null) {
            return "???" + key + "???"; // fallback nếu chưa được set
        }
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }


}
