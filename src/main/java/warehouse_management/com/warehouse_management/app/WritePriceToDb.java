package warehouse_management.com.warehouse_management.app;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@WritingConverter
public class WritePriceToDb implements Converter<BigDecimal, Decimal128> {

    @Override
    public Decimal128 convert(BigDecimal source) {
        if (source == null) return null;

        if (source.scale() == 0) {
            return new Decimal128(source);
        }

        return new Decimal128(source.setScale(2, RoundingMode.HALF_UP));
    }
}
