import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Мирон on 24.10.2014 PACKAGE_NAME.
 */
public class TStaticTemplateMatcher implements IMetaTemplateMatcher {

    private ArrayList<vertex> tree;
    private ArrayList<Pair<String, Integer>> templates;
    private boolean alreadyBuilt;
    private int templateNumber,N;
    private int templatesCount = 0;

    private int numberOfMatchOperations = 0;
    private int numberOfBuildOperations = 0;

    public TStaticTemplateMatcher(){
        tree = new ArrayList<>();
        templates = new ArrayList<>();
        alreadyBuilt = false;
        templateNumber = 0;
        tree.add(new vertex());
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

    ArrayList<Pair<String, Integer>> getTemplates() {
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
        templatesCount++;
        templates.add(new Pair<>(template, templateNumber));
        int k = 0;
        for (int pos = 0; pos < template.length(); pos++) {
            numberOfBuildOperations++;
            int ch = template.charAt(pos) - 'a';
            while (tree.size() <= k) {
                tree.add(new vertex());
                numberOfBuildOperations++;
            }
            if (tree.get(k).edge[ch] != -1) {
                k = tree.get(k).edge[ch];
            } else {
                tree.get(k).edge[ch] = N;
                while (tree.size() <= N) {
                    tree.add(new vertex());
                    numberOfBuildOperations++;
                }
                tree.get(N).parent = k;
                tree.get(N).parentCh = ch;
                k = N;
                N++;
            }
        }
        tree.get(k).leaf.add(templateNumber);
        return templateNumber++;
    }

    @Override
    public ArrayList<Pair<Integer, Integer>> MatchStream(ICharStream stream) {
        ArrayList<Pair<Integer, Integer> > result = new ArrayList<Pair<Integer, Integer> >();
        alreadyBuilt = true;
        int v = 0;
        int pos = 0;
        while (!stream.isEmpty()) {
            int ch = stream.getChar() - 'a';
            v = go(v,ch);
            if (tree.get(v).leaf.size() > 0) {
                for (int l : tree.get(v).leaf) {
                    numberOfMatchOperations++;
                    result.add(new Pair<Integer, Integer>(pos, l));
                }
            }
            int u = getHardLink(v);
            while (u > 0) {
                for (int l : tree.get(u).leaf) {
                    numberOfMatchOperations++;
                    result.add(new Pair<Integer, Integer>(pos, l));
                }
                u = getHardLink(u);
            }
            pos++;
        }
        return result;
    }

    private int getLink(int v) {
        numberOfMatchOperations++;
        if (tree.get(v).link != -1) {
            return tree.get(v).link;
        }
        if (v == 0 || tree.get(v).parent == 0) {
            return tree.get(v).link = 0;
        }
        return tree.get(v).link = go(getLink(tree.get(v).parent),tree.get(v).parentCh);
    }

    private int go(int v,int ch) {
        numberOfMatchOperations++;
        if (tree.get(v).edge[ch] != -1) {
            return tree.get(v).go[ch] = tree.get(v).edge[ch];
        }
        if (tree.get(v).go[ch] != -1) {
            return tree.get(v).go[ch];
        }
        if (v == 0) {
            return tree.get(v).go[ch] = 0;
        }
        return  tree.get(v).go[ch] = go (getLink(v),ch);
    }

    private int getHardLink(int v) {
        numberOfMatchOperations++;
        if (tree.get(v).hardLink != -1) {
            return tree.get(v).hardLink;
        }
        if (v == 0 || tree.get(v).parent == 0) {
            return tree.get(v).hardLink = 0;
        }
        int u = getLink(v);
        if (tree.get(u).leaf.size() > 0) {
            return tree.get(v).hardLink = u;
        }
        return tree.get(v).hardLink = getHardLink(u);
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