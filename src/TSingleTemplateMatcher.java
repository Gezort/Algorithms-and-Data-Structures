import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Мирон on 10.10.2014 PACKAGE_NAME.
 */
public class TSingleTemplateMatcher implements IMetaTemplateMatcher {
    private String leftTemplate;
    private String rightTemplate;
    private int rightN;
    private int leftN;
    private ArrayList<Integer> pi;
    private int piPos = 0;
    private int id = 0;
    private int numberOfOperations;

    int getNumberOfOperations() {
        return numberOfOperations;
    }

    TSingleTemplateMatcher() {
        leftN = rightN = 0;
    }

    private char getTemplateChar(int ind) {
        if (ind < leftN) {
            return leftTemplate.charAt(leftN - ind - 1);
        }
        if (ind == leftN + rightN) {
            return '$';
        }
        return rightTemplate.charAt(ind - leftN);
    }

    private int getPi(int pos) {
        numberOfOperations++;
        if (pos <= piPos) {
            return pi.get(pos);
        }
        for (int i = piPos + 1; i <= pos; i++) {
            numberOfOperations++;
            int k = pi.get(i - 1);
            while (k > 0 && getTemplateChar(i) != getTemplateChar(k)) {
                numberOfOperations++;
                k = pi.get(k - 1);
            }
            pi.add((getTemplateChar(i) == getTemplateChar(k) ? k + 1 : 0));
        }
        piPos = pos;
        return pi.get(pos);
    }

    @Override
    public int addTemplate(String tmp) throws TNotSupportedException {
        numberOfOperations = 0;
        rightN = tmp.length();
        leftTemplate = "";
        rightTemplate = tmp;
        pi = new ArrayList<>();
        pi.add(0);
        piPos = 0;
        return id++;
    }

    @Override
    public ArrayList<Pair<Integer, Integer>> MatchStream(ICharStream stream) throws TNotSupportedException {
        if (rightTemplate == null) {
            throw new TNotSupportedException("Stream matching without template isn't supported in TSingleTemplateMatcher");
        }
        ArrayList<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
        int previousPi = 0;
        int pos = 0;
        while (!stream.isEmpty()) {
            char nextChar = stream.getChar();
            int currentPi = previousPi;
            numberOfOperations++;
            while (currentPi > 0 && getTemplateChar(currentPi) != nextChar) {
                numberOfOperations++;
                currentPi = getPi(currentPi - 1);
            }
            previousPi = (getTemplateChar(currentPi) == nextChar ? currentPi + 1 : 0);
            if (previousPi == leftN + rightN) {
                result.add(new Pair<Integer, Integer>(pos, id - 1));
            }
            pos++;
        }
        return result;
    }

    public void appendCharToTemplate(char c) throws TNotSupportedException {
        if (rightTemplate == null) {
            throw new TNotSupportedException("Character appending without template isn't supported in TSingleTemplateMatcher");
        }
        numberOfOperations++;
        rightTemplate += c;
        rightN++;
    }

    public void prependCharToTemplate(char c) throws TNotSupportedException {
        if (leftTemplate == null) {
            throw new TNotSupportedException("Character prepending without template isn't supported in TSingleTemplateMatcher");
        }
        numberOfOperations++;
        leftTemplate += c;
        leftN++;
        pi = new ArrayList<>();
        pi.add(0);
        piPos = 0;
    }
}
