package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1/templates")
public class ResourceController {
    @GetMapping("/{templateCode}")
    public ResponseEntity<InputStreamResource> downloadTemplate(
            @PathVariable("templateCode") String templateCode
    ) throws IOException {

        // ví dụ: "products" -> "templates/products_template.xlsx"
        String filePath = String.format("report_templates/%s.xlsx", templateCode);
        ClassPathResource resource = new ClassPathResource(filePath);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + templateCode + "_template.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(resource.getInputStream()));
    }
}

