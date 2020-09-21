package pl.sda.marijuana;

import lombok.RequiredArgsConstructor;
import pl.sda.marijuana.data.PriceSnapshot;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class THCStats {
    private final List<PriceSnapshot> priceSnapshots;

    // Sprawdź, który stan ma ogółem najlepsze średnie ceny trawy. (ignoruj wpisy, dla których brakuje danych)
    public String findStateWithBestAvgPrice() {
        //TODO
    }

    // Pokaż 5 najniższych cen wysokiej jakości palenia.
    public List<PriceSnapshot> findFiveCheapestHighQualityDeals() {
        //TODO
    }

    // Pokaż historycznie najlepszą cenę średniej jakości zielska dla każdego stanu.
    public Map<String, PriceSnapshot> findBestPriceForAvgWeedByState() {
        //TODO
    }

    // Na każdy możliwy rok pokaż stan, w którym dało się najtaniej kupić jakikolwiek towar.
    public Map<Integer, String> findCheapestPlaceToBuyWeedByYear() {
        //TODO
    }

    // (dla ambitnych). To samo co wyżej tylko na każdą dostępną parę „rok-miesiąc”
    public Map<String, String> findCheapestPlaceToBuyWeedByMonthAndYear() {
        //TODO
    }
}
