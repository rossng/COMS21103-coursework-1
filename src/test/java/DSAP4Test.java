import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DSAP4Test {
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @Before
    public void setUp() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void tearDown() {
        System.setOut(null);
        System.setErr(null);
    }

    @Test
    public void testStripHyphens() {
        Assert.assertEquals("r", DSAP4.stripHyphens("-r"));
        Assert.assertEquals("i-", DSAP4.stripHyphens("-i-"));
        Assert.assertEquals("recursive", DSAP4.stripHyphens("-recursive"));
        Assert.assertEquals("recursive", DSAP4.stripHyphens("--recursive"));
        Assert.assertEquals("memo-ized", DSAP4.stripHyphens("--memo-ized"));
    }

    @Test
    public void testGetCalculationStrategyFromFlag() {
        Assert.assertThat(
                DSAP4.getCalculationStrategyFromFlag("r").get(),
                new IsInstanceOf(RecursiveMostSandwichesDeliverableCalculator.class)
        );
        Assert.assertThat(
                DSAP4.getCalculationStrategyFromFlag("m").get(),
                new IsInstanceOf(MemoizedMostSandwichesDeliverableCalculator.class)
        );
        Assert.assertThat(
                DSAP4.getCalculationStrategyFromFlag("i").get(),
                new IsInstanceOf(IterativeMostSandwichesDeliverableCalculator.class)
        );
        Assert.assertEquals(Optional.empty(), DSAP4.getCalculationStrategyFromFlag("wat"));
    }

    private void testWithFlag(Map<String, String> tests, String flag) throws URISyntaxException {
        for (Map.Entry<String, String> pathAndAnswer : tests.entrySet()) {
            outContent.reset();
            errContent.reset();

            String path = Paths.get(
                    getClass()
                            .getResource(pathAndAnswer.getKey())
                            .toURI()
            ).toString();

            DSAP4.main(new String[] { flag, path });
            Assert.assertEquals(pathAndAnswer.getValue(), outContent.toString());
            Assert.assertEquals("", errContent.toString());
        }
    }

    @Test
    public void testDSAP4Recursive() throws URISyntaxException {
        Map<String, String> pathToAnswerMap = new HashMap<>();

        pathToAnswerMap.put("/P4eg1.txt", "19");
        pathToAnswerMap.put("/P4eg2.txt", "25");
        pathToAnswerMap.put("/P4eg3.txt", "36");
        pathToAnswerMap.put("/P4eg4.txt", "105");
        pathToAnswerMap.put("/P4eg5.txt", "45");
        pathToAnswerMap.put("/P4eg6.txt", "219");

        testWithFlag(pathToAnswerMap, "-r");
        testWithFlag(pathToAnswerMap, "--recursive");
    }

    @Test
    public void testDSAP4Memoized() throws URISyntaxException {
        Map<String, String> pathToAnswerMap = new HashMap<>();

        pathToAnswerMap.put("/P4eg1.txt", "19");
        pathToAnswerMap.put("/P4eg2.txt", "25");
        pathToAnswerMap.put("/P4eg3.txt", "36");
        pathToAnswerMap.put("/P4eg4.txt", "105");
        pathToAnswerMap.put("/P4eg5.txt", "45");
        pathToAnswerMap.put("/P4eg6.txt", "219");
        pathToAnswerMap.put("/P4eg7.txt", "735");
        pathToAnswerMap.put("/P4eg8.txt", "1503");
        pathToAnswerMap.put("/P4eg9.txt", "20571");
        pathToAnswerMap.put("/P4eg10.txt", "349");

        testWithFlag(pathToAnswerMap, "-m");
        testWithFlag(pathToAnswerMap, "--memoized");
    }

    @Test
    public void testDSAP4Iterative() throws URISyntaxException {
        Map<String, String> pathToAnswerMap = new HashMap<>();

        pathToAnswerMap.put("/P4eg1.txt", "19");
        pathToAnswerMap.put("/P4eg2.txt", "25");
        pathToAnswerMap.put("/P4eg3.txt", "36");
        pathToAnswerMap.put("/P4eg4.txt", "105");
        pathToAnswerMap.put("/P4eg5.txt", "45");
        pathToAnswerMap.put("/P4eg6.txt", "219");
        pathToAnswerMap.put("/P4eg7.txt", "735");
        pathToAnswerMap.put("/P4eg8.txt", "1503");
        pathToAnswerMap.put("/P4eg9.txt", "20571");
        pathToAnswerMap.put("/P4eg10.txt", "349");

        testWithFlag(pathToAnswerMap, "-i");
        testWithFlag(pathToAnswerMap, "--iterative");
    }
}