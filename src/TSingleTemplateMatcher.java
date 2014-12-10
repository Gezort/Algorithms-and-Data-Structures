import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Мирон on 10.10.2014 PACKAGE_NAME.
 */
public class TSingleTemplateMatcher implements IMetaTemplateMatcher {
    private String template;
    private int N;
    private int MAXN = 1000000;
    private int[] pi;
    private int piPos = 0;
    private int id = 0;
    private int numberOfOperations;

    int getNumberOfOperations() {
        return numberOfOperations;
    }

    TSingleTemplateMatcher() {
        N = 0;
        pi = new int[MAXN];
        template = null;
    }

    private int getPi(int pos) {
        numberOfOperations++;
        if (pos <= piPos) {
            return pi[pos];
        }
        for (int i = piPos + 1; i <= pos; i++) {
            numberOfOperations++;
            int k = pi[i - 1];
            while (k > 0 && template.charAt(i) != template.charAt(k)) {
                numberOfOperations++;
                k = pi[k - 1];
            }
            pi[i] = (template.charAt(i) == template.charAt(k) ? k + 1 : 0);
        }
        piPos = pos;
        return pi[pos];
    }

    @Override
    public int addTemplate(String tmp) throws TNotSupportedException {
        N = tmp.length();
        template = tmp;
        pi[0] = 0;
        piPos = 0;
        return id++;
    }

    @Override
    public ArrayList<Pair<Integer, Integer>> MatchStream(ICharStream stream) throws TNotSupportedException {
        if (template == null) {
            throw new TNotSupportedException("Stream matching without template isn't supported in TSingleTemplateMatcher");
        }
        ArrayList<Pair<Integer, Integer> > result = new ArrayList<Pair<Integer, Integer> >();
        int previousPi = 0;
        int pos = 0;
        template += "$";
        while (!stream.isEmpty()) {
            char nextChar = stream.getChar();
            int currentPi = previousPi;
            numberOfOperations++;
            while (currentPi > 0 && template.charAt(currentPi) != nextChar) {
                numberOfOperations++;
                currentPi = getPi(currentPi - 1);
            }
            previousPi = (template.charAt(currentPi) == nextChar ? currentPi + 1 : 0);
            if (previousPi == N) {
                result.add(new Pair<Integer, Integer> (pos, id - 1));
            }
            pos++;
        }
        template = template.substring(0,template.length() - 1);
        return result;
    }

    public void appendCharToTemplate(char c) throws TNotSupportedException {
        if (template == null) {
            throw new TNotSupportedException("Character appending without template isn't supported in TSingleTemplateMatcher");
        }
        template += c;
        N++;
    }

    public void prependCharToTemplate(char c) throws TNotSupportedException {
        if (template == null) {
            throw new TNotSupportedException("Character prepending without template isn't supported in TSingleTemplateMatcher");
        }
        template = c + template;
        N++;
        pi[0] = 0;
        piPos = 0;
    }
}
