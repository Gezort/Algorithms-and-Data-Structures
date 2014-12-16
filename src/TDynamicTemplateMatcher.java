import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Мирон on 17.11.2014 PACKAGE_NAME.
 */
public class TDynamicTemplateMatcher implements IMetaTemplateMatcher {

    private ArrayList<TStaticTemplateMatcher> matchers = new ArrayList<>();
    private ArrayList<Integer> size = new ArrayList<>();
    private int templateN = 0;

    private TStaticTemplateMatcher mergeMatchers(TStaticTemplateMatcher a, TStaticTemplateMatcher b) throws TNotSupportedException{
        ArrayList<Pair<String, Integer>> aTemplates = a.getTemplates();
        ArrayList<Pair<String, Integer>> bTemplates = b.getTemplates();
        int aTemplatesCount = a.getTemplatesCount();
        int bTemplatesCount = a.getTemplatesCount();
        TStaticTemplateMatcher result = new TStaticTemplateMatcher();
        for (int i = 0; i < aTemplatesCount; i++) {
            result.setTemplateNumber(aTemplates.get(i).getValue());
            result.addTemplate(aTemplates.get(i).getKey());
        }
        for (int i = 0; i < bTemplatesCount; i++) {
            result.setTemplateNumber(bTemplates.get(i).getValue());
            result.addTemplate(bTemplates.get(i).getKey());
        }
        return result;
    }

    @Override
    public int addTemplate(String template) throws TNotSupportedException {
        TStaticTemplateMatcher matcher = new TStaticTemplateMatcher();
        matcher.setTemplateNumber(templateN);
        matcher.addTemplate(template);
        if (size.get(size.size() - 1) == 0) {
            size.add(0);
            matchers.add(null);
        }
        for (int i = 0; i < size.size(); i++) {
            if (size.get(i) > 0) {
                matcher = mergeMatchers(matcher, matchers.get(i));
                size.set(i, 0);
                matchers.set(i, null);
            } else {
                size.set(i, matcher.getTemplatesCount());
                matchers.set(i, matcher);
                break;
            }
        }
        return templateN++;
    }

    @Override
    public ArrayList<Pair<Integer, Integer>> MatchStream(ICharStream stream) {
        StringBuilder builder = new StringBuilder();
        while (!stream.isEmpty()) {
            builder.append(stream.getChar());
        }
        String s = builder.toString();
        ArrayList<Pair<Integer, Integer>> result = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            if (size.get(i) > 0) {
                result.addAll(matchers.get(i).MatchStream(new StringStream(s)));
            }
        }
        return result;
    }
}
