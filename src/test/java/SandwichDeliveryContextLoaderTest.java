import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SandwichDeliveryContextLoaderTest {

    @Test
    public void testLoadProfitForecast() {
        SandwichDeliveryContextLoader loader = new SandwichDeliveryContextLoader();

        Map<String, Pair<List<Integer>, List<Integer>>> expectedDeserializations = new HashMap<>();

        expectedDeserializations.put("/P4eg1.txt", new Pair<>(
                Arrays.asList(10, 1, 7, 7),
                Arrays.asList(8, 4, 2, 1)
        ));

        expectedDeserializations.put("/P4eg2.txt", new Pair<>(
                Arrays.asList(10, 9, 9, 3, 2),
                Arrays.asList(9, 7, 5, 3, 1)
        ));

        expectedDeserializations.put("/P4eg3.txt", new Pair<>(
                Arrays.asList(5, 10, 7, 5, 5, 10, 3, 6),
                Arrays.asList(7, 5, 5, 5, 5, 5, 4, 2)
        ));

        expectedDeserializations.put("/P4eg7.txt", new Pair<>(
                Arrays.asList(
                        15, 30, 23, 6, 17, 29, 9, 30, 9, 1, 5, 15, 7, 26, 12, 9, 6, 19, 18, 25, 2, 3, 8, 28, 19,
                        10, 4, 15, 18, 21, 20, 15, 26, 27, 18, 6, 2, 9, 20, 14, 10, 6, 15, 7, 1, 25, 20, 27, 21, 8),
                Arrays.asList(
                        143, 140, 138, 137, 137, 131, 128, 124, 122, 120, 120, 120, 115, 111, 110, 109, 109, 107, 105,
                        99, 97, 93, 87, 81, 79, 74, 69, 69, 63, 62, 60, 60, 58, 58, 56, 52, 52, 47, 42, 38, 36, 33, 30,
                        27, 24, 18, 13, 9, 8, 2)
        ));

        for (Map.Entry<String, Pair<List<Integer>, List<Integer>>> deserialization : expectedDeserializations.entrySet()) {
            try {
                Pair<List<Integer>, List<Integer>> forecast =
                        loader.deserializeInput(getClass().getResourceAsStream(deserialization.getKey()));

                Assert.assertEquals(deserialization.getValue().getKey(), forecast.getKey());
                Assert.assertEquals(deserialization.getValue().getValue(), forecast.getValue());
            } catch (IOException e) {
                Assert.fail("An IOException was thrown when loading " + deserialization.getKey());
            } catch (ParseException e) {
                Assert.fail("A ParseException was thrown when loading " + deserialization.getKey());
            }
        }
    }
}
