package warehouse_management.com.warehouse_management.service.report;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ReportTemplateCache {

    private final Map<String, byte[]> templateCache = new ConcurrentHashMap<>();

    /**
     * Get cached template or load it
     */
    public byte[] getTemplate(String templateFileName) {
        return templateCache.computeIfAbsent(templateFileName, this::loadTemplate);
    }

    /**
     * Load template from classpath
     */
    private byte[] loadTemplate(String templateFileName) {
        try (InputStream is = new ClassPathResource("report_templates/" + templateFileName).getInputStream();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            is.transferTo(bos);
            byte[] template = bos.toByteArray();

            log.info("Cached template: {} (size: {} KB)", templateFileName, template.length / 1024);
            return template;

        } catch (IOException e) {
            log.error("Failed to load template: {}", templateFileName, e);
            throw new RuntimeException("Failed to load template: " + templateFileName, e);
        }
    }

    public void clearCache() {
        templateCache.clear();
        log.info("Template cache cleared");
    }


    @PostConstruct
    public void preloadTemplates() {
        String[] templates = {
                "PXKDCNB.xlsx",
                "PXK.xlsx",
                "PNK.xlsx",
                "EXCEL_TO_PRODUCTION_SPARE_PART.xlsx",
                "EXCEL_TO_PRODUCTION_PRODUCT_LIFTING_FRAME.xlsx",
        };

        for (String template : templates) {
            try {
                getTemplate(template);
            } catch (Exception e) {
                log.warn("Failed to preload template: {}", template);
            }
        }
    }
}
