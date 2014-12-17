import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Мирон on 16.12.2014 PACKAGE_NAME.
 */
public class TestDynamicTemplateMatcher {

    private ICharStream streamSingle;
    private ICharStream streamDynamic;

    private TDynamicTemplateMatcher dynamicTemplateMatcher = new TDynamicTemplateMatcher();
    private TSingleTemplateMatcher singleTemplateMatcher = new TSingleTemplateMatcher();

    Comparator pairComparator = new Comparator<Pair<Integer, Integer>>() {
        @Override
        public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
            if (o1.getKey() < o2.getKey()) {
                return -1;
            }
            if (o1.getKey() > o2.getKey()) {
                return 1;
            }
            if (o1.getValue() < o2.getValue()) {
                return -1;
            }
            if (o1.getValue() > o2.getValue()) {
                return 1;
            }
            return 0;
        }
    };

    private void TestEquality(String[] template, String stream) throws TNotSupportedException {
        singleTemplateMatcher = new TSingleTemplateMatcher();
        dynamicTemplateMatcher = new TDynamicTemplateMatcher();
        ArrayList<Pair<Integer, Integer>> singleResult = new ArrayList<>();

        for (int i = 0; i < template.length; i++) {
            singleTemplateMatcher.addTemplate(template[i]);
            streamSingle = new StringStream(stream);
            singleResult.addAll(singleTemplateMatcher.MatchStream(streamSingle));
        }
        Collections.sort(singleResult, pairComparator);

        for (int i = 0; i < template.length; i++) {
            dynamicTemplateMatcher.addTemplate(template[i]);
        }

        streamDynamic = new StringStream(stream);
        ArrayList<Pair<Integer, Integer>> dynamicResult;
        dynamicResult = dynamicTemplateMatcher.MatchStream(streamDynamic);
        Collections.sort(dynamicResult, pairComparator);

        Assert.assertEquals(singleResult.size(), dynamicResult.size());
        for (int i = 0; i < singleResult.size(); i++) {
            Assert.assertEquals(singleResult.get(i), dynamicResult.get(i));
        }
    }

    private void TestProductivity(String[] template, String stream) throws TNotSupportedException {
        dynamicTemplateMatcher = new TDynamicTemplateMatcher();
        int templatesSize = 0;
        for (int i = 0; i < template.length; i++) {
            dynamicTemplateMatcher.addTemplate(template[i]);
            templatesSize += template[i].length();
        }
        streamDynamic = new StringStream(stream);
        if (template.length > 0) {
            Assert.assertTrue(dynamicTemplateMatcher.getNumberOfBuildOperations() <
                    4 * templatesSize * ((int) Math.log(template.length) + 1));
            Assert.assertTrue(dynamicTemplateMatcher.getNumberOfMatchOperations() <
                    4 * stream.length() * ((int) Math.log(template.length) + 1));
        }
    }

    @Test
    public void simpleTest() throws TNotSupportedException {
        TestEquality(new String[]{"a", "ab", "ca", "aa"}, "aaaa");
        TestEquality(new String[]{"ab", "ab"}, "abacaba");
        TestEquality(new String[]{"aba"}, "abacabadabacaba");
        TestEquality(new String[]{"aba", "aca", "ada", "aaa"}, "abaacaadaaaa");
        TestEquality(new String[]{"d", "a", "b"}, "ababa");
    }

    @Test
    public void cleverTest() throws TNotSupportedException {
        TestEquality(new String[]{"aba", "aa"}, "a");
        TestEquality(new String[]{"aba", "a"}, "");
        TestEquality(new String[]{"", ""}, "aba");
        TestEquality(new String[]{"", ""}, "");
    }

    @Test
    public void stressTest() throws TNotSupportedException {
        for (int i = 0; i < 100; i++) {
            RandomStream stream = new RandomStream(2 * (i % 5 + 1), 10000);
            String[] temps = new String[i % 10];
            for (int k = 0; k < i % 10; k++) {
                RandomStream template = new RandomStream(2 + i % 5, i % 10);
                temps[k] = template.getString();
            }
            TestEquality(temps, stream.getString());
        }
    }

    @Test
    public void productivityTest() throws TNotSupportedException {
        String[] s = new String[32];
        StringBuilder builder;
        for (int i = 0; i < (1 << 5); i++) {
            builder = new StringBuilder("");
            for (int j = 0; j < 5; j++) {
                if ((i & (1 << j)) == 0) {
                    builder.append('a');
                } else {
                    builder.append('b');
                }
            }
            s[i] = builder.toString();
        }
        builder = new StringBuilder("");
        for (int i = 0; i < 10000; i++) {
            builder.append(s[i % 32]);
        }
        TestProductivity(s, builder.toString());

        s = new String[1000];
        builder = new StringBuilder("a");
        for (int i = 0; i < 1000; i++) {
            s[i] = builder.toString();
            builder.append('a');
        }
        for (int i = 0; i < 2; i++) {
            builder.append(builder.toString());
        }
        TestProductivity(s, builder.toString());
        builder = new StringBuilder("a");
        String temp;
        for (char a = 'b'; a <= 'f'; a++) {
            s[a - 'a' - 1] = builder.toString();
            temp = builder.toString();
            builder.append(a);
            builder.append(temp);
        }
        TestProductivity(s, builder.toString());
    }
}
