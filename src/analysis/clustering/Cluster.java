package analysis.clustering;

import javafx.scene.shape.Shape;
import javafx.util.Callback;

import java.util.*;

/**
 * Created by ThallionDarkshine on 9/13/2018.
 */
public class Cluster {
    public List<Float> vector;
    public Cluster c1, c2;
    public Cluster parent;
    public ClusterPair pair;
    public Map<Cluster, ClusterPair> pairs;
    public int size;
    public float height;
    public int index;
    public Clusterable data;
    public List<Shape> gfx;

    public Cluster(List<Float> vec, Clusterable data) {
        vector = vec;
        this.data = data;
        c1 = c2 = null;
        parent = null;
        pair = null;
        pairs = new HashMap<>();
        size = 1;
        height = 0f;
        gfx = new ArrayList<>();
    }

    public Cluster(Cluster c1, Cluster c2, ClusterPair pair) {
        this.c1 = c1;
        this.c2 = c2;
        this.pair = pair;

        c1.parent = this;
        c2.parent = this;
        parent = null;

        size = c1.size + c2.size;
        height = c1.pairs.get(c2).distance() / 2.0f;

        gfx = new ArrayList<>();
        pairs = new HashMap<>();

        for (Map.Entry<Cluster, ClusterPair> entry : c1.pairs.entrySet()) {
            Cluster other = entry.getKey();

            if (other == c2) continue;

            float d1 = entry.getValue().distance();
            float d2 = c2.pairs.get(other).distance();

            pairs.put(other, new ClusterPair(this, other, (d1 * c1.size + d2 * c2.size) / size));
        }
    }

    public void dfs(Callback<Cluster, Integer> func) {
        func.call(this);

        if (c1 != null) c1.dfs(func);
        if (c2 != null) c2.dfs(func);
    }

    public void ios(Callback<Cluster, Integer> func) {
        if (c1 != null) c1.ios(func);

        func.call(this);

        if (c2 != null) c2.ios(func);
    }

    public List<Cluster> cut(float cutHeight) {
        if (height > cutHeight) {
            List<Cluster> clusters = new ArrayList<>();
            clusters.addAll(c1.cut(cutHeight));
            clusters.addAll(c2.cut(cutHeight));

            return clusters;
        } else {
            return Collections.singletonList(this);
        }
    }

    public List<Cluster> cutFancy(float baseCutHeight, float percentDiffCutoff) {
        if (c1 == null && c2 == null) {
            System.out.println("leaf node");

            return Collections.singletonList(this);
        } else if (c1.size == 1 && c2.size == 1) {
            System.out.println("children are leafs");
            System.out.println(height + ", " + baseCutHeight);

            if (height < baseCutHeight) {
                return Collections.singletonList(this);
            } else {
                return Arrays.asList(c1, c2);
            }
        } else {
            List<Cluster> c1Cut = c1.cutFancy(baseCutHeight, percentDiffCutoff);
            List<Cluster> c2Cut = c2.cutFancy(baseCutHeight, percentDiffCutoff);

            if (c1Cut.size() == 1 && c2Cut.size() == 1) {
                if (c1.size == 1 || c2.size == 1) {
                    System.out.println("one child is a leaf, other is cluster");
                    System.out.println(height + ", " + baseCutHeight);

                    if (height < baseCutHeight) {
                        return Collections.singletonList(this);
                    } else {
                        return Arrays.asList(c1, c2);
                    }
                } else {
                    System.out.println("children are clusters");

                    float distance = ((height - c1.height) * c1.size + (height - c2.height) * c2.size) / size;
                    if (c1.size > 1 && c2.size > 1) distance *= 2;
                    float percentDiff = distance / (c1.height + c2.height);

                    System.out.println(c1.height + ", " + c2.height + ", " + height);
                    System.out.println(distance + ", " + percentDiff);

                    if (percentDiff < percentDiffCutoff) {
                        return Collections.singletonList(this);
                    } else {
                        return Arrays.asList(c1, c2);
                    }
                }
            } else {
                System.out.println("children are segmented");

                List<Cluster> clusters = new ArrayList<>(c1Cut);
                clusters.addAll(c2Cut);

                return clusters;
            }


        }
    }
}
