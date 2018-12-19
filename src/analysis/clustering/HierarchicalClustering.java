package analysis.clustering;

import java.util.*;

/**
 * Created by ThallionDarkshine on 9/13/2018.
 */
public class HierarchicalClustering {
    public static float distance(List<Float> vec1, List<Float> vec2) {
        if (vec1.size() != vec2.size()) throw new RuntimeException("Error: Operation not supported for vectors of different size.");

        float distance = 0f;

        for (int i = 0;i < vec1.size();++i) {
            distance += Math.abs(vec1.get(i) - vec2.get(i));
        }

        return distance;
    }

    public static Cluster buildDendrogram(List<Map.Entry<List<Float>, Clusterable>> data) {
        List<Cluster> clusters = new ArrayList<>();
        PriorityQueue<ClusterPair> priorityQueue = new PriorityQueue<>((o1, o2) -> Float.compare(o1.distance(), o2.distance()));

        for (Map.Entry<List<Float>, Clusterable> entry : data) {
            Cluster c = new Cluster(entry.getKey(), entry.getValue());
            clusters.add(c);
        }

        for (int i = 0;i < clusters.size() - 1;++i) {
            Cluster c1 = clusters.get(i);
            for (int j = i + 1;j < clusters.size();++j) {
                Cluster c2 = clusters.get(j);

                float distance = c1.data.distance(c2.data);
                ClusterPair pair = new ClusterPair(c1, c2, distance);

                c1.pairs.put(c2, pair);
                c2.pairs.put(c1, pair);

                priorityQueue.add(pair);
            }
        }

        while (clusters.size() > 1) {
            ClusterPair toMerge = priorityQueue.poll();

            Cluster merged = new Cluster(toMerge.c1, toMerge.c2, toMerge);

            clusters.remove(toMerge.c1);
            clusters.remove(toMerge.c2);

            for (ClusterPair p : toMerge.c1.pairs.values()) {
                if (p.c1 != toMerge.c2 && p.c2 != toMerge.c2) {
                    priorityQueue.remove(p);

                    p.getOtherChild(toMerge.c1).pairs.remove(toMerge.c1);
                }
            }
            for (ClusterPair p : toMerge.c2.pairs.values()) {
                if (p.c1 != toMerge.c1 && p.c2 != toMerge.c1) {
                    priorityQueue.remove(p);

                    p.getOtherChild(toMerge.c2).pairs.remove(toMerge.c2);
                }
            }

            for (ClusterPair p : merged.pairs.values()) {
                priorityQueue.add(p);
                p.c2.pairs.put(merged, p);
            }

            clusters.add(merged);
        }

        return clusters.get(0);
    }
}
