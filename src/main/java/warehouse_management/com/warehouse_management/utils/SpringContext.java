package warehouse_management.com.warehouse_management.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringContext {

    private static ApplicationContext context;

    public SpringContext(ApplicationContext context) {
        SpringContext.context = context;
    }

    public static <T> T getBean(Class<T> typeBean){
        return context.getBean(typeBean);
    }

    public static <T> T getBean(String qualifier, Class<T> typeBean){
        return context.getBean(qualifier, typeBean);
    }

}
