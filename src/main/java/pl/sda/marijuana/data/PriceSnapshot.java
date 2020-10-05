package pl.sda.marijuana.data;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceSnapshot {
    private String state;
    private BigDecimal highQualityPrice;
    private BigDecimal mediumQualityPrice;
    private BigDecimal lowQualityPrice;
    private Integer month;
    private Integer year;

    public BigDecimal averagePrice() {
        Optional<BigDecimal> optionalSum = Stream.of(
                highQualityPrice,
                mediumQualityPrice,
                lowQualityPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal::add);

        return optionalSum
                .map(sum -> sum.divide(new BigDecimal(existingPricesCount()), RoundingMode.CEILING))
                .orElse(BigDecimal.ZERO);


    }

    private long existingPricesCount() {
        return Stream.of(
                highQualityPrice,
                mediumQualityPrice,
                lowQualityPrice)
                .filter(Objects::nonNull)
                .count();
    }
}
