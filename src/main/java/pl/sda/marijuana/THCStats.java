package pl.sda.marijuana;

import lombok.RequiredArgsConstructor;
import pl.sda.marijuana.data.FromJSONSnapshotProvider;
import pl.sda.marijuana.data.PriceSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class THCStats {
    private final List<PriceSnapshot> priceSnapshots;

    private static BigDecimal averageBigDecimal(List<BigDecimal> bigDecimals) {
        Optional<BigDecimal> optionalSum = bigDecimals.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal::add);

        return optionalSum
                .map(sum -> sum.divide(new BigDecimal(existingPricesCount(bigDecimals)), RoundingMode.CEILING))
                .orElse(BigDecimal.ZERO);

    }

    private static long existingPricesCount(List<BigDecimal> bigDecimals) {
        return bigDecimals.stream()
                .filter(Objects::nonNull)
                .count();
    }

    private static BigDecimal priceToCompare(PriceSnapshot priceSnapshot) {
        if (priceSnapshot.getLowQualityPrice() != null) {
            return priceSnapshot.getLowQualityPrice();
        } else if (priceSnapshot.getMediumQualityPrice() != null) {
            return priceSnapshot.getMediumQualityPrice();
        } else {
            return priceSnapshot.getHighQualityPrice();
        }
    }

    private static String monthAndYearCombined(PriceSnapshot priceSnapshot) {
        return String.valueOf(priceSnapshot.getMonth()) + "-" + priceSnapshot.getYear();
    }

    //Metoda do sprawdzenia, ile powinno być par miesiąc-rok
    private long distinctPairsMonthAndYear() {
        return priceSnapshots.stream().map(priceSnapshot -> monthAndYearCombined(priceSnapshot)).distinct().count();
    }


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
                .min(Map.Entry.<String, BigDecimal>comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public String findStateWithBestAvgPriceIgnoringSnapshotsWithNull() {
        return priceSnapshots.stream().filter(priceSnapshot -> priceSnapshot.getLowQualityPrice() != null)
                .filter(priceSnapshot -> priceSnapshot.getMediumQualityPrice() != null)
                .filter(priceSnapshot -> priceSnapshot.getHighQualityPrice() != null)
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


    // Pokaż 5 najniższych cen wysokiej jakości palenia.
    public List<PriceSnapshot> findFiveCheapestHighQualityDeals() {
        return priceSnapshots.stream()
                .sorted(Comparator.comparing(PriceSnapshot::getHighQualityPrice))
                .limit(5)
                .collect(Collectors.toList());
    }

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

    // Na każdy możliwy rok pokaż stan, w którym dało się najtaniej kupić jakikolwiek towar.
    public Map<Integer, String> findCheapestPlaceToBuyWeedByYear() {
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
                                .findFirst()
                                .get()));
    }

    // (dla ambitnych). To samo co wyżej tylko na każdą dostępną parę „rok-miesiąc”
    public Map<String, String> findCheapestPlaceToBuyWeedByMonthAndYear() {
        return priceSnapshots.stream()
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

    public static void main(String[] args) {
        Path path = Paths.get("src/main/resources/weed.json");
        FromJSONSnapshotProvider fromJSONSnapshotProvider = new FromJSONSnapshotProvider(path);
        THCStats thcStats = new THCStats(fromJSONSnapshotProvider.getSnapshots());

        //System.out.println(thcStats.findFiveCheapestHighQualityDeals());
        //System.out.println(thcStats.findStateWithBestAvgPrice());
        //System.out.println(thcStats.findBestPriceForAvgWeedByState());
        //System.out.println(thcStats.findCheapestPlaceToBuyWeedByYear());
        //System.out.println(thcStats.findCheapestPlaceToBuyWeedByMonthAndYear());
        Map<String, String> result = thcStats.findCheapestPlaceToBuyWeedByMonthAndYear();
        System.out.println(result);
        System.out.println(result.size());

    }
}
