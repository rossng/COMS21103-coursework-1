import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class DSAP1Test {
    @Test
    public void testStripHyphens() {
        Assert.assertEquals("r", DSAP1.stripHyphens("-r"));
        Assert.assertEquals("i-", DSAP1.stripHyphens("-i-"));
        Assert.assertEquals("recursive", DSAP1.stripHyphens("-recursive"));
        Assert.assertEquals("recursive", DSAP1.stripHyphens("--recursive"));
        Assert.assertEquals("memo-ized", DSAP1.stripHyphens("--memo-ized"));
    }

    @Test
    public void testGetCalculationStrategyFromFlag() {
        Assert.assertThat(
                DSAP1.getCalculationStrategyFromFlag("r").get(),
                new IsInstanceOf(RecursiveLargestEmptySquareCalculator.class)
        );
        Assert.assertThat(
                DSAP1.getCalculationStrategyFromFlag("m").get(),
                new IsInstanceOf(MemoizedLargestEmptySquareCalculator.class)
        );
        Assert.assertThat(
                DSAP1.getCalculationStrategyFromFlag("i").get(),
                new IsInstanceOf(IterativeLargestEmptySquareCalculator.class)
        );
        Assert.assertEquals(Optional.empty(), DSAP1.getCalculationStrategyFromFlag("wat"));
    }
}