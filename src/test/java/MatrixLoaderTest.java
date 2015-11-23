import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class MatrixLoaderTest {
    class MatrixRow {
        public int rowNumber;
        public boolean[] rowContents;
        public MatrixRow(int rowNumber, boolean[] rowContents) {
            this.rowNumber = rowNumber;
            this.rowContents = rowContents;
        }
    }

    @Test
    public void testLoadMatrix() {
        MatrixLoader loader = new MatrixLoader();

        Map<String, MatrixRow[]> expectedDeserializations = new HashMap<>();

        expectedDeserializations.put("/P1eg1.txt", new MatrixRow[] {
                new MatrixRow(0, new boolean[]{false,false,false,false,false,true,false,false,false,false,false,false}),
                new MatrixRow(1, new boolean[]{false,false,false,false,false,false,false,true,false,false,true,false}),
                new MatrixRow(2, new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false}),
                new MatrixRow(11, new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false})
        });

        expectedDeserializations.put("/P1eg2.txt", new MatrixRow[] {
                new MatrixRow(0, new boolean[]{true,false,false,true,false}),
                new MatrixRow(1, new boolean[]{true,true,false,true,true}),
                new MatrixRow(2, new boolean[]{true,true,true,true,false}),
                new MatrixRow(4, new boolean[]{true,true,true,false,true})
        });

        expectedDeserializations.put("/P1eg1.txt", new MatrixRow[] {
                new MatrixRow(0, new boolean[]{false,false,false,false,false,true,false,false,false,false,false,false}),
                new MatrixRow(1, new boolean[]{false,false,false,false,false,false,false,true,false,false,true,false}),
                new MatrixRow(2, new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false}),
                new MatrixRow(11, new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false})
        });

        for (Map.Entry<String, MatrixRow[]> deserialization : expectedDeserializations.entrySet()) {
            try {
                boolean[][] matrix = loader.deserializeMatrix(getClass().getResourceAsStream(deserialization.getKey()));
                for (MatrixRow deserializedRow : deserialization.getValue()) {
                    Assert.assertArrayEquals(matrix[deserializedRow.rowNumber], deserializedRow.rowContents);
                }
            } catch (IOException e) {
                Assert.fail("An IOException was thrown when loading " + deserialization.getKey());
            } catch (ParseException e) {
                Assert.fail("A ParseException was thrown when loading " + deserialization.getKey());
            }
        }
    }
}
