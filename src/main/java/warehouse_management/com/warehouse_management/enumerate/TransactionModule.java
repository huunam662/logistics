package warehouse_management.com.warehouse_management.enumerate;



public enum TransactionModule {
    WAREHOUSE("WAREHOUSE"),
    CONTAINER("CONTAINER");

    private final String id;

    TransactionModule(final String id) {
        this.id = id;
    }


    public static TransactionModule fromId(String id) {
        for (TransactionModule module : values()) {
            if (module.getId().equals(id)) {
                return module;
            }
        }
        return null;
    }

    public String getId() {
        return this.id;
    }
}