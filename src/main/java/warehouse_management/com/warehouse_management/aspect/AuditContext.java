package warehouse_management.com.warehouse_management.aspect;

public class AuditContext {
    private static final ThreadLocal<String> detailHolder = new ThreadLocal<>();

    public static void setDetail(String detail) {
        detailHolder.set(detail);
    }

    public static String getDetail() {
        return detailHolder.get();
    }

    public static void clear() {
        detailHolder.remove();
    }
}
