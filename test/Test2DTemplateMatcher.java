import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Мирон on 18.12.2014 PACKAGE_NAME.
 */
public class Test2DTemplateMatcher {

    private T2DSingleTemplateMatcher t2DSingleTemplateMatcher;

    private void TestEquality(ArrayList<String> A, ArrayList<String> B) throws TNotSupportedException {
        t2DSingleTemplateMatcher = new T2DSingleTemplateMatcher();
        t2DSingleTemplateMatcher.addTemplate(A);
        ArrayList<Pair<Integer, Integer>> singleResult = t2DSingleTemplateMatcher.Match(B);
        ArrayList<Pair<Integer, Integer>> naiveResult = new ArrayList<>();
        for (int i = 0; i <= A.size() - B.size(); i++) {
            for (int j = 0; j <= A.get(0).length() - B.get(0).length(); j++) {
                boolean flag = true;
                cycle:
                for (int k = 0; k < B.size(); k++) {
                    for (int l = 0; l < B.get(0).length(); l++) {
                        if (B.get(k).charAt(l) != A.get(i + k).charAt(j + l)) {
                            flag = false;
                            break cycle;
                        }
                    }
                }
                if (flag) {
                    naiveResult.add(new Pair<>(i, j));
                }
            }
        }
        Assert.assertEquals(naiveResult.size(), singleResult.size());
        for (int i = 0; i < naiveResult.size(); i++) {
            Assert.assertEquals(naiveResult.get(i), singleResult.get(i));
        }
    }

    @Test
    public void simpleTest() throws TNotSupportedException {
        ArrayList<String> A = new ArrayList<>(Arrays.asList("aaaa", "aaba", "abaa", "baba"));
        ArrayList<String> B = new ArrayList<>(Arrays.asList("aa", "ba"));
        TestEquality(A, B);
        TestEquality(B, A);
        A = new ArrayList<>(Arrays.asList("aaaa", "aaaa", "aaaa", "aaaa"));
        B = new ArrayList<>(Arrays.asList("aaaa", "aaaa", "aaaa", "aaaa"));
        TestEquality(A, B);
        B = new ArrayList<>(Arrays.asList("a"));
        TestEquality(A, B);
    }

    @Test
    public void cleverTest() throws TNotSupportedException {
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < 300; i++) {
            s.append('a');
        }
        ArrayList<String> A = new ArrayList<>();
        ArrayList<String> B = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            A.add(s.toString());
        }
        s = new StringBuilder(s.substring(200));
        for (int i = 0; i < 50; i++) {
            B.add(s.toString());
        }
        TestEquality(A, B);
    }

    @Test
    public void stressTest() throws TNotSupportedException {
        Random random = new Random(System.nanoTime());
        for (int i = 0; i < 300; i++) {
            int NA = 10 + Math.abs(random.nextInt()) % 300;
            int MA = 10 + Math.abs(random.nextInt()) % 300;
            int NB = Math.max(Math.abs(random.nextInt()) % 10, 1);
            int MB = Math.max(Math.abs(random.nextInt()) % 10, 1);
            ArrayList<String> A, B;
            A = new ArrayList<>();
            B = new ArrayList<>();
            for (int j = 0; j < NA; j++) {
                A.add(new RandomStream(2 + i % 2, MA).getString());
            }
            for (int j = 0; j < NB; j++) {
                B.add(new RandomStream(2 + i % 2, MB).getString());
            }
            TestEquality(A, B);
        }
    }

    @Test(expected = TNotSupportedException.class)
    public void exceptionTest() throws TNotSupportedException {
        new T2DSingleTemplateMatcher().Match(new ArrayList<>(Arrays.asList("a")));
    }
}
