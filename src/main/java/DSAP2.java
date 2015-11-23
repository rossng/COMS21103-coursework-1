import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DSAP2 {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Not enough arguments were supplied.");
            printUsage();
        } else {
            MaximumCornerShopProfitCalculator calculator = getCalculationStrategyFromFlag(stripHyphens(args[0])).get();
            try {
                InputStream input = Files.newInputStream(Paths.get(args[1]));
                ProfitForecastLoader loader = new ProfitForecastLoader();
                int maximumProfit = calculator.getMaxProfit(loader.deserializeInput(input));
                System.out.print(maximumProfit);
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
    public static Optional<MaximumCornerShopProfitCalculator> getCalculationStrategyFromFlag(String flag) {
        String lowerCaseFlag = flag.toLowerCase();

        if (Arrays.asList("r", "recursive").contains(lowerCaseFlag)) {
            return Optional.of(new RecursiveMaximumCornerShopProfitCalculator());
        } else if (Arrays.asList("m", "memoized").contains(lowerCaseFlag)) {
            return Optional.of(new MemoizedMaximumCornerShopProfitCalculator());
        } else if (Arrays.asList("i", "iterative").contains(lowerCaseFlag)) {
            return Optional.of(new IterativeMaximumCornerShopProfitCalculator());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Print usage instructions for the program.
     */
    private static void printUsage() {
        System.out.println("usage:");
        System.out.println("       dsap2 -r <filename>");
        System.out.println("       dsap2 -m <filename>");
        System.out.println("       dsap2 -i <filename>");
    }

}

class ProfitForecastMetadata {
    public int numberOfDays;
    public int stockChangeCost;

    public ProfitForecastMetadata(int numberOfDays, int stockChangeCost) {
        this.numberOfDays = numberOfDays;
        this.stockChangeCost = stockChangeCost;
    }
}

class ProfitForecast {
    public ProfitForecastMetadata metadata;
    public List<Integer> umbrellaProfitForecast;
    public List<Integer> suncreamProfitForecast;

    public ProfitForecast(List<Integer> umbrellaProfitForecast, List<Integer> suncreamProfitForecast, ProfitForecastMetadata metadata) {
        this.umbrellaProfitForecast = umbrellaProfitForecast;
        this.suncreamProfitForecast = suncreamProfitForecast;
        this.metadata = metadata;
    }
}

class ProfitForecastLoader {

    /**
     * Deserialize a profit forecast definition file.
     * @param input A profit forecast file.
     * @return A 2D boolean array corresponding to the input file supplied.
     * @throws IOException if the input cannot be read.
     * @throws ParseException if the input does not conform to the expected serialization format.
     */
    public ProfitForecast deserializeInput(InputStream input) throws IOException, ParseException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            ProfitForecastMetadata metadata = deserializeMetadata(reader.readLine());
            List<Integer> umbrellaProfitForecast = deserializeProfitList(reader.readLine(), metadata.numberOfDays);
            List<Integer> suncreamProfitForecast = deserializeProfitList(reader.readLine(), metadata.numberOfDays);

            if (reader.ready()) {
                throw new ParseException("The file is longer than the specified length.", -1);
            }

            return new ProfitForecast(umbrellaProfitForecast, suncreamProfitForecast, metadata);
        }
    }

    private ProfitForecastMetadata deserializeMetadata(String line) throws ParseException {
        // Break the line up by horizontal whitespace (\h)
        String[] tokens = line.trim().split("\\h+");

        // Check that the line is the correct length
        if (tokens.length != 2) {
            throw new ParseException(String.format("Expected 2 items, found %d in the line: %s", tokens.length, line), 0);
        }

        int numberOfDays = Integer.parseInt(tokens[0]);
        int stockChangeCost = Integer.parseInt(tokens[1]);

        return new ProfitForecastMetadata(numberOfDays, stockChangeCost);
    }

    private List<Integer> deserializeProfitList(String line, int numberOfItems) throws ParseException {
        // Break the line up by horizontal whitespace (\h)
        String[] tokens = line.trim().split("\\h+");

        // Check that the line is the correct length
        if (tokens.length != numberOfItems) {
            throw new ParseException(String.format("Expected %d items, found %d in the line: %s", numberOfItems, tokens.length, line), 0);
        }

        // Parse each number in the line
        List<Integer> profits = new ArrayList<>();

        for (String token : tokens) {
            profits.add(Integer.parseInt(token));
        }

        return profits;
    }
}

class DSAP2Utils {
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

interface MaximumCornerShopProfitCalculator {
    int getMaxProfit(ProfitForecast forecast);
    enum Item { UMBRELLA, SUNCREAM }
}

class RecursiveMaximumCornerShopProfitCalculator implements MaximumCornerShopProfitCalculator {
    public int getMaxProfit(ProfitForecast forecast) {
        return DSAP2Utils.max(
                p(forecast.metadata.numberOfDays, Item.UMBRELLA, forecast),
                p(forecast.metadata.numberOfDays, Item.SUNCREAM, forecast)
        );
    }

    private int p(int days, Item finishOnItem, ProfitForecast forecast) {
        if (days == 0) return 0;

        else if (days > 0) switch (finishOnItem) {
            case UMBRELLA:
                return DSAP2Utils.max(
                        p(days - 1, Item.SUNCREAM, forecast) - forecast.metadata.stockChangeCost,
                        p(days - 1, Item.UMBRELLA, forecast)
                ) + forecast.umbrellaProfitForecast.get(days - 1);
            case SUNCREAM:
                return DSAP2Utils.max(
                        p(days - 1, Item.SUNCREAM, forecast),
                        p(days - 1, Item.UMBRELLA, forecast) - forecast.metadata.stockChangeCost
                ) + forecast.suncreamProfitForecast.get(days - 1);
        }

        throw new IllegalArgumentException("Expected days >= 1, found " + days);
    }
}

class MemoizedMaximumCornerShopProfitCalculator implements MaximumCornerShopProfitCalculator {
    public int getMaxProfit(ProfitForecast forecast) {
        return 0;
    }
}

class IterativeMaximumCornerShopProfitCalculator implements MaximumCornerShopProfitCalculator {
    public int getMaxProfit(ProfitForecast forecast) {
        return 0;
    }
}