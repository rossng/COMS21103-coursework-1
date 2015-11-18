import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;
import java.text.ParseException;

public class MatrixLoaderTest {
    @Test
    public void testLoadMatrix() {
        MatrixLoader loader = new MatrixLoader();
        try {
            boolean[][] matrix = loader.deserializeMatrix(getClass().getResourceAsStream("/P1eg1.txt"));
            Assert.assertArrayEquals(matrix[0], new boolean[]{false,false,false,false,false,true,false,false,false,false,false,false});
            Assert.assertArrayEquals(matrix[1], new boolean[]{false,false,false,false,false,false,false,true,false,false,true,false});
            Assert.assertArrayEquals(matrix[2], new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false});
            Assert.assertArrayEquals(matrix[11], new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false});
        } catch (IOException e) {
            Assert.fail("An IOException was thrown.");
        } catch (ParseException e) {
            Assert.fail("A ParseException was thrown.");
        }
    }
}
