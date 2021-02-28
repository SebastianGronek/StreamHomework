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

    public long existingPricesCount() {
        return Stream.of(
                highQualityPrice,
                mediumQualityPrice,
                lowQualityPrice)
                .filter(Objects::nonNull)
                .count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceSnapshot that = (PriceSnapshot) o;
        return Objects.equals(state, that.state) &&
                Objects.equals(highQualityPrice, that.highQualityPrice) &&
                Objects.equals(mediumQualityPrice, that.mediumQualityPrice) &&
                Objects.equals(lowQualityPrice, that.lowQualityPrice) &&
                Objects.equals(month, that.month) &&
                Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, highQualityPrice, mediumQualityPrice, lowQualityPrice, month, year);
    }
}
