import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Мирон on 10.10.2014 PACKAGE_NAME.
 */
public interface IMetaTemplateMatcher {

    int addTemplate(final String template) throws TNotSupportedException;

    ArrayList<Pair<Integer, Integer>> MatchStream(ICharStream stream) throws TNotSupportedException;
}
