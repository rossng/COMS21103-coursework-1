import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProfitForecastLoaderTest {

    @Test
    public void testLoadProfitForecast() {
        ProfitForecastLoader loader = new ProfitForecastLoader();

        Map<String, ProfitForecast> expectedDeserializations = new HashMap<>();

        expectedDeserializations.put("/P2eg1.txt", new ProfitForecast(
                Arrays.asList(5, 13, 15, 5, 5),
                Arrays.asList(30, 20, 2, 14, 14),
                new ProfitForecastMetadata(5, 10)
        ));

        expectedDeserializations.put("/P2eg2.txt", new ProfitForecast(
                Arrays.asList(6, 4, 6, 4, 4),
                Arrays.asList(2, 6, 5, 6, 4),
                new ProfitForecastMetadata(5, 7)
        ));

        expectedDeserializations.put("/P2eg3.txt", new ProfitForecast(
                Arrays.asList(4, 8, 8, 9, 6, 7, 10, 10),
                Arrays.asList(6, 7, 10, 2, 4, 9, 3, 7),
                new ProfitForecastMetadata(8, 3)
        ));

        expectedDeserializations.put("/P2eg7.txt", new ProfitForecast(
                Arrays.asList(2, 25, 11, 4, 14, 10, 29, 26, 25, 7, 13, 11, 1, 8, 30, 22, 31, 31, 6, 15, 24, 29, 29, 27, 30, 17, 5, 10, 31, 11, 29, 19, 5, 23, 28, 7, 4, 24, 2, 23, 5, 19, 7, 13, 25, 5, 29, 10, 14, 10),
                Arrays.asList(22, 15, 17, 14, 29, 22, 2, 31, 6, 25, 27, 18, 17, 12, 13, 30, 5, 28, 13, 8, 17, 25, 6, 29, 22, 16, 16, 1, 31, 31, 27, 24, 3, 30, 1, 7, 30, 25, 29, 28, 12, 22, 7, 26, 12, 14, 31, 14, 15, 13),
                new ProfitForecastMetadata(50, 17)
        ));

        for (Map.Entry<String, ProfitForecast> deserialization : expectedDeserializations.entrySet()) {
            try {
                ProfitForecast forecast = loader.deserializeInput(getClass().getResourceAsStream(deserialization.getKey()));

                Assert.assertEquals(deserialization.getValue().umbrellaProfitForecast, forecast.umbrellaProfitForecast);
                Assert.assertEquals(deserialization.getValue().suncreamProfitForecast, forecast.suncreamProfitForecast);

                Assert.assertEquals(deserialization.getValue().metadata.numberOfDays, forecast.metadata.numberOfDays);
                Assert.assertEquals(deserialization.getValue().metadata.stockChangeCost, forecast.metadata.stockChangeCost);
            } catch (IOException e) {
                Assert.fail("An IOException was thrown when loading " + deserialization.getKey());
            } catch (ParseException e) {
                Assert.fail("A ParseException was thrown when loading " + deserialization.getKey());
            }
        }
    }
}
