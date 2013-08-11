import org.jblas.ComplexDouble;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;

import java.util.ArrayList;
import java.util.List;


public class JBLASSpike {
    public static void main(String[] args) {
        DoubleMatrix matrix = new DoubleMatrix(new double[][] {
                {1,1,0,0,1,0,0},
                {1,1,0,0,1,0,0},
                {0,0,1,1,1,0,0},
                {0,0,1,1,1,0,0},
                {1,1,1,1,1,1,1},
                {0,0,0,0,1,1,1},
                {0,0,0,0,1,1,1},
        });

        List<Double> principalEigenvector = getPrincipalEigenvector(matrix);
        System.out.println("principalEigenvector = " + principalEigenvector);
        System.out.println("normalisedPrincipalEigenvector = " + normalised(principalEigenvector));

        System.out.println("Eigen.eigenvectors(matrix)[0] = " + printFriendly(Eigen.eigenvectors(matrix)[0].getColumn(1)));
        System.out.println("Eigen.eigenvectors(matrix)[0] = " + printFriendly(Eigen.eigenvectors(matrix)[0].getColumn(2)));
        System.out.println("Eigen.eigenvectors(matrix)[0] = " + printFriendly(Eigen.eigenvectors(matrix)[0].getColumn(3)));
    }

    public static List<Double> normalised(List<Double> principalEigenvector) {
        double total = sum(principalEigenvector);
        List<Double> normalisedValues = new ArrayList<Double>();
        for (Double aDouble : principalEigenvector) {
            normalisedValues.add(aDouble / total);
        }
        return normalisedValues;
    }

    private static double sum(List<Double> principalEigenvector) {
        double total = 0;
        for (Double aDouble : principalEigenvector) {
            total += aDouble;
        }
        return total;
    }

    private static String printFriendly(ComplexDoubleMatrix column) {
        String result = "";
        for (ComplexDouble complexDouble : column.toArray()) {
            result += " " + complexDouble.abs();
        }

        return result;
    }

    public static List<Double> getPrincipalEigenvector(DoubleMatrix matrix) {
        int maxIndex = getMaxIndex(matrix);
        ComplexDoubleMatrix eigenVectors = Eigen.eigenvectors(matrix)[0];
        return getEigenVector(eigenVectors, maxIndex);
    }

    private static int getMaxIndex(DoubleMatrix matrix) {
        ComplexDouble[] doubleMatrix = Eigen.eigenvalues(matrix).toArray();
        int maxIndex = 0;
        for (int i = 0; i < doubleMatrix.length; i++){
            double newnumber = doubleMatrix[i].abs();
            if ((newnumber > doubleMatrix[maxIndex].abs())){
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private static List<Double> getEigenVector(ComplexDoubleMatrix eigenvector, int columnId) {
        ComplexDoubleMatrix column = eigenvector.getColumn(columnId);

        List<Double> values = new ArrayList<Double>();
        for (ComplexDouble value : column.toArray()) {
//            values.add(value / column.sum());
            values.add(value.abs()  );
        }
        return values;
    }
}
