import org.junit.Test;
import org.junit.Assert;

public class RecursiveLargestEmptySquareCalculatorTest {
    RecursiveLargestEmptySquareCalculator calculator = new RecursiveLargestEmptySquareCalculator();
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
