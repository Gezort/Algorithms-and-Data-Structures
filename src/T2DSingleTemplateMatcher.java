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

    public ArrayList<Pair<Integer, Integer>> MatchStream(ArrayList<String> B) throws TNotSupportedException {
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
        ArrayList<Pair<Integer,Integer>>[] coincidences = new ArrayList[NB];
        TSingleTemplateMatcher matcher = new TSingleTemplateMatcher();
        if (NB > NA || MB > MA) {
            return new ArrayList<>();
        }
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
        ArrayList<Pair<Integer,Integer>> result = new ArrayList<>();
        int[] index = new int[NB];
        for (int i = 0; i < NB; i++) {
            index[i] = 0;
        }
        for (int i = 0; i < coincidences[0].size(); i++) {
            index[0] = i;
            boolean flag = true;
            for (int j = 1; j < NB; j++) {
                int curInd = index[j];
                while (curInd < coincidences[j].size() &&
                        coincidences[j].get(curInd).getKey() < coincidences[j - 1].get(index[j - 1]).getKey() + j &&
                        coincidences[j].get(curInd).getValue() < coincidences[j - 1].get(index[j - 1]).getValue()) {
                    curInd++;
                    index[j]++;
                }
                if (curInd >= coincidences[j].size() ||
                        coincidences[j].get(curInd).getKey() != coincidences[j - 1].get(index[j - 1]).getKey() + j ||
                        coincidences[j].get(curInd).getValue() != coincidences[j - 1].get(index[j - 1]).getValue()) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result.add(coincidences[0].get(i));
            }
        }
        return result;
    }

}
