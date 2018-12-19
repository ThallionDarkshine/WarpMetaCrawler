package analysis.clustering;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.util.List;
import java.util.Map;

/**
 * Created by ThallionDarkshine on 9/13/2018.
 */
public class PCA {
    public static List<Map.Entry<List<Float>, Object>> transform(List<Map.Entry<List<Float>, Object>> matrix, RealMatrix transform) {
        RealMatrix mMatrix = toRealMatrix(matrix);

        RealMatrix result = mMatrix.multiply(transform);

        for (int i = 0;i < result.getRowDimension();++i) {
            for (int j = 0;j < result.getColumnDimension();++j) {
                matrix.get(i).getKey().set(j, (float) result.getEntry(i, j));
            }
        }

        return fromRealMatrix(result, matrix);
    }

    public static List<Map.Entry<List<Float>, Object>> applyPca(List<Map.Entry<List<Float>, Object>> matrix) {
        RealMatrix mMatrix = toRealMatrix(matrix);

        Covariance covariance = new Covariance(mMatrix);
        RealMatrix cMatrix = covariance.getCovarianceMatrix();
        EigenDecomposition eigenDecomposition = new EigenDecomposition(cMatrix);
        RealMatrix result = eigenDecomposition.getV();

        return fromRealMatrix(result, matrix);
    }

    public static RealMatrix toRealMatrix(List<Map.Entry<List<Float>, Object>> matrix) {
        RealMatrix mMatrix = new Array2DRowRealMatrix(matrix.size(), matrix.get(0).getKey().size());

        for (int i = 0;i < mMatrix.getRowDimension();++i) {
            for (int j = 0;j < mMatrix.getColumnDimension();++j) {
                mMatrix.setEntry(i, j, matrix.get(i).getKey().get(j));
            }
        }

        return mMatrix;
    }

    public static List<Map.Entry<List<Float>, Object>> fromRealMatrix(RealMatrix matrix, List<Map.Entry<List<Float>, Object>> basis) {
        for (int i = 0;i < matrix.getRowDimension();++i) {
            for (int j = 0;j < matrix.getColumnDimension();++j) {
                basis.get(i).getKey().set(j, (float) matrix.getEntry(i, j));
            }
        }

        return basis;
    }

    public static RealMatrix buildPcaTransform(RealMatrix matrix) {
        RealMatrix mtm = matrix.transpose().multiply(matrix);

        System.out.println(mtm.getRowDimension());
        System.out.println(mtm.getColumnDimension());

        EigenDecomposition eigenDecomposition = new EigenDecomposition(mtm);

        return eigenDecomposition.getV();
    }
}
