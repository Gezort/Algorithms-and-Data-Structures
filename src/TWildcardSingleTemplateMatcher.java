import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Мирон on 05.12.2014 PACKAGE_NAME.
 */
public class TWildcardSingleTemplateMatcher implements IMetaTemplateMatcher {

    private int MAXK = 5000;
    private int MAXN = 5000;
    private int[][] coincidences;
    private int[] coincN;
    private int[] tmpLength;
    private int wildcardN = 0;
    private TStaticTemplateMatcher matcher;
    private boolean created;

    public TWildcardSingleTemplateMatcher() {
        coincidences = new int[MAXK][];
        tmpLength = new int[MAXK];
        coincN = new int[MAXK];
    }

    @Override
    public int addTemplate(String template) throws TNotSupportedException {
        created = true;
        for (int i = 0; i < wildcardN; i++) {
            coincidences[i] = null;
        }
        wildcardN = 0;
        matcher = new TStaticTemplateMatcher();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < template.length(); i++) {
            if (template.charAt(i) == '?') {
                tmpLength[wildcardN] = builder.length();
                matcher.addTemplate(builder.toString());
                coincidences[wildcardN] = new int[MAXN];
                builder = new StringBuilder("");
                coincN[wildcardN++] = 0;
            } else {
                builder.append(template.charAt(i));
            }
        }
        if (builder.length() > 0) {
            tmpLength[wildcardN] = builder.length();
            matcher.addTemplate(builder.toString());
            coincidences[wildcardN] = new int[MAXN];
            coincN[wildcardN++] = 0;
        }
        return 0;
    }

    @Override
    public ArrayList<Pair<Integer, Integer>> MatchStream(ICharStream stream) throws TNotSupportedException {
        if (!created) {
            throw new TNotSupportedException("Stream matching without template isn't supported");
        }
        int[] index = new int[MAXK];
        ArrayList<Pair<Integer, Integer>> res = matcher.MatchStream(stream);
        for (Pair<Integer, Integer> pair : res) {
            int tmpNum = pair.getValue();
            coincidences[tmpNum][coincN[tmpNum]++] = pair.getKey();
        }
        for (int i = 0; i < wildcardN; i++) {
            index[i] = 0;
            Arrays.sort(coincidences[i], 0, coincN[i]);
        }
        res = new ArrayList<>();
        for (int i = 0; i < coincN[0]; i++) {
            index[0] = i;
            boolean flag = true;
            for (int j = 1; j < wildcardN; j++) {
                int previousEnd = coincidences[j - 1][index[j - 1]] + 1;
                int curInd = index[j];
                while (curInd < coincN[j] && coincidences[j][curInd] < previousEnd + 1) {
                    curInd++;
                }
                if (curInd > coincN[j] || coincidences[j][curInd] != previousEnd + 1) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                res.add(new Pair<>(coincidences[0][i], 0));
            }
        }
        return res;
    }

}
