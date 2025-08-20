package warehouse_management.com.warehouse_management.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.Set;

@Configuration
public class SwaggerDocs {

    @Bean
    public OpenAPI openAPI(){

        Server localDevServer = new Server();
        localDevServer.setUrl("http://localhost:8080/api");
        localDevServer.setDescription("Local Development Server");

        Server prodDevServer = new Server();
        prodDevServer.setUrl("https://gateway.dev.meu-solutions.com/logistic-erp/api");
        prodDevServer.setDescription("Production Development Server");

        final String securitySchemaName = "Bearer Authorization";

        return new OpenAPI()
                .servers(List.of(localDevServer, prodDevServer))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemaName))
                .info(info())
                .components(components(securitySchemaName));
    }

    public Info info(){

        Contact contact = new Contact();
        contact.setUrl("https://meu-solutions.com");
        contact.setName("By Meu Solutions");
        contact.setEmail("contact@meu-solutions.com");

        Info info = new Info();
        info.setTitle("MeU Warehouse Logistics API docs.");
        info.setVersion("v1.0.0");
        info.setDescription("For MeU Warehouse Logistics Application Client Production.");
        info.setContact(contact);

        return info;
    }

    public Components components(String securitySchemaName){

        SecurityScheme securityScheme = new SecurityScheme();
        securityScheme.setName(securitySchemaName);
        securityScheme.setType(SecurityScheme.Type.HTTP);
        securityScheme.setScheme("bearer");
        securityScheme.setBearerFormat("JWT");

        Components components = new Components();
        components.addSecuritySchemes(securitySchemaName, securityScheme);

        return components;
    }


    @Bean
    public OpenApiCustomizer removeApiPrefixCustomizer(){
        return openApi -> {
            Paths paths = new Paths();
            Set<String> pathKeys = openApi.getPaths().keySet();
            for(String path : pathKeys){
                if(path.startsWith("/api")){
                    paths.put(path.replaceFirst("/api", ""), openApi.getPaths().get(path));
                }
                else paths.put(path, openApi.getPaths().get(path));
            }
            openApi.setPaths(paths);
        };
    }

}
