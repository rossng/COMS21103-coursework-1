import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class DSAP3 {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Not enough arguments were supplied.");
            printUsage();
        } else {
            HighestTeamAbilityCalculator calculator = getCalculationStrategyFromFlag(stripHyphens(args[0])).get();
            try {
                InputStream input = Files.newInputStream(Paths.get(args[1]));
                WizardGuildLoader loader = new WizardGuildLoader();
                int teamAbility = calculator.getHighestTeamAbility(loader.deserializeInput(input));
                System.out.print(teamAbility);
            } catch (IOException e) {
                System.err.println("Could not open file.");
                System.err.println(e.getMessage());
            } catch (ParseException e) {
                System.err.println("Could not parse the file.");
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Strip the hyphens from the beginning of a command-line flag
     * @param flag The hyphen-prefixed command-line flag
     * @return The command-line flag without a hyphen prefix
     */
    public static String stripHyphens(String flag) {
        int i = 0;
        while (flag.charAt(i) == '-') {
            i++;
        }
        return flag.substring(i);
    }

    /**
     * Converts a command line flag to the mode to start the application in
     * @param flag The flag, excluding hyphens
     * @return The corresponding application mode
     */
    public static Optional<HighestTeamAbilityCalculator> getCalculationStrategyFromFlag(String flag) {
        String lowerCaseFlag = flag.toLowerCase();

        if (Arrays.asList("r", "recursive").contains(lowerCaseFlag)) {
            return Optional.of(new RecursiveHighestTeamAbilityCalculator());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Print usage instructions for the program.
     */
    private static void printUsage() {
        System.out.println("usage:");
        System.out.println("       dsap3 -r <filename>");
        System.out.println("       dsap3 -m <filename>");
        System.out.println("       dsap3 -i <filename>");
    }
}

class DSAP3Utils {
    /**
     * Get the smallest int int from a vararg list of ints.
     * @param ints The ints to search for the smallest value.
     * @return The smallest int in the list.
     */
    public static int min(int... ints) {
        if (ints.length == 0) {
            return 0;
        }

        int smallest = Integer.MAX_VALUE;
        for(int i : ints) {
            if (i < smallest) smallest = i;
        }
        return smallest;
    }

    /**
     * Get the largest int int from a vararg list of ints.
     * @param ints The ints to search for the largest value.
     * @return The largest int in the list.
     */
    public static int max(int... ints) {
        if (ints.length == 0) {
            return 0;
        }

        int largest = Integer.MIN_VALUE;
        for(int i : ints) {
            if (i > largest) largest = i;
        }
        return largest;
    }
}

class WizardGuildLoader {

    /**
     * Deserialize a wizard guild definition file.
     * @param input A wizard guild file.
     * @return A map of wizard ids to Wizards.
     * @throws IOException if the input cannot be read.
     * @throws ParseException if the input does not conform to the expected serialization format.
     */
    public Map<Integer, Wizard> deserializeInput(InputStream input) throws IOException, ParseException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            int numberOfWizards = Integer.parseInt(reader.readLine());
            Map<Integer, Wizard> wizards = new HashMap<>();

            for (int i = 0; i < numberOfWizards; i++) {
                String line = reader.readLine();
                Wizard deserializedWizard = deserializeWizard(line);
                wizards.put(deserializedWizard.id, deserializedWizard);
            }

            if (reader.ready()) {
                throw new ParseException("The file is longer than the specified length.", -1);
            }

            return wizards;
        }
    }

    private Wizard deserializeWizard(String line) throws ParseException {
        // Break up the line into wizard-specific details and the list of apprentices
        String[] sections = line.trim().split(":");
        if (sections.length > 2) {
            throw new ParseException(
                    String.format("Expected 2 sections, found %d in the line: %s", sections.length, line),
                    0
            );
        }

        String details = sections[0];

        // If there is an apprentice list, retrieve it - else it is an empty string
        String apprentices = "";
        try {
            apprentices = sections[1];
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        // Break the wizard details up by horizontal whitespace (\h)
        String[] detailsTokens = details.trim().split("\\h+");
        if (detailsTokens.length != 2) {
            throw new ParseException(
                    String.format("Expected 2 items in wizard details, found %d in: %s", detailsTokens.length, details),
                    0
            );
        }

        int id = Integer.parseInt(detailsTokens[0]);
        int magicalAbility = Integer.parseInt(detailsTokens[1]);

        // Break the apprentice list into tokens
        String[] apprenticesTokens = apprentices.trim().split("\\h+");
        List<Integer> apprenticeIds = Arrays
                .stream(apprenticesTokens)
                .filter(t -> t.length() > 0)
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        return new Wizard(id, magicalAbility, apprenticeIds);
    }
}

class Wizard {
    public int id;
    public int magicalAbility;
    public List<Integer> apprenticeIds;

    public Wizard(int id, int magicalAbility, List<Integer> apprenticeIds) {
        this.id = id;
        this.magicalAbility = magicalAbility;
        this.apprenticeIds = apprenticeIds;
    }
}

interface HighestTeamAbilityCalculator {
    int getHighestTeamAbility(Map<Integer, Wizard> guild);
}

class RecursiveHighestTeamAbilityCalculator implements HighestTeamAbilityCalculator {
    public int getHighestTeamAbility(Map<Integer, Wizard> guild) {
        return DSAP3Utils.max(selected(1, guild), unselected(1, guild));
    }

    private int selected(int rootWizardId, Map<Integer, Wizard> guild) {
        Wizard rootWizard = guild.get(rootWizardId);
        return rootWizard.magicalAbility +
                rootWizard
                        .apprenticeIds.stream()
                        .map(guild::get)
                        .map(w -> unselected(w.id, guild))
                        .reduce(0, (l, r) -> l + r);
    }

    private int unselected(int rootWizardId, Map<Integer, Wizard> guild) {
        Wizard rootWizard = guild.get(rootWizardId);
        return rootWizard
                .apprenticeIds.stream()
                .map(guild::get)
                .map(w -> DSAP3Utils.max(selected(w.id, guild), unselected(w.id, guild)))
                .reduce(0, (l, r) -> l + r);
    }
}
