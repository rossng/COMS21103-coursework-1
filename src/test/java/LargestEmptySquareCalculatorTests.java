import org.junit.Assert;

public class LargestEmptySquareCalculatorTests {

    public void testGetLargestEmptySquareWhenWholeMatrixIsEmpty(LargestEmptySquareCalculator calculator) {
        Assert.assertEquals(3, calculator.getLargestEmptySquare(
                new boolean[][]{
                        new boolean[]{false, false, false},
                        new boolean[]{false, false, false},
                        new boolean[]{false, false, false}
                }
        ));
    }

    public void testGetLargestEmptySquareWhenMatrixIsPartiallyEmpty(LargestEmptySquareCalculator calculator) {
        Assert.assertEquals(2, calculator.getLargestEmptySquare(
                new boolean[][]{
                        new boolean[]{true, false, false},
                        new boolean[]{false, false, false},
                        new boolean[]{false, false, false}
                }
        ));

        Assert.assertEquals(2, calculator.getLargestEmptySquare(
                new boolean[][]{
                        new boolean[]{false, false, false},
                        new boolean[]{false, false, true},
                        new boolean[]{false, false, false}
                }
        ));

        Assert.assertEquals(1, calculator.getLargestEmptySquare(
                new boolean[][]{
                        new boolean[]{false, false, false},
                        new boolean[]{false, true, true},
                        new boolean[]{false, false, false}
                }
        ));
    }

    public void testGetLargestEmptySquareWhenMatrixIsEntirelyNonEmpty(LargestEmptySquareCalculator calculator) {
        Assert.assertEquals(0, calculator.getLargestEmptySquare(
                new boolean[][]{
                        new boolean[]{true, true, true},
                        new boolean[]{true, true, true},
                        new boolean[]{true, true, true}
                }
        ));
    }
}
