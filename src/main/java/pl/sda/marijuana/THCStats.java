package pl.sda.marijuana;

import lombok.RequiredArgsConstructor;
import pl.sda.marijuana.data.PriceSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

// Tests are missing.
@RequiredArgsConstructor
public class THCStats {
    private final List<PriceSnapshot> priceSnapshots;

    // No need to be static - could actually be extracted to class, say "Average" that would accept a list of bigDecimals
    // and expose `BigDecimal rounded(RoundingMode)` method
    // Or, if you want to keep it static, create a class like "BigDecimalMath" and place it there
    private static BigDecimal averageBigDecimal(List<BigDecimal> bigDecimals) {
        Optional<BigDecimal> optionalSum = bigDecimals.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal::add);

        return optionalSum
                .map(sum -> sum.divide(new BigDecimal(existingPricesCount(bigDecimals)), RoundingMode.CEILING))
                .orElse(BigDecimal.ZERO);

    }

    // No need to be static, misleading method name, this refers to general BigDecimal arithmetics
    private static long existingPricesCount(List<BigDecimal> bigDecimals) {
        return bigDecimals.stream()
                .filter(Objects::nonNull)
                .count();
    }

    // Naming suggestion: "lowestExistingPrice".
    // No need to be static
    private static BigDecimal priceToCompare(PriceSnapshot priceSnapshot) {
        if (priceSnapshot.getLowQualityPrice() != null) {
            return priceSnapshot.getLowQualityPrice();
        } else if (priceSnapshot.getMediumQualityPrice() != null) {
            return priceSnapshot.getMediumQualityPrice();
        } else {
            return priceSnapshot.getHighQualityPrice();
        }
    }

    // No need to be static - could actually be placed in PriceSnapshot
    private static String monthAndYearCombined(PriceSnapshot priceSnapshot) {
        return String.valueOf(priceSnapshot.getMonth()) + "-" + priceSnapshot.getYear();
    }

    
    // Nice
    //Metoda do sprawdzenia, ile powinno być par miesiąc-rok
    private long distinctPairsMonthAndYear() {
        // map could be shortened to method reference
        return priceSnapshots.stream().map(priceSnapshot -> monthAndYearCombined(priceSnapshot)).distinct().count();
    }


    // Nice solution, although as a brain teaser - you can try cascading collectors or even write your own, so
    // you can end up with map <State, Average price>, without Collectors.toMap in line 71.
    // Sprawdź, który stan ma ogółem najlepsze średnie ceny trawy. (ignoruj wpisy, dla których brakuje danych)
    public String findStateWithBestAvgPrice() {
        return priceSnapshots.stream().collect(Collectors
                .groupingBy(PriceSnapshot::getState, Collectors
                        .mapping(PriceSnapshot::averagePrice, Collectors.toList())))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> averageBigDecimal(e.getValue())))
                .entrySet()
                .stream()
                // nice!
                .min(Map.Entry.<String, BigDecimal>comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null); // never return null, it's better to throw an exception
    }

    //W tej metodzie odfiltrowuje snapshoty, dla których nie ma wszystkich 3 cen.
    // Żeby to zrobić, zmieniłem modyfikator dostępu metody existingPricesCount() - nie wiem, czy to dobra praktyka. [jak trzeba to trzeba]
    public String findStateWithBestAvgPriceIgnoringSnapshotsWithNull() {
        return priceSnapshots.stream().filter(priceSnapshot -> priceSnapshot.existingPricesCount()>2)
                .collect(Collectors
                        .groupingBy(PriceSnapshot::getState, Collectors
                                .mapping(PriceSnapshot::averagePrice, Collectors.toList())))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> averageBigDecimal(e.getValue())))
                .entrySet()
                .stream()
                .min(Map.Entry.<String, BigDecimal>comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }


    // OK
    // Pokaż 5 najniższych cen wysokiej jakości palenia.
    public List<PriceSnapshot> findFiveCheapestHighQualityDeals() {
        return priceSnapshots.stream()
                // +1 for Comparator.comparing with reference
                .sorted(Comparator.comparing(PriceSnapshot::getHighQualityPrice))
                .limit(5)
                .collect(Collectors.toList());
    }

    // Same brain teaser as above
    // Pokaż historycznie najlepszą cenę średniej jakości zielska dla każdego stanu.
    public Map<String, PriceSnapshot> findBestPriceForAvgWeedByState() {
        return priceSnapshots.stream()
                .collect(Collectors.groupingBy(PriceSnapshot::getState, Collectors.toList()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue()
                                .stream()
                                .min(Comparator.comparing(PriceSnapshot::getMediumQualityPrice))
                                .get()));
    }

    // Same brain teaser as above.
    // Na każdy możliwy rok pokaż stan, w którym dało się najtaniej kupić jakikolwiek towar.
    public Map<Integer, String> findCheapestPlaceToBuyWeedByYear() {
        // Since you start each method with same 5 lines, you could extract them to a method. 
        // Try creating a method that accepts a collector from line 133.
        return priceSnapshots.stream()
                .collect(Collectors.groupingBy(PriceSnapshot::getYear, Collectors.toList()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue()
                                .stream()
                                .sorted(Comparator.comparing(PriceSnapshot -> priceToCompare(PriceSnapshot)))
                                .map(PriceSnapshot::getState)
                                .findFirst()//sorted.findFirst == max 
                                .get()));
    }

    // Cool.
    // (dla ambitnych). To samo co wyżej tylko na każdą dostępną parę „rok-miesiąc”
    public Map<String, String> findCheapestPlaceToBuyWeedByMonthAndYear() {
        return priceSnapshots.stream() // Great!
                .collect(Collectors.groupingBy(THCStats::monthAndYearCombined, Collectors.toList()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue()
                                .stream()
                                .sorted(Comparator.comparing(PriceSnapshot -> priceToCompare(PriceSnapshot)))
                                .map(PriceSnapshot::getState)
                                .findFirst()
                                .get()));
    }
}
