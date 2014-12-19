import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Мирон on 07.11.2014 PACKAGE_NAME.
 */
public class TestStaticTemplateMatcher {

    private ICharStream streamSingle;
    private ICharStream streamStatic;

    private TStaticTemplateMatcher staticTemplateMatcher = new TStaticTemplateMatcher();
    private TSingleTemplateMatcher singleTemplateMatcher = new TSingleTemplateMatcher();

    Comparator pairComparator = new Comparator<Pair<Integer, Integer>>() {
        @Override
        public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
            if (o1.getKey() < o2.getKey() || (o1.getKey() == o2.getKey() && o1.getValue() < o2.getValue())) {
                return -1;
            }
            if (o1.getKey() > o2.getKey() || (o1.getKey() == o2.getKey() && o1.getValue() > o2.getValue())) {
                return 1;
            }
            return 0;
        }
    };

    private void TestEquality(String[] template, String stream) throws TNotSupportedException {
        singleTemplateMatcher = new TSingleTemplateMatcher();
        staticTemplateMatcher = new TStaticTemplateMatcher();
        ArrayList<Pair<Integer, Integer>> singleResult = new ArrayList<>();

        for (int i = 0; i < template.length; i++) {
            singleTemplateMatcher.addTemplate(template[i]);
            streamSingle = new StringStream(stream);
            singleResult.addAll(singleTemplateMatcher.MatchStream(streamSingle));
        }
        Collections.sort(singleResult, pairComparator);

        for (int i = 0; i < template.length; i++) {
            staticTemplateMatcher.addTemplate(template[i]);
        }

        streamStatic = new StringStream(stream);
        ArrayList<Pair<Integer, Integer>> staticResult = new ArrayList<>();
        staticResult = staticTemplateMatcher.MatchStream(streamStatic);
        Collections.sort(staticResult, pairComparator);

        Assert.assertEquals(singleResult.size(), staticResult.size());
        for (int i = 0; i < singleResult.size(); i++) {
            Assert.assertEquals(singleResult.get(i), staticResult.get(i));
        }
    }

    private void TestProductivity(String[] template, String stream) throws TNotSupportedException {
        staticTemplateMatcher = new TStaticTemplateMatcher();
        int templatesSize = 0;
        for (int i = 0; i < template.length; i++) {
            staticTemplateMatcher.addTemplate(template[i]);
            templatesSize += template[i].length();
        }
        streamStatic = new StringStream(stream);
        int answerSize = staticTemplateMatcher.MatchStream(streamStatic).size();
        Assert.assertTrue(staticTemplateMatcher.getNumberOfBuildOperations() + staticTemplateMatcher.getNumberOfMatchOperations() <
                2 * (stream.length() + answerSize + templatesSize * 26));
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

        for (int i = 0; i < 100; i++) {
            TestProductivity(s, new RandomStream(25, 100000).getString());
        }
        s = new String[100];
        String stream = new RandomStream(20, 100000).getString();
        for (int i = 0; i < 50; i++) {
            s[i] = stream.substring((i % 2) * 100, i * 100);
            s[i + 50] = s[i] + (i % 2 == 1 ? "x" : "a");
        }
        TestProductivity(s, stream);
    }

    @Test(expected = TNotSupportedException.class)
    public void addExceptionTest() throws TNotSupportedException {
        staticTemplateMatcher = new TStaticTemplateMatcher();
        staticTemplateMatcher.addTemplate("a");
        staticTemplateMatcher.MatchStream(new StringStream("aba"));
        staticTemplateMatcher.addTemplate("a");
    }


}
