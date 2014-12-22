import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Мирон on 17.12.2014 PACKAGE_NAME.
 */
public class T2DSingleTemplateMatcher {

    private ArrayList<vertex> tree;
    private ArrayList<Pair<String, Integer>> templates;
    private boolean alreadyBuilt;
    private int templateNumber, N;
    private ArrayList<String> A;
    private int NA = 0;
    private int MA = 0;
    private ArrayList<Integer>[] equivalencyClass;
    private int numberOfMatchOperations = 0;
    private int numberOfBuildOperations = 0;

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

        build(B);

        int[][] counter = new int[NA][MA];
        for (int i = 0; i < NA; i++) {
            for (int j = 0; j < MA; j++) {
                counter[i][j] = 0;
            }
        }

        for (int i = 0; i < NA; i++) {
            ArrayList<Pair<Integer, Integer>> res = matchOneString(A.get(i));
            for (Pair<Integer, Integer> p : res) {
                for (int j = 0; j < equivalencyClass[p.getValue()].size(); j++) {
                    int rowIndex = equivalencyClass[p.getValue()].get(j);
                    int k = p.getKey() - B.get(rowIndex).length() + 1;
                    if (i < rowIndex) {
                        continue;
                    }
                    counter[i - rowIndex][k]++;
                }
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

    public ArrayList<Pair<Integer, Integer>> matchOneString(String stream) {
        ArrayList<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
        alreadyBuilt = true;
        int v = 0;
        int pos = 0;
        for (int i = 0; i < stream.length(); i++) {
            int ch = stream.charAt(i) - 'a';
            v = go(v, ch);
            if (tree.get(v).leaf != -1) {
                numberOfMatchOperations++;
                result.add(new Pair<Integer, Integer>(pos, tree.get(v).leaf));
            }
            int u = getHardLink(v);
            while (u > 0) {
                numberOfMatchOperations++;
                result.add(new Pair<Integer, Integer>(pos, tree.get(u).leaf));
                u = getHardLink(u);
            }
            pos++;
        }
        return result;
    }

    public int addString(String template) throws TNotSupportedException {
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
        if (tree.get(k).leaf == -1) {
            tree.get(k).leaf = templateNumber;
            return templateNumber++;
        }
        return tree.get(k).leaf;
    }

    private void build(ArrayList<String> B) throws TNotSupportedException {
        equivalencyClass = new ArrayList[B.size()];
        for (int i = 0; i < B.size(); i++) {
            equivalencyClass[i] = new ArrayList<>();
        }
        tree = new ArrayList<>();
        templates = new ArrayList<>();
        alreadyBuilt = false;
        templateNumber = 0;
        tree.add(new vertex());
        N = 1;
        for (int i = 0; i < B.size(); i++) {
            equivalencyClass[addString(B.get(i))].add(i);
        }
    }

    public int getNumberOfMatchOperations() {
        return numberOfMatchOperations;
    }

    public int getNumberOfBuildOperations() {
        return numberOfBuildOperations;
    }

    private int getLink(int v) {
        numberOfMatchOperations++;
        if (tree.get(v).link != -1) {
            return tree.get(v).link;
        }
        if (v == 0 || tree.get(v).parent == 0) {
            return tree.get(v).link = 0;
        }
        return tree.get(v).link = go(getLink(tree.get(v).parent), tree.get(v).parentCh);
    }

    private int go(int v, int ch) {
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
        return tree.get(v).go[ch] = go(getLink(v), ch);
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
        if (tree.get(u).leaf != -1) {
            return tree.get(v).hardLink = u;
        }
        return tree.get(v).hardLink = getHardLink(u);
    }

    class vertex {
        int[] edge;
        int leaf;
        int parent;
        int parentCh;
        int link;
        int[] go;
        int hardLink;

        vertex() {
            leaf = - 1;
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
