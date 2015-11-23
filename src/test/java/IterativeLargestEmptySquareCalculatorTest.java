import org.junit.Test;

public class IterativeLargestEmptySquareCalculatorTest {
    IterativeLargestEmptySquareCalculator calculator = new IterativeLargestEmptySquareCalculator();
    LargestEmptySquareCalculatorTests tests = new LargestEmptySquareCalculatorTests();

    @Test
    public void testGetLargestEmptySquareWhenWholeMatrixIsEmpty() {
        tests.testGetLargestEmptySquareWhenWholeMatrixIsEmpty(calculator);
    }

    @Test
    public void testGetLargestEmptySquareWhenMatrixIsPartiallyEmpty() {
        tests.testGetLargestEmptySquareWhenMatrixIsPartiallyEmpty(calculator);
    }

    @Test
    public void testGetLargestEmptySquareWhenMatrixIsEntirelyNonEmpty() {
        tests.testGetLargestEmptySquareWhenMatrixIsEntirelyNonEmpty(calculator);
    }
}
