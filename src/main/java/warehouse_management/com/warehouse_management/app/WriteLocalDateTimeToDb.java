package warehouse_management.com.warehouse_management.app;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Component
@WritingConverter
public class WriteLocalDateTimeToDb implements Converter<LocalDateTime, Date> {

    @Override
    public Date convert(LocalDateTime source) {
        return source == null ? null : Date.from(source.toInstant(ZoneOffset.UTC));
    }

}