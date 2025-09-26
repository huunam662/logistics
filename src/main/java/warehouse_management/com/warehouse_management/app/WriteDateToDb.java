package warehouse_management.com.warehouse_management.app;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;


@WritingConverter
public class WriteDateToDb implements Converter<LocalDateTime, Date> {

    @Override
    public Date convert(LocalDateTime source) {
        return source == null ? null : Date.from(source.atZone(ZoneOffset.UTC).toInstant());
    }
}

