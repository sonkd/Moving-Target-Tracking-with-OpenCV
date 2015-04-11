package sonkd;

import java.util.Vector;

/**
 * AssignmentOptimal.java
 * TODO: 
 *
 * @author Kim Dinh Son
 * Email:sonkdbk@gmail.com
 */

public class AssignmentOptimal {
	public double Solve(Vector<Vector<Double>> DistMatrix,
			Vector<Integer> Assignment) {
		int nrow = DistMatrix.size(); // number of columns (tracks)
		int ncol = DistMatrix.get(0).size(); // number of rows (measurements)

		// Init
		int[] assignment = new int[nrow];
		double[][] costMatrix = new double[nrow][ncol];

		// Fill matrix with random numbers
		for (int i = 0; i < nrow; i++) {
			for (int j = 0; j < ncol; j++) {
				costMatrix[i][j] = DistMatrix.get(i).get(j);
			}
		}

		HungarianAlg3 b = new HungarianAlg3(costMatrix);
		assignment = b.execute();

		// form result
		Assignment.clear();
		for (int x = 0; x < ncol; x++) {
			Assignment.add(assignment[x]);
		}
		return b.computeCost(costMatrix, assignment);
	}
}
