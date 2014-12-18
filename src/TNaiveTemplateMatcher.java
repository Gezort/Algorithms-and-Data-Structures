import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Мирон on 10.10.2014 PACKAGE_NAME.
 */
public class TNaiveTemplateMatcher implements IMetaTemplateMatcher {
    private ArrayList<String> templates;
    private int maxTSize;

    TNaiveTemplateMatcher() {
        maxTSize = 0;
        templates = new ArrayList<String>();
    }

    @Override
    public int addTemplate(String template) {
        templates.add(template);
        if (maxTSize < template.length()) {
            maxTSize = template.length();
        }
        return templates.size() - 1;
    }

    @Override
    public ArrayList<Pair<Integer, Integer>> MatchStream(ICharStream stream) {
        if (templates.size() == 0) {
            throw new NullPointerException("Matching while no templates aren't supported");
        }
        ArrayList<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
        String currentStr = "";
        int pos = 0;
        while (!stream.isEmpty()) {
            char nextChar = stream.getChar();
            currentStr += nextChar;
            if (currentStr.length() > maxTSize)
                currentStr = currentStr.substring(1);
            for (int i = 0; i < templates.size(); i++) {
                String t = templates.get(i);
                if (t.length() > currentStr.length()) {
                    continue;
                }
                if (t.equals(currentStr.substring(currentStr.length() - t.length()))) {
                    result.add(new Pair<Integer, Integer>(pos, i));
                }
            }
            pos++;
        }
        return result;
    }
}
