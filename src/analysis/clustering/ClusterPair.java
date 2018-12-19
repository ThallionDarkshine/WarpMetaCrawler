package analysis.clustering;

/**
 * Created by ThallionDarkshine on 9/13/2018.
 */
public class ClusterPair {
    public Cluster c1, c2;
    public float distance;

    public ClusterPair(Cluster c1, Cluster c2, float distance) {
        this.c1 = c1;
        this.c2 = c2;
        this.distance = distance;
    }

    public Cluster getOtherChild(Cluster c) {
        return c1 == c ? c2 : c1;
    }

    public float distance() {
        return distance;
    }
}
