import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Мирон on 10.10.2014 PACKAGE_NAME.
 */
public class TestSingleTemplateMatcher {

    private ICharStream streamNaive;
    private ICharStream streamSingle;

    private TSingleTemplateMatcher singleTemplateMatcher = new TSingleTemplateMatcher();
    private TNaiveTemplateMatcher naiveTemplateMatcher = new TNaiveTemplateMatcher();

    private void TestEquality(String template, String stream) throws TNotSupportedException {
        singleTemplateMatcher = new TSingleTemplateMatcher();
        naiveTemplateMatcher = new TNaiveTemplateMatcher();
        naiveTemplateMatcher.addTemplate(template);
        singleTemplateMatcher.addTemplate(template);
        streamNaive = new StringStream(stream);
        streamSingle = new StringStream(stream);
        ArrayList<Pair<Integer, Integer> > naiveResult = naiveTemplateMatcher.MatchStream(streamNaive);
        ArrayList<Pair<Integer, Integer> > singleResult = singleTemplateMatcher.MatchStream(streamSingle);
        Assert.assertEquals(naiveResult.size(), singleResult.size());
        for (int i = 0; i < naiveResult.size(); i++) {
            Assert.assertEquals(naiveResult.get(i), singleResult.get(i));
        }
    }

    private void TestProductivity(String template, String stream) throws TNotSupportedException {
        singleTemplateMatcher = new TSingleTemplateMatcher();
        singleTemplateMatcher.addTemplate(template);
        streamSingle = new StringStream(stream);
        singleTemplateMatcher.MatchStream(streamSingle);
        Assert.assertTrue(singleTemplateMatcher.getNumberOfOperations() < 4*stream.length());
    }

    @Test
    public void simpleTest() throws TNotSupportedException{
        TestEquality("a", "aaaa");
        TestEquality("ab", "abacaba");
        TestEquality("aba", "abacabadabacaba");
        TestEquality("aba", "aba");
        TestEquality("d", "ababa");
    }

    @Test
    public void cleverTest() throws TNotSupportedException{
        TestEquality("aba", "a");
        TestEquality("aba", "");
        TestEquality("", "aba");
        TestEquality("", "");
    }

    @Test
    public void stressTest() throws TNotSupportedException {
        for (int i = 0; i < 100; i++) {
            RandomStream template = new RandomStream(2 + i % 5,i % 10);
            RandomStream stream = new RandomStream(2 * (i % 5  + 1),10000);
            TestEquality(template.getString(), stream.getString());
        }
    }

    @Test
    public void productivityTest() throws TNotSupportedException {
        String s = "";
        for (int i = 0; i < 100000; i++) {
            s += "a";
        }
        TestProductivity("a",s);
        s = "a";
        for (char a = 'b'; a <= 'f'; a++) {
            s += a + s;
        }
        TestProductivity("a",s);
    }

    @Test (expected = TNotSupportedException.class)
    public void prependExceptionTest() throws TNotSupportedException {
        singleTemplateMatcher.prependCharToTemplate('a');
    }

    @Test (expected = TNotSupportedException.class)
    public void appendExceptionTest() throws TNotSupportedException {
        singleTemplateMatcher.appendCharToTemplate('b');
    }

    @Test (expected = TNotSupportedException.class)
    public void matchExceptionTest() throws TNotSupportedException {
        streamSingle = new StringStream("a");
        singleTemplateMatcher.MatchStream(streamSingle);
    }

}
