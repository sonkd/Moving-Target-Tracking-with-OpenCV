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
	public double Solve(double[][] DistMatrix,
			Vector<Integer> Assignment) {
		int N = DistMatrix.length; // number of columns (tracks)
		int M = DistMatrix[0].length; // number of rows (measurements)
		int dim =  Math.max(N, M);

		// Init
		int[] assignment = new int[N];
		int[] match = new int[dim];
		double[][] costMatrix = new double[dim][dim];

		// Fill matrix with random numbers
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				costMatrix[i][j] = DistMatrix[i][j];
			}
		}

		HungarianAlg3 b = new HungarianAlg3(costMatrix);
		match = b.execute();
		System.arraycopy(match, 0, assignment, 0, N);

		// form result
		for (int x = 0; x < N; x++) {
			Assignment.add(assignment[x]);
		}
		return b.computeCost(costMatrix, match);
	}
}
