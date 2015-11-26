import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WizardGuildLoaderTest {

    @Test
    public void testLoadWizardGuild() {
        WizardGuildLoader loader = new WizardGuildLoader();

        Map<String, Map<Integer, Wizard>> expectedDeserializations = new HashMap<>();

        Map<Integer, Wizard> eg1 = new HashMap<>();
        Map<Integer, Wizard> eg2 = new HashMap<>();

        eg1.put(1, new Wizard(1, 31, Arrays.asList(2, 3, 4)));
        eg1.put(2, new Wizard(2, 46, Collections.emptyList()));
        eg1.put(3, new Wizard(3, 25, Arrays.asList(5, 6)));
        eg1.put(4, new Wizard(4, 6, Collections.singletonList(7)));
        eg1.put(5, new Wizard(5, 82, Arrays.asList(8, 9)));
        eg1.put(6, new Wizard(6, 15, Collections.singletonList(10)));
        eg1.put(7, new Wizard(7, 12, Collections.emptyList()));
        eg1.put(8, new Wizard(8, 17, Collections.emptyList()));
        eg1.put(9, new Wizard(9, 4, Collections.emptyList()));
        eg1.put(10, new Wizard(10, 48, Collections.emptyList()));

        eg2.put(1, new Wizard(1, 19, Collections.singletonList(2)));
        eg2.put(2, new Wizard(2, 16, Collections.singletonList(3)));
        eg2.put(3, new Wizard(3, 8, Collections.singletonList(4)));
        eg2.put(4, new Wizard(4, 17, Collections.singletonList(5)));
        eg2.put(5, new Wizard(5, 15, Collections.singletonList(6)));
        eg2.put(6, new Wizard(6, 6, Collections.emptyList()));

        expectedDeserializations.put("/P3eg1.txt", eg1);
        expectedDeserializations.put("/P3eg2.txt", eg2);

        for (Map.Entry<String, Map<Integer, Wizard>> expectedDeserialization : expectedDeserializations.entrySet()) {
            try {
                // Attempt to deserialize the file into a guild
                Map<Integer, Wizard> guild = loader.deserializeInput(getClass().getResourceAsStream(expectedDeserialization.getKey()));

                // For each deserialized wizard in the guild, compare it to the expected deserialization
                for (Map.Entry<Integer, Wizard> actualWizard : guild.entrySet()) {
                    // Verify that the wizard appear in the expected deserialization
                    Assert.assertTrue(expectedDeserialization.getValue().containsKey(actualWizard.getKey()));

                    // Verify that the wizard's details are correct
                    Wizard expectedWizard = expectedDeserialization.getValue().get(actualWizard.getKey());
                    Assert.assertEquals(expectedWizard.magicalAbility, actualWizard.getValue().magicalAbility);
                    Assert.assertEquals(expectedWizard.apprenticeIds, actualWizard.getValue().apprenticeIds);
                }

            } catch (IOException e) {
                Assert.fail("An IOException was thrown when loading " + expectedDeserialization.getKey());
            } catch (ParseException e) {
                Assert.fail("A ParseException was thrown when loading " + expectedDeserialization.getKey());
            }
        }
    }
}
