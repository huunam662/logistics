package warehouse_management.com.warehouse_management.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.bson.types.ObjectId;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import warehouse_management.com.warehouse_management.app.WritePriceToDb;
import warehouse_management.com.warehouse_management.security.CustomUserDetail;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Configuration
@EnableMongoAuditing
public class CoreInstance {

    @Bean
    public SimpleModule objectIdModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(ObjectId.class, new JsonSerializer<ObjectId>() {
            @Override
            public void serialize(ObjectId value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value != null) {
                    gen.writeString(value.toString());
                } else {
                    gen.writeNull();
                }
            }
        });
        return module;
    }

    @Primary
    @Bean
    public AuditorAware<String> auditorWare() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken){
                return Optional.empty();
            }
            if(!(authentication.getPrincipal() instanceof CustomUserDetail user)){
                return Optional.empty();
            }
            return Optional.of((user.getId()));
        };
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
                new WritePriceToDb()
        ));
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.serializerByType(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    if (value != null) {

                        ZonedDateTime utcZdt = value.atZone(ZoneOffset.UTC);

                        ZonedDateTime hcmZdt = utcZdt.withZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"));

                        LocalDateTime timeToSerialize = hcmZdt.toLocalDateTime();

                        gen.writeString(timeToSerialize.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    } else {
                        gen.writeNull();
                    }
                }
            });
        };
    }

}
