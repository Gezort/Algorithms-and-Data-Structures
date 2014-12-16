import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Мирон on 12.12.2014 PACKAGE_NAME.
 */
public class TestWildCardSingleTemplateMatcher {

    private ICharStream streamSingle;

    private TWildcardSingleTemplateMatcher wildcardSingleTemplateMatcher;

    private ArrayList<Pair<Integer, Integer>> naiveMatch (String template, String stream) {
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
        ArrayList<Pair<Integer, Integer> > singleResult = wildcardSingleTemplateMatcher.MatchStream(streamSingle);
        ArrayList<Pair<Integer, Integer> > naiveResult = naiveMatch(template, stream);
        Assert.assertEquals(naiveResult.size(), singleResult.size());
        for (int i = 0; i < naiveResult.size(); i++) {
            Assert.assertEquals(naiveResult.get(i), singleResult.get(i));
        }
    }

//    private void TestProductivity(String template, String stream) throws TNotSupportedException {
//        singleTemplateMatcher = new TSingleTemplateMatcher();
//        int mid = template.length() / 2;
//        String tmp = "" + template.charAt(mid);
//        singleTemplateMatcher.addTemplate(tmp);
//        for (int i = mid - 1; i >= 0; i--) {
//            singleTemplateMatcher.prependCharToTemplate(template.charAt(i));
//        }
//        for (int i = mid + 1; i < template.length(); i++) {
//            singleTemplateMatcher.appendCharToTemplate(template.charAt(i));
//        }
//        streamSingle = new StringStream(stream);
//        singleTemplateMatcher.MatchStream(streamSingle);
//        Assert.assertTrue(singleTemplateMatcher.getNumberOfOperations() < 4*stream.length());
//    }

    @Test
    public void simpleTest() throws TNotSupportedException{
        TestEquality("a", "aaaa");
        TestEquality("ab", "abacaba");
        TestEquality("aba", "aba");
        TestEquality("d", "ababa");
        TestEquality("a??", "abacaba");
        TestEquality("?a", "abacaba");
        TestEquality("a??ba", "abacabadabacadaba");
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
            RandomStream stream = new RandomStream(2 * (i % 5  + 1),2000);
            TestEquality(template.getString(), stream.getString());
        }
    }

//    @Test
//    public void productivityTest() throws TNotSupportedException {
//        String s = "";
//        for (int i = 0; i < 100000; i++) {
//            s += "a";
//        }
//        TestProductivity("a",s);
//        s = "a";
//        for (char a = 'b'; a <= 'f'; a++) {
//            s += a + s;
//        }
//        TestProductivity("a",s);
//    }

    @Test (expected = TNotSupportedException.class)
    public void matchExceptionTest() throws TNotSupportedException {
        streamSingle = new StringStream("a");
        wildcardSingleTemplateMatcher = new TWildcardSingleTemplateMatcher();
        wildcardSingleTemplateMatcher.MatchStream(streamSingle);
    }

}
