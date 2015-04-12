package old;

import java.util.Vector;

/**
 * AssignmentProblemSolver.java
 * TODO: 
 *
 * @author Kim Dinh Son
 * Email:sonkdbk@gmail.com
 */

public abstract class AssignmentProblemSolver2 {
	// --------------------------------------------------------------------------
	// Computes the optimal assignment (minimum overall costs) using Munkres algorithm.
	// --------------------------------------------------------------------------
	public abstract void assignmentOptimal(int[] assignment);

	public AssignmentProblemSolver2() {
	};
	
	public abstract double Solve(Vector<Vector<Double> >DistMatrix,Vector<Integer>Assignment);
	public abstract void computeAssignmentCost(int[] assignment);
	public abstract void buildAssignmentVector(int[] assignment, int[] starMatrix);

	
	protected static void ShowMaskMatrix(int[] M,int[] ColCover, int[] RowCover, int nrow, int ncol)
    {
		System.out.println();
        for (int c = 0; c < ncol; c++)
        	System.out.print(" " + ColCover[c]);
        for (int r = 0; r < nrow; r++)
        {
        	System.out.print("\n  " + RowCover[r] + "  ");
            for (int c = 0; c < ncol; c++)
            {
            	System.out.print(M[r+nrow*c] + " ");
            }
        }
    }
}
