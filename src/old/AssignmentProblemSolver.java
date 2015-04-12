package old;

import java.util.Vector;

/**
 * AssignmentProblemSolver.java
 * TODO: 
 *
 * @author Kim Dinh Son
 * Email:sonkdbk@gmail.com
 */

public abstract class AssignmentProblemSolver {
	// --------------------------------------------------------------------------
	// Computes the optimal assignment (minimum overall costs) using Munkres algorithm.
	// --------------------------------------------------------------------------
	public abstract void assignmentOptimal(int[] assignment, double cost, double[] distIn, int nOfRows, int nOfColumns);
	
	public abstract void buildAssignmentVector(int[] assignment, boolean[] starMatrix, int nOfRows, int nOfColumns);
	public abstract void computeAssignmentCost(int[] assignment, double cost, double[] distMatrix, int nOfRows);
	public abstract void step2a(int[] assignment, double[] distMatrix, boolean[] starMatrix, boolean[] newStarMatrix, boolean[] primeMatrix, boolean[] coveredColumns, boolean[] coveredRows, int nOfRows, int nOfColumns, int minDim);
	public abstract void step2b(int[] assignment, double[] distMatrix, boolean[] starMatrix, boolean[] newStarMatrix, boolean[] primeMatrix, boolean[] coveredColumns, boolean[] coveredRows, int nOfRows, int nOfColumns, int minDim);
	public abstract void step3 (int[] assignment, double[] distMatrix, boolean[] starMatrix, boolean[] newStarMatrix, boolean[] primeMatrix, boolean[] coveredColumns, boolean[] coveredRows, int nOfRows, int nOfColumns, int minDim);
	public abstract void step4 (int[] assignment, double[] distMatrix, boolean[] starMatrix, boolean[] newStarMatrix, boolean[] primeMatrix, boolean[] coveredColumns, boolean[] coveredRows, int nOfRows, int nOfColumns, int minDim, int row, int col);
	public abstract void step5 (int[] assignment, double[] distMatrix, boolean[] starMatrix, boolean[] newStarMatrix, boolean[] primeMatrix, boolean[] coveredColumns, boolean[] coveredRows, int nOfRows, int nOfColumns, int minDim);
	// --------------------------------------------------------------------------
	// Computes a suboptimal solution. Good for cases with many forbidden assignments.
	// --------------------------------------------------------------------------
	public abstract void assignmentSuboptimal_1(int[] assignment, double cost, double[] distMatrixIn, int nOfRows, int nOfColumns);
	// --------------------------------------------------------------------------
	// Computes a suboptimal solution. Good for cases with many forbidden assignments.
	// --------------------------------------------------------------------------
	public abstract void assignmentSuboptimal_2(int[] assignment, double cost, double[] distMatrixIn, int nOfRows, int nOfColumns);
	
	public enum TMethod { optimal, many_forbidden_assignments, without_forbidden_assignments};
	public AssignmentProblemSolver() {
	};
	public abstract double Solve(Vector<Vector<Double> >DistMatrix,Vector<Integer>Assignment,TMethod method); // Medthod = optimal

}
