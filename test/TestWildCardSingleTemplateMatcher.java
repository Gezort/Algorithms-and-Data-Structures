import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Мирон on 12.12.2014 PACKAGE_NAME.
 */
public class TestWildCardSingleTemplateMatcher {

    private ICharStream streamSingle;

    private TWildcardSingleTemplateMatcher wildcardSingleTemplateMatcher;

    private ArrayList<Pair<Integer, Integer>> naiveMatch(String template, String stream) {
        ArrayList<Pair<Integer, Integer>> res = new ArrayList<>();
        if (template.length() == 0) {
            return res;
        }
        for (int i = 0; i <= stream.length() - template.length(); i++) {
            boolean flag = true;
            for (int j = 0; j < template.length(); j++) {
                if (template.charAt(j) != '?' && stream.charAt(i + j) != template.charAt(j)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                res.add(new Pair<>(i + template.length() - 1, 0));
            }
        }
        return res;
    }

    private void TestEquality(String template, String stream) throws TNotSupportedException {
        wildcardSingleTemplateMatcher = new TWildcardSingleTemplateMatcher();
        wildcardSingleTemplateMatcher.addTemplate(template);
        streamSingle = new StringStream(stream);
        ArrayList<Pair<Integer, Integer>> singleResult = wildcardSingleTemplateMatcher.MatchStream(streamSingle);
        ArrayList<Pair<Integer, Integer>> naiveResult = naiveMatch(template, stream);
        Assert.assertEquals(naiveResult.size(), singleResult.size());
        for (int i = 0; i < naiveResult.size(); i++) {
            Assert.assertEquals(naiveResult.get(i), singleResult.get(i));
        }
    }

    private void TestProductivity(String template, String stream) throws TNotSupportedException {
        wildcardSingleTemplateMatcher = new TWildcardSingleTemplateMatcher();
        wildcardSingleTemplateMatcher.addTemplate(template);
        int k = 1;
        for (int i = 0; i < template.length(); i++) {
            if (template.charAt(i) == '?') {
                k++;
            }
        }
        streamSingle = new StringStream(stream);
        Assert.assertTrue(wildcardSingleTemplateMatcher.getNumberOfOperations() < 4 * k * stream.length());
    }

    @Test
    public void simpleTest() throws TNotSupportedException {
        TestEquality("a", "aaaa");
        TestEquality("ab", "abacaba");
        TestEquality("aba", "aba");
        TestEquality("d", "ababa");
        TestEquality("a??", "abacaba");
        TestEquality("?a", "abacaba");
        TestEquality("a??ba", "abacabadabacadaba");
    }

    @Test
    public void cleverTest() throws TNotSupportedException {
        TestEquality("aba", "a");
        TestEquality("aba", "");
        TestEquality("", "aba");
        TestEquality("", "");
        TestEquality("????", "aaa");
        TestEquality("a?", "aaaa");
        TestEquality("?a", "aaaa");
        TestEquality("?", "");
        TestEquality("?a?", "aaaaaaa");
    }

    @Test
    public void stressTest() throws TNotSupportedException {
        Random generator = new Random(System.nanoTime());
        for (int i = 0; i < 100; i++) {
            RandomStream rnd = new RandomStream(2 + i % 5, i % 10);
            StringBuilder template = new StringBuilder(rnd.getString());
            for (int j = 0; j < template.length(); j++) {
                if (generator.nextInt() % 3 == 0) {
                    template.replace(j, j, "?");
                }
            }
            RandomStream stream = new RandomStream(2 * (i % 5 + 1), 500);
            TestEquality(template.toString(), stream.getString());
        }
    }

    @Test
    public void productivityTest() throws TNotSupportedException {
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < 1000000; i++) {
            s.append('a');
        }
        TestProductivity("a", s.toString());
        s = new StringBuilder('a');
        for (char a = 'b'; a <= 'f'; a++) {
            s.append(a + s.toString());
        }
        TestProductivity("a", s.toString());
        TestProductivity("?", s.toString());
        StringBuilder template = new StringBuilder("");
        for (int i = 0; i < 100; i++) {
            if (i % 4 < 2) {
                template.append('?');
            } else {
                template.append('a');
            }
        }
        TestProductivity(template.toString(), s.toString());
        template = new StringBuilder("");
        for (int i = 0; i < 100; i++) {
            if (i % 2 != 0) {
                template.append('?');
            } else {
                template.append('a');
            }
        }
    }

    @Test(expected = TNotSupportedException.class)
    public void matchExceptionTest() throws TNotSupportedException {
        streamSingle = new StringStream("a");
        wildcardSingleTemplateMatcher = new TWildcardSingleTemplateMatcher();
        wildcardSingleTemplateMatcher.MatchStream(streamSingle);
    }

}
