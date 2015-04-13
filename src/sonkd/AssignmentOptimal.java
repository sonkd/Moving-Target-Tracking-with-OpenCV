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
		int[] match = new int[dim];

		HungarianAlg3 b = new HungarianAlg3(DistMatrix);
		match = b.execute();
		
		// form result
		Assignment.clear();
		for (int x = 0; x < N; x++) {
			Assignment.add(match[x]);
		}
		
		return b.computeCost(DistMatrix, match);
	}
}
