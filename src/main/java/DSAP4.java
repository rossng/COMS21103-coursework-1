import javafx.util.Pair;

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

public class DSAP4 {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Not enough arguments were supplied.");
            printUsage();
        } else {
            MostSandwichesDeliverableCalculator calculator = getCalculationStrategyFromFlag(stripHyphens(args[0])).get();
            try {
                InputStream input = Files.newInputStream(Paths.get(args[1]));
                SandwichDeliveryContextLoader loader = new SandwichDeliveryContextLoader();
                Pair<List<Integer>, List<Integer>> sandwichDeliveryContext = loader.deserializeInput(input);

                int mostDeliverableSandwiches = calculator.getMostSandwichesDeliverable(
                        sandwichDeliveryContext.getKey(),
                        sandwichDeliveryContext.getValue()
                );
                System.out.print(mostDeliverableSandwiches);
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
    public static Optional<MostSandwichesDeliverableCalculator> getCalculationStrategyFromFlag(String flag) {
        String lowerCaseFlag = flag.toLowerCase();

        if (Arrays.asList("r", "recursive").contains(lowerCaseFlag)) {
            return Optional.of(new RecursiveMostSandwichesDeliverableCalculator());
        } else if (Arrays.asList("m", "memoized").contains(lowerCaseFlag)) {
            return Optional.of(new MemoizedMostSandwichesDeliverableCalculator());
        } else if (Arrays.asList("i", "iterative").contains(lowerCaseFlag)) {
            return Optional.of(new IterativeMostSandwichesDeliverableCalculator());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Print usage instructions for the program.
     */
    private static void printUsage() {
        System.out.println("usage:");
        System.out.println("       DSAP4 -r <filename>");
        System.out.println("       DSAP4 -m <filename>");
        System.out.println("       DSAP4 -i <filename>");
    }

}

class SandwichDeliveryContextLoader {

    /**
     * Deserialize a sandwich delivery context definition file.
     * @param input A sanwich delivery context file.
     * @return A pair of integer lists corresponding to the input file supplied: the number of sandwich orders per day
     * and the maximum number of sandwiches deliverable each day after a rest day
     * @throws IOException if the input cannot be read.
     * @throws ParseException if the input does not conform to the expected serialization format.
     */
    public Pair<List<Integer>, List<Integer>> deserializeInput(InputStream input) throws IOException, ParseException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            int numberOfDays = Integer.parseInt(reader.readLine());
            List<Integer> sandwichOrders = deserializeList(reader.readLine(), numberOfDays);
            List<Integer> sandwichDeliveryMaxima = deserializeList(reader.readLine(), numberOfDays);

            if (reader.ready()) {
                throw new ParseException("The file is longer than the specified length.", -1);
            }

            return new Pair<>(sandwichOrders, sandwichDeliveryMaxima);
        }
    }

    private List<Integer> deserializeList(String line, int numberOfItems) throws ParseException {
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

class DSAP4Utils {
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

interface MostSandwichesDeliverableCalculator {
    int getMostSandwichesDeliverable(List<Integer> forecast, List<Integer> sandwichDeliveryMaxima);
}

class RecursiveMostSandwichesDeliverableCalculator implements MostSandwichesDeliverableCalculator {
    public int getMostSandwichesDeliverable(List<Integer> sandwichOrders, List<Integer> sandwichDeliveryMaxima) {
        return sandwiches(1, sandwichOrders.size(), 0, sandwichOrders, sandwichDeliveryMaxima);
    }

    private int sandwiches(int fromDay, int toDay, int startingRunLength, List<Integer> sandwichOrders, List<Integer> sandwichDeliveryMaxima) {
        if (fromDay > toDay) return 0;
        else if (fromDay == toDay) return DSAP4Utils.min(
                sandwichOrders.get(fromDay - 1),
                sandwichDeliveryMaxima.get(startingRunLength)
        );
        else if (startingRunLength == 0) return DSAP4Utils.min(
                sandwichOrders.get(fromDay - 1),
                sandwichDeliveryMaxima.get(0)
        ) + sandwiches(fromDay + 1, toDay, 1, sandwichOrders, sandwichDeliveryMaxima);
        else return DSAP4Utils.max(
                    DSAP4Utils.min(
                            sandwichOrders.get(fromDay - 1),
                            sandwichDeliveryMaxima.get(startingRunLength)
                    ) + sandwiches(fromDay + 1, toDay, startingRunLength + 1, sandwichOrders, sandwichDeliveryMaxima),
                    sandwiches(fromDay + 1, toDay, 0, sandwichOrders, sandwichDeliveryMaxima)
            );
    }
}

class MemoizedMostSandwichesDeliverableCalculator implements MostSandwichesDeliverableCalculator {
    public int getMostSandwichesDeliverable(List<Integer> forecast, List<Integer> sandwichDeliveryMaxima) {
        return 0;
    }
}

class IterativeMostSandwichesDeliverableCalculator implements MostSandwichesDeliverableCalculator {
    public int getMostSandwichesDeliverable(List<Integer> forecast, List<Integer> sandwichDeliveryMaxima) {
        return 0;
    }
}