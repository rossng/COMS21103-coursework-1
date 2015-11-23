import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Optional;

public class DSAP1 {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Not enough arguments were supplied.");
            printUsage();
        } else {
            LargestEmptySquareCalculator calculator = getCalculationStrategyFromFlag(stripHyphens(args[0])).get();
            try {
                InputStream input = Files.newInputStream(Paths.get(args[1]));
                MatrixLoader loader = new MatrixLoader();
                int largestEmptySquare = calculator.getLargestEmptySquare(loader.deserializeMatrix(input));
                System.out.print(largestEmptySquare);
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
    public static Optional<LargestEmptySquareCalculator> getCalculationStrategyFromFlag(String flag) {
        String lowerCaseFlag = flag.toLowerCase();

        if (Arrays.asList("r", "recursive").contains(lowerCaseFlag)) {
            return Optional.of(new RecursiveLargestEmptySquareCalculator());
        } else if (Arrays.asList("m", "memoized").contains(lowerCaseFlag)) {
            return Optional.of(new MemoizedLargestEmptySquareCalculator());
        } else if (Arrays.asList("i", "iterative").contains(lowerCaseFlag)) {
            return Optional.of(new IterativeLargestEmptySquareCalculator());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Print usage instructions for the program.
     */
    private static void printUsage() {
        System.out.println("usage:");
        System.out.println("       dsap1 -r <filename>");
        System.out.println("       dsap1 -m <filename>");
        System.out.println("       dsap1 -i <filename>");
    }

}

class MatrixLoader {

    /**
     * Deserialize a matrix definition file into a nested boolean array representing the same matrix.
     * @param input A matrix definition file.
     * @return A 2D boolean array corresponding to the input file supplied.
     * @throws IOException if the input cannot be read.
     * @throws ParseException if the input does not conform to the expected serialization format.
     */
    public boolean[][] deserializeMatrix(InputStream input) throws IOException, ParseException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            int sideLength = Integer.parseUnsignedInt(reader.readLine());
            boolean[][] matrix = new boolean[sideLength][sideLength];

            String line = null;
            for (int currentLine = 0; currentLine < sideLength; currentLine++) {
                line = reader.readLine();
                if (line.length() != sideLength) {
                    throw new ParseException("The current line is not the specified length.", currentLine);
                }
                matrix[currentLine] = deserializeToBooleanArray(line, sideLength);
            }

            if (reader.ready()) {
                throw new ParseException("The file is longer than the specified length.", -1);
            }

            return matrix;
        }
    }

    /**
     * Given a line of input from a matrix definition file, deserialize it to an array of booleans of the length
     * specified. Discard any further input.
     * @param line The line from the input file.
     * @param length The length of the array to be returned.
     * @return An array of booleans corresponding to the input string, the length of which is equal to the length
     * parameter.
     * @throws ParseException if unexpected characters are found.
     */
    private boolean[] deserializeToBooleanArray(String line, int length) throws ParseException {
        boolean[] bools = new boolean[length];
        for (int i = 0; i < length; i++) {
            char currentChar = line.charAt(i);
            if (currentChar == '0') {
                bools[i] = false;
            } else if (currentChar == '1') {
                bools[i] = true;
            } else {
                throw new ParseException("Booleans must be represented by '0' or '1'.", i);
            }
        }
        return bools;
    }
}

interface LargestEmptySquareCalculator {
    public int getLargestEmptySquare(boolean[][] matrix);
}

class DSAP1Utils {
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
}

class RecursiveLargestEmptySquareCalculator implements LargestEmptySquareCalculator {
    public int getLargestEmptySquare(boolean[][] matrix) {
        int largestEmptySquare = 0;
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[0].length; x++) {
                int s = les(matrix, x, y);
                if (s > largestEmptySquare) {
                    largestEmptySquare = s;
                }
            }
        }
        return largestEmptySquare;
    }

    /**
     * Get the side length of the largest empty square whose bottom right corner is at (x,y)
     * @param matrix The matrix of empty and non-empty cells to find the largest empty square in
     * @param x The x-coordinate of the bottom right corner of the largest empty square
     * @param y The y-coordinate of the bottom right corner of the largest empty square
     * @return The side length of the largest empty square in this position
     */
    private int les(boolean[][] matrix, int x, int y) {
        if (matrix[y][x]) {
            return 0;
        } else if (x == 0 || y == 0) {
            return 1;
        } else {
            return DSAP1Utils.min(
                    les(matrix, x-1, y-1),
                    les(matrix, x-1, y),
                    les(matrix, x, y-1)
            ) + 1;
        }
    }
}

class MemoizedLargestEmptySquareCalculator implements LargestEmptySquareCalculator {
    public int getLargestEmptySquare(boolean[][] matrix) {
        Integer[][] memory = new Integer[matrix.length][matrix.length];
        int largestEmptySquare = 0;
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[0].length; x++) {
                int s = les(matrix, x, y, memory);
                if (s > largestEmptySquare) {
                    largestEmptySquare = s;
                }
            }
        }
        return largestEmptySquare;
    }

    private int les(boolean[][] matrix, int x, int y, Integer[][] memory) {
        if (matrix[y][x]) {
            return 0;
        } else if (x == 0 || y == 0) {
            return 1;
        } else if (memory[y][x] == null) {
            memory[y][x] = DSAP1Utils.min(
                    les(matrix, x-1, y-1, memory),
                    les(matrix, x-1, y, memory),
                    les(matrix, x, y-1, memory)
            ) + 1;
        }
        return memory[y][x];
    }
}

class IterativeLargestEmptySquareCalculator implements LargestEmptySquareCalculator {
    public int getLargestEmptySquare(boolean[][] matrix) {
        Integer[][] memory = new Integer[matrix.length][matrix.length];
        int largestEmptySquare = 0;
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[0].length; x++) {
                if (matrix[y][x]) {
                    memory[y][x] = 0;
                } else if (x == 0 || y == 0) {
                    memory[y][x] = 1;
                } else {
                    memory[y][x] = DSAP1Utils.min(
                            memory[y-1][x-1],
                            memory[y][x-1],
                            memory[y-1][x]
                    ) + 1;
                }

                if (memory[y][x] > largestEmptySquare) {
                    largestEmptySquare = memory[y][x];
                }
            }
        }
        return largestEmptySquare;
    }
}