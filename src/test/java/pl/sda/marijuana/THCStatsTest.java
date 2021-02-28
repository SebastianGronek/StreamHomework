package pl.sda.marijuana;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.sda.marijuana.data.FromJSONSnapshotProvider;
import pl.sda.marijuana.data.PriceSnapshot;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;

class THCStatsTest {
    FromJSONSnapshotProvider fromJSONSnapshotProvider = new FromJSONSnapshotProvider(Paths.get("src/test/resources/weedForTests.json"));
    final List<PriceSnapshot> priceSnapshots=fromJSONSnapshotProvider.getSnapshots();
    THCStats thcStats = new THCStats(priceSnapshots);
    @Test
    void shouldFindFiveCheapestHighQualityDeals() {
//given

//when
        List<PriceSnapshot> result = thcStats.findFiveCheapestHighQualityDeals();
//then
        FromJSONSnapshotProvider fromJSONSnapshotProviderResult = new FromJSONSnapshotProvider(Paths.get("src/test/resources/find5Cheapest.json"));
        List<PriceSnapshot> expectedResult=fromJSONSnapshotProviderResult.getSnapshots();
        assertThat(result,containsInAnyOrder(expectedResult.get(0), expectedResult.get(1),
                expectedResult.get(2),expectedResult.get(3), expectedResult.get(4)));
    }

    @Test
    void shouldFindStateWithBestAvgPriceIgnoringSnapshotsWithNull()
    {
        //given

        //when
        String result = thcStats.findStateWithBestAvgPriceIgnoringSnapshotsWithNull();
        //then
        Assertions.assertEquals(result, "California");
    }

    @Test
    void shouldFindBestPriceForAvgWeedByState() {
        //given

        //when
        Map<String, PriceSnapshot> result = thcStats.findBestPriceForAvgWeedByState();
        //then
        FromJSONSnapshotProvider fromJSONSnapshotProviderResult = new FromJSONSnapshotProvider(Paths.get("src/test/resources/bestPriceForAverageWeed.json"));
        List<PriceSnapshot> expectedResult=fromJSONSnapshotProviderResult.getSnapshots();
        assertThat(result, hasEntry(expectedResult.get(0).getState(),expectedResult.get(0)));
        assertThat(result, hasEntry(expectedResult.get(1).getState(),expectedResult.get(1)));
        assertThat(result, hasEntry(expectedResult.get(2).getState(),expectedResult.get(2)));
        assertThat(result, hasEntry(expectedResult.get(3).getState(),expectedResult.get(3)));
        assertThat(result, hasEntry(expectedResult.get(4).getState(),expectedResult.get(4)));
    }

    @Test
    void shouldFindCheapestPlaceToBuyWeedByYear() {
        //given

        //when
        Map<Integer, String> result = thcStats.findCheapestPlaceToBuyWeedByYear();
        //then
        assertThat(result, hasEntry(2014,"Arkansas"));
        assertThat(result, hasEntry(2015,"California"));

    }

    @Test
    void shouldFindCheapestPlaceToBuyWeedByMonthAndYear() {
        //given
        FromJSONSnapshotProvider fromJSONSnapshotProvider = new FromJSONSnapshotProvider(Paths.get("src/test/resources/weedForTests.json"));
        final List<PriceSnapshot> priceSnapshots=fromJSONSnapshotProvider.getSnapshots();
        THCStats thcStats = new THCStats(priceSnapshots);
        //when
        Map<String, String> result = thcStats.findCheapestPlaceToBuyWeedByMonthAndYear();
        //then
        assertThat(result, hasEntry("1-2014","Arkansas"));
        assertThat(result, hasEntry("2-2014","Arkansas"));
        assertThat(result, hasEntry("1-2015","California"));

    }
}