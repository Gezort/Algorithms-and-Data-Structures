import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Мирон on 10.10.2014 PACKAGE_NAME.
 */
public class Main {
    public static void main(String[] args) throws TNotSupportedException {
        T2DSingleTemplateMatcher t2DSingleTemplateMatcher = new T2DSingleTemplateMatcher();
        ArrayList<String> A = new ArrayList<>();
        A.add("aaa");
        A.add("baa");
        A.add("bba");
        t2DSingleTemplateMatcher.addTemplate(A);
        ArrayList<String> B = new ArrayList<>();
        B.add("aa");
        B.add("ba");
        for (Pair<Integer, Integer> p : t2DSingleTemplateMatcher.Match(B)) {
            System.out.println(p.getKey() + " " + p.getValue());
        }
    }
}
