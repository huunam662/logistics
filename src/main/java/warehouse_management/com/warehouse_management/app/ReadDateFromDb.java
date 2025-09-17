package warehouse_management.com.warehouse_management.app;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Component
@ReadingConverter
public class ReadDateFromDb implements Converter<Date, LocalDateTime> {

    @Override
    public LocalDateTime convert(Date source) {
        return source == null ? null : LocalDateTime.ofInstant(source.toInstant(), ZoneOffset.UTC);
    }

}
