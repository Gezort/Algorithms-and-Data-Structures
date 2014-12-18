import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Мирон on 05.12.2014 PACKAGE_NAME.
 */
public class TWildcardSingleTemplateMatcher implements IMetaTemplateMatcher {

    private ArrayList<ArrayList<Integer>> coincidences;
    private ArrayList<Integer> coincN;
    private ArrayList<Integer> tmpLength;
    private int templateSummaryLength = 0;
    private int streamLength = 0;
    private int wildcardN = 0;
    private TStaticTemplateMatcher matcher;
    private boolean created;

    private int numberOfOperations;

    @Override
    public int addTemplate(String template) throws TNotSupportedException {
        numberOfOperations = 0;
        created = true;
        wildcardN = 0;
        coincidences = new ArrayList<>();
        tmpLength = new ArrayList<>();
        coincN = new ArrayList<>();
        matcher = new TStaticTemplateMatcher();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < template.length(); i++) {
            numberOfOperations++;
            if (template.charAt(i) == '?') {
                tmpLength.add(builder.length());
                matcher.addTemplate(builder.toString());
                coincidences.add(new ArrayList<>());
                builder = new StringBuilder("");
                coincN.add(0);
            } else {
                builder.append(template.charAt(i));
            }
        }
        tmpLength.add(builder.length());
        wildcardN = tmpLength.size();
        matcher.addTemplate(builder.toString());
        coincidences.add(new ArrayList<>());
        coincN.add(0);
        templateSummaryLength = template.length();
        return 0;
    }

    @Override
    public ArrayList<Pair<Integer, Integer>> MatchStream(ICharStream stream) throws TNotSupportedException {
        if (!created) {
            throw new TNotSupportedException("Stream matching without template isn't supported");
        }
        if (templateSummaryLength == 0) {
            return new ArrayList<>();
        }
        int[] index = new int[wildcardN];
        ArrayList<Pair<Integer, Integer>> res = matcher.MatchStream(stream);
        numberOfOperations += matcher.getNumberOfMatchOperations();
        streamLength = stream.streamSize();
        for (Pair<Integer, Integer> pair : res) {
            numberOfOperations++;
            int tmpNum = pair.getValue();
            if (tmpLength.get(tmpNum) == 0) {
                continue;
            }
            coincidences.get(tmpNum).add(pair.getKey());
            coincN.set(tmpNum, coincN.get(tmpNum) + 1);
        }
        for (int i = 0; i < wildcardN; i++) {
            if (tmpLength.get(i) == 0) {
                coincN.set(i, streamLength);
                for (int j = 0; j < coincN.get(i); j++) {
                    numberOfOperations++;
                    coincidences.get(i).add((i == 0 ? j - 1 : j));
                }
                numberOfOperations++;
            }
        }
        for (int i = 0; i < wildcardN; i++) {
            index[i] = 0;
            numberOfOperations++;
        }
        res = new ArrayList<>();
        for (int i = 0; i < coincN.get(0); i++) {
            numberOfOperations++;
            index[0] = i;
            boolean flag = true;
            for (int j = 1; j < wildcardN; j++) {
                numberOfOperations++;
                int previousEnd = coincidences.get(j - 1).get(index[j - 1]) + 1;
                int curInd = index[j];
                while (curInd < coincN.get(j) && coincidences.get(j).get(curInd) < previousEnd + tmpLength.get(j)) {
                    curInd++;
                    index[j]++;
                    numberOfOperations++;
                }
                if ((curInd >= coincN.get(j)) || (coincidences.get(j).get(curInd) != (previousEnd + tmpLength.get(j)))) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                res.add(new Pair<>(coincidences.get(0).get(i) + templateSummaryLength - tmpLength.get(0), 0));
            }
        }
        return res;
    }

    public int getNumberOfOperations() {
        return numberOfOperations;
    }
}
