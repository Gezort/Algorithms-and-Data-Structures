import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Мирон on 17.12.2014 PACKAGE_NAME.
 */
public class T2DSingleTemplateMatcher {

    private ArrayList<String> A;
    private int NA = 0;
    private int MA = 0;

    public void addTemplate(ArrayList<String> template) throws TNotSupportedException {
        A = new ArrayList<>();
        for (int i = 0; i < template.size(); i++) {
            A.add(template.get(i));
            if (i > 0 && template.get(i).length() != template.get(i - 1).length()) {
                throw new TNotSupportedException("Template is not a matrix");
            }
        }
        NA = A.size();
        MA = A.get(0).length();
        return;
    }

    public ArrayList<Pair<Integer, Integer>> Match(ArrayList<String> B) throws TNotSupportedException {
        if (NA == 0) {
            throw new TNotSupportedException("Matching stream without template isn't supported");
        }
        for (int i = 1; i < B.size(); i++) {
            if (B.get(i).length() != B.get(i - 1).length()) {
                throw new TNotSupportedException("Stream is not a matrix");
            }
        }
        int NB = B.size();
        int MB = B.get(0).length();
        if (NB > NA || MB > MA) {
            return new ArrayList<>();
        }

        TSingleTemplateMatcher matcher = new TSingleTemplateMatcher();
        ArrayList<Pair<Integer,Integer>>[] coincidences = new ArrayList[NB];
        for (int i = 0; i < NB; i++) {
            coincidences[i] = new ArrayList<>();
            matcher.addTemplate(B.get(i));
            int len = B.get(i).length();
            for (int j = 0; j < NA; j++) {
                ArrayList<Pair<Integer, Integer>> res = matcher.MatchStream(new StringStream(A.get(j)));
                for (Pair<Integer, Integer> p : res) {
                    coincidences[i].add(new Pair<>(j,p.getKey() - len + 1));
                }
            }
        }
        int[][] counter = new int[NA][MA];
        for (int i = 0; i < NA; i++) {
            for (int j = 0; j < MA; j++) {
                counter[i][j] = 0;
            }
        }
        for (int i = 0; i < NA; i++) {
            ArrayList<Pair<Integer, Integer>> res = matcher.MatchStream(new StringStream(A.get(i)));
            for (Pair<Integer, Integer> p : res) {
                int j = p.getKey() - B.get(p.getValue()).length() + 1;
                if (i < p.getValue()) {
                    continue;
                }
                counter[i - p.getValue()][j]++;
            }
        }
        ArrayList<Pair<Integer, Integer>> result = new ArrayList<>();
        for (int i = 0; i < NA; i++) {
            for (int j = 0; j < MA; j++) {
                if (counter[i][j] == NB) {
                    result.add(new Pair<>(i, j));
                }
            }
        }
        return result;
    }

}
