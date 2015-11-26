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

public class DSAP2Test {
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
        Assert.assertEquals("r", DSAP2.stripHyphens("-r"));
        Assert.assertEquals("i-", DSAP2.stripHyphens("-i-"));
        Assert.assertEquals("recursive", DSAP2.stripHyphens("-recursive"));
        Assert.assertEquals("recursive", DSAP2.stripHyphens("--recursive"));
        Assert.assertEquals("memo-ized", DSAP2.stripHyphens("--memo-ized"));
    }

    @Test
    public void testGetCalculationStrategyFromFlag() {
        Assert.assertThat(
                DSAP2.getCalculationStrategyFromFlag("r").get(),
                new IsInstanceOf(RecursiveMaximumCornerShopProfitCalculator.class)
        );
        Assert.assertThat(
                DSAP2.getCalculationStrategyFromFlag("m").get(),
                new IsInstanceOf(MemoizedMaximumCornerShopProfitCalculator.class)
        );
        Assert.assertThat(
                DSAP2.getCalculationStrategyFromFlag("i").get(),
                new IsInstanceOf(IterativeMaximumCornerShopProfitCalculator.class)
        );
        Assert.assertEquals(Optional.empty(), DSAP2.getCalculationStrategyFromFlag("wat"));
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

            DSAP2.main(new String[] { flag, path });
            Assert.assertEquals(pathAndAnswer.getValue(), outContent.toString());
            Assert.assertEquals("", errContent.toString());
        }
    }

    @Test
    public void testDSAP2Recursive() throws URISyntaxException {
        Map<String, String> pathToAnswerMap = new HashMap<>();

        pathToAnswerMap.put("/P2eg1.txt", "80");
        pathToAnswerMap.put("/P2eg2.txt", "24");
        pathToAnswerMap.put("/P2eg3.txt", "62");
        pathToAnswerMap.put("/P2eg4.txt", "31");

        testWithFlag(pathToAnswerMap, "-r");
        testWithFlag(pathToAnswerMap, "--recursive");
    }

    @Test
    public void testDSAP2Memoized() throws URISyntaxException {
        Map<String, String> pathToAnswerMap = new HashMap<>();

        pathToAnswerMap.put("/P2eg1.txt", "80");
        pathToAnswerMap.put("/P2eg2.txt", "24");
        pathToAnswerMap.put("/P2eg3.txt", "62");
        pathToAnswerMap.put("/P2eg4.txt", "31");
        pathToAnswerMap.put("/P2eg5.txt", "87");
        pathToAnswerMap.put("/P2eg6.txt", "209");
        pathToAnswerMap.put("/P2eg7.txt", "970");
        pathToAnswerMap.put("/P2eg8.txt", "1207");
        pathToAnswerMap.put("/P2eg9.txt", "24298");
        pathToAnswerMap.put("/P2eg10.txt", "78987");


        testWithFlag(pathToAnswerMap, "-m");
        testWithFlag(pathToAnswerMap, "--memoized");
    }

    @Test
    public void testDSAP2Iterative() throws URISyntaxException {
        Map<String, String> pathToAnswerMap = new HashMap<>();

        pathToAnswerMap.put("/P2eg1.txt", "0");
        pathToAnswerMap.put("/P2eg2.txt", "0");
        pathToAnswerMap.put("/P2eg3.txt", "0");
        pathToAnswerMap.put("/P2eg4.txt", "0");
        pathToAnswerMap.put("/P2eg5.txt", "0");
        pathToAnswerMap.put("/P2eg6.txt", "0");
        pathToAnswerMap.put("/P2eg7.txt", "0");
        pathToAnswerMap.put("/P2eg8.txt", "0");
        pathToAnswerMap.put("/P2eg9.txt", "0");
        pathToAnswerMap.put("/P2eg10.txt", "0");


        testWithFlag(pathToAnswerMap, "-i");
        testWithFlag(pathToAnswerMap, "--iterative");
    }
}