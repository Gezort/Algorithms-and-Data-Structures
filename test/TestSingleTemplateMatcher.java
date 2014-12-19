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
        ArrayList<Pair<Integer, Integer>> naiveResult = naiveTemplateMatcher.MatchStream(streamNaive);
        ArrayList<Pair<Integer, Integer>> singleResult = singleTemplateMatcher.MatchStream(streamSingle);
        Assert.assertEquals(naiveResult.size(), singleResult.size());
        for (int i = 0; i < naiveResult.size(); i++) {
            Assert.assertEquals(naiveResult.get(i), singleResult.get(i));
        }
    }

    private void TestProductivity(String template, String stream) throws TNotSupportedException {
        singleTemplateMatcher = new TSingleTemplateMatcher();
        int mid = template.length() / 2;
        String tmp = "" + template.charAt(mid);
        singleTemplateMatcher.addTemplate(tmp);
        for (int i = mid - 1; i >= 0; i--) {
            singleTemplateMatcher.prependCharToTemplate(template.charAt(i));
        }
        for (int i = mid + 1; i < template.length(); i++) {
            singleTemplateMatcher.appendCharToTemplate(template.charAt(i));
        }
        streamSingle = new StringStream(stream);
        singleTemplateMatcher.MatchStream(streamSingle);
        Assert.assertTrue(singleTemplateMatcher.getNumberOfOperations() < 4 * stream.length());
    }

    @Test
    public void simpleTest() throws TNotSupportedException {
        TestEquality("a", "aaaa");
        TestEquality("ab", "abacaba");
        TestEquality("aba", "abacabadabacaba");
        TestEquality("aba", "aba");
        TestEquality("d", "ababa");
    }

    @Test
    public void cleverTest() throws TNotSupportedException {
        TestEquality("aba", "a");
        TestEquality("aba", "");
        TestEquality("", "aba");
        TestEquality("", "");
    }

    @Test
    public void stressTest() throws TNotSupportedException {
        for (int i = 0; i < 100; i++) {
            RandomStream template = new RandomStream(2 + i % 5, i % 10);
            RandomStream stream = new RandomStream(2 * (i % 5 + 1), 10000);
            TestEquality(template.getString(), stream.getString());
        }
    }

    @Test
    public void productivityTest() throws TNotSupportedException {
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < 100000; i++) {
            s.append('a');
        }
        TestProductivity("a", s.toString());

        TestProductivity(s.substring(50000), s.toString());

        s = new StringBuilder('a');
        for (char a = 'b'; a <= 'f'; a++) {
            s.append(a + s.toString());
        }
        TestProductivity("a", s.toString());

        TestProductivity(s.toString(), s.toString());

        s = new StringBuilder(new RandomStream(5, 100000).getString());
        TestProductivity(s.toString(), s.toString());

        s = new StringBuilder(new RandomStream(5, 100).getString());
        String template = s.toString();
        for (int i = 0; i < 8; i++) {
            s.append(s.toString());
        }
        TestProductivity(template, s.toString());
    }

    @Test(expected = TNotSupportedException.class)
    public void prependExceptionTest() throws TNotSupportedException {
        singleTemplateMatcher.prependCharToTemplate('a');
    }

    @Test(expected = TNotSupportedException.class)
    public void appendExceptionTest() throws TNotSupportedException {
        singleTemplateMatcher.appendCharToTemplate('b');
    }

    @Test(expected = TNotSupportedException.class)
    public void matchExceptionTest() throws TNotSupportedException {
        streamSingle = new StringStream("a");
        singleTemplateMatcher.MatchStream(streamSingle);
    }

}
