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

public class DSAP3Test {
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
        Assert.assertEquals("r", DSAP3.stripHyphens("-r"));
        Assert.assertEquals("i-", DSAP3.stripHyphens("-i-"));
        Assert.assertEquals("recursive", DSAP3.stripHyphens("-recursive"));
        Assert.assertEquals("recursive", DSAP3.stripHyphens("--recursive"));
        Assert.assertEquals("memo-ized", DSAP3.stripHyphens("--memo-ized"));
    }

    @Test
    public void testGetCalculationStrategyFromFlag() {
        Assert.assertThat(
                DSAP3.getCalculationStrategyFromFlag("r").get(),
                new IsInstanceOf(RecursiveHighestTeamAbilityCalculator.class)
        );
        Assert.assertEquals(Optional.empty(), DSAP3.getCalculationStrategyFromFlag("wat"));
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

            DSAP3.main(new String[] { flag, path });
            Assert.assertEquals(pathAndAnswer.getValue(), outContent.toString());
            Assert.assertEquals("", errContent.toString());
        }
    }

    @Test
    public void testDSAP3Recursive() throws URISyntaxException {
        Map<String, String> pathToAnswerMap = new HashMap<>();

        pathToAnswerMap.put("/P3eg1.txt", "188");
        pathToAnswerMap.put("/P3eg2.txt", "42");
        pathToAnswerMap.put("/P3eg3.txt", "77");
        pathToAnswerMap.put("/P3eg4.txt", "46");
        pathToAnswerMap.put("/P3eg5.txt", "181");
        pathToAnswerMap.put("/P3eg6.txt", "712");
        pathToAnswerMap.put("/P3eg7.txt", "1054");
        pathToAnswerMap.put("/P3eg8.txt", "230");
        pathToAnswerMap.put("/P3eg9.txt", "4791096");
        pathToAnswerMap.put("/P3eg10.txt", "31762");

        testWithFlag(pathToAnswerMap, "-r");
        testWithFlag(pathToAnswerMap, "--recursive");
    }
}