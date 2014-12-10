import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Мирон on 17.11.2014 PACKAGE_NAME.
 */
public class TDynamicTemplateMatcher implements IMetaTemplateMatcher {

    private TStaticTemplateMatcher[] matchers = new TStaticTemplateMatcher[50];
    private int size[] = new int[50];
    private int templateN = 0;

    private TStaticTemplateMatcher mergeMatchers(TStaticTemplateMatcher a, TStaticTemplateMatcher b) throws TNotSupportedException{
        Pair<String, Integer>[] aTemplates = a.getTemplates();
        Pair<String, Integer>[] bTemplates = b.getTemplates();
        int aTemplatesCount = a.getTemplatesCount();
        int bTemplatesCount = a.getTemplatesCount();
        TStaticTemplateMatcher result = new TStaticTemplateMatcher();
        for (int i = 0; i < aTemplatesCount; i++) {
            result.setTemplateNumber(aTemplates[i].getValue());
            result.addTemplate(aTemplates[i].getKey());
        }
        for (int i = 0; i < bTemplatesCount; i++) {
            result.setTemplateNumber(bTemplates[i].getValue());
            result.addTemplate(bTemplates[i].getKey());
        }
        return result;
    }

    @Override
    public int addTemplate(String template) throws TNotSupportedException {
        TStaticTemplateMatcher matcher = new TStaticTemplateMatcher();
        matcher.setTemplateNumber(templateN);
        matcher.addTemplate(template);
        for (int i = 0; i < 50; i++) {
            if (size[i] > 0) {
                matcher = mergeMatchers(matcher, matchers[i]);
                size[i] = 0;
                matchers[i] = null;
            } else {
                size[i] = matcher.getTemplatesCount();
                matchers[i] = matcher;
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
            if (size[i] > 0) {
                result.addAll(matchers[i].MatchStream(new StringStream(s)));
            }
        }
        return result;
    }
}
