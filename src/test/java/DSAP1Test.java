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

public class DSAP1Test {
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

    private void testWithFlag(Map<String, String> tests, String flag) throws URISyntaxException {
        for (Map.Entry<String, String> pathAndAnswer : tests.entrySet()) {
            outContent.reset();
            errContent.reset();

            String path = Paths.get(
                    getClass()
                            .getResource(pathAndAnswer.getKey())
                            .toURI()
            ).toString();

            DSAP1.main(new String[] { flag, path });
            Assert.assertEquals(pathAndAnswer.getValue(), outContent.toString());
            Assert.assertEquals("", errContent.toString());
        }
    }

    @Test
    public void testDSAP1Recursive() throws URISyntaxException {
        Map<String, String> pathToAnswerMap = new HashMap<>();

        pathToAnswerMap.put("/P1eg1.txt", "5");
        pathToAnswerMap.put("/P1eg2.txt", "1");
        pathToAnswerMap.put("/P1eg3.txt", "2");
        pathToAnswerMap.put("/P1eg4.txt", "4");


        testWithFlag(pathToAnswerMap, "-r");
        testWithFlag(pathToAnswerMap, "--recursive");
    }

    @Test
    public void testDSAP1Memoized() throws URISyntaxException {
        Map<String, String> pathToAnswerMap = new HashMap<>();

        pathToAnswerMap.put("/P1eg1.txt", "5");
        pathToAnswerMap.put("/P1eg2.txt", "1");
        pathToAnswerMap.put("/P1eg3.txt", "2");
        pathToAnswerMap.put("/P1eg4.txt", "4");
        pathToAnswerMap.put("/P1eg5.txt", "7");
        pathToAnswerMap.put("/P1eg6.txt", "4");
        pathToAnswerMap.put("/P1eg7.txt", "13");
        pathToAnswerMap.put("/P1eg8.txt", "18");
        pathToAnswerMap.put("/P1eg9.txt", "75");
        pathToAnswerMap.put("/P1eg10.txt", "71");


        testWithFlag(pathToAnswerMap, "-m");
        testWithFlag(pathToAnswerMap, "--memoized");
    }

    @Test
    public void testDSAP1Iterative() throws URISyntaxException {
        Map<String, String> pathToAnswerMap = new HashMap<>();

        pathToAnswerMap.put("/P1eg1.txt", "5");
        pathToAnswerMap.put("/P1eg2.txt", "1");
        pathToAnswerMap.put("/P1eg3.txt", "2");
        pathToAnswerMap.put("/P1eg4.txt", "4");
        pathToAnswerMap.put("/P1eg5.txt", "7");
        pathToAnswerMap.put("/P1eg6.txt", "4");
        pathToAnswerMap.put("/P1eg7.txt", "13");
        pathToAnswerMap.put("/P1eg8.txt", "18");
        pathToAnswerMap.put("/P1eg9.txt", "75");
        pathToAnswerMap.put("/P1eg10.txt", "71");


        testWithFlag(pathToAnswerMap, "-i");
        testWithFlag(pathToAnswerMap, "--iterative");
    }
}