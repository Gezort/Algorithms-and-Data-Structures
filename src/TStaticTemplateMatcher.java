import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Мирон on 24.10.2014 PACKAGE_NAME.
 */
public class TStaticTemplateMatcher implements IMetaTemplateMatcher {

    private vertex[] tree;
    private Pair<String, Integer>[] templates;
    private boolean alreadyBuilt;
    private int templateNumber,N;
    private int templatesCount = 0;

    private int numberOfMatchOperations = 0;
    private int numberOfBuildOperations = 0;

    public TStaticTemplateMatcher(){
        tree = new vertex[10000];
        templates = new Pair[10000];
        alreadyBuilt = false;
        for (int i = 0; i < 10000; i++) {
            tree[i] = new vertex();
            numberOfBuildOperations++;
        }
        templateNumber = 0;
        N = 1;
    }

    public int getNumberOfMatchOperations() {
        return numberOfMatchOperations;
    }

    public int getNumberOfBuildOperations() {
        return numberOfBuildOperations;
    }

    void setTemplateNumber(int number) {
        templateNumber = number;
    }

    Pair<String, Integer>[] getTemplates() {
        return templates;
    }

    int getTemplatesCount() {
        return templatesCount;
    }

    @Override
    public int addTemplate(String template) throws TNotSupportedException {
        if (alreadyBuilt) {
            throw new TNotSupportedException("Adding templates after matching isn't supported in the TStaticTemplateMatcher");
        }
//        System.out.println("Adding template: " + templateNumber + " " + templatesCount + " " + template);
        templates[templatesCount++] = new Pair<>(template, templateNumber);
        int k = 0;
        for (int pos = 0; pos < template.length(); pos++) {
            numberOfBuildOperations++;
            int ch = template.charAt(pos) - 'a';
            if (tree[k].edge[ch] != -1) {
                k = tree[k].edge[ch];
            } else {
                tree[k].edge[ch] = N;
                tree[N].parent = k;
                tree[N].parentCh = ch;
                k = N;
                N++;
            }
        }
        tree[k].leaf.add(templateNumber);
        return templateNumber++;
    }

    @Override
    public ArrayList<Pair<Integer, Integer>> MatchStream(ICharStream stream) {
        ArrayList<Pair<Integer, Integer> > result = new ArrayList<Pair<Integer, Integer> >();
//        for (int i = 0; i < N; i++) {
//            for (int j = 0; j < 30; j++) {
//                if (tree[i].edge[j] != -1) {
//                    System.out.println(i + " " + tree[i].edge[j] + " " + j);
//                }
//            }
//        }
        alreadyBuilt = true;
        int v = 0;
        int pos = 0;
        while (!stream.isEmpty()) {
            int ch = stream.getChar() - 'a';
            v = go(v,ch);
            if (tree[v].leaf.size() > 0) {
                for (int l : tree[v].leaf) {
                    numberOfMatchOperations++;
                    result.add(new Pair<Integer, Integer>(pos, l));
                }
//                System.out.println(pos + " " + tree[v].leaf);
            }
            int u = getHardLink(v);
            while (u > 0) {
                for (int l : tree[u].leaf) {
                    numberOfMatchOperations++;
                    result.add(new Pair<Integer, Integer>(pos, l));
                }
//                System.out.println(pos + " " + tree[u].leaf);
                u = getHardLink(u);
            }
            pos++;
        }
        return result;
    }

    private int getLink(int v) {
//        System.out.println("getLink started with parametre " + v);
        numberOfMatchOperations++;
        if (tree[v].link != -1) {
            return tree[v].link;
        }
        if (v == 0 || tree[v].parent == 0) {
            return tree[v].link = 0;
        }
        return tree[v].link = go(getLink(tree[v].parent),tree[v].parentCh);
    }

    private int go(int v,int ch) {
//        System.out.println("go started with parametres " + v + " " + ch);
        numberOfMatchOperations++;
        if (tree[v].edge[ch] != -1) {
            return tree[v].go[ch] = tree[v].edge[ch];
        }
        if (tree[v].go[ch] != -1) {
            return tree[v].go[ch];
        }
        if (v == 0) {
            return tree[v].go[ch] = 0;
        }
        return  tree[v].go[ch] = go (getLink(v),ch);
    }
    private int getHardLink(int v) {
//        System.out.println("getHardLink started with parametres " + v + " " + ch);
        numberOfMatchOperations++;
        if (tree[v].hardLink != -1) {
            return tree[v].hardLink;
        }
        if (v == 0 || tree[v].parent == 0) {
            return tree[v].hardLink = 0;
        }
        int u = getLink(v);
        if (tree[u].leaf.size() > 0) {
            return tree[v].hardLink = u;
        }
        return tree[v].hardLink = getHardLink(u);
    }

    class vertex {
        int[] edge;
        ArrayList<Integer> leaf;
        int parent;
        int parentCh;
        int link;
        int[] go;
        int hardLink;
        vertex(){
            leaf = new ArrayList<>();
            parent = 0;
            parentCh = 0;
            link = -1;
            edge = new int[30];
            go = new int[30];
            hardLink = -1;
            for (int i = 0; i < 30; i++) {
                go[i] = -1;
                edge[i] = -1;
            }
        }
    }
}