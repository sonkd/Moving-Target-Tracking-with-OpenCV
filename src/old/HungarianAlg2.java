package old;

import java.util.Vector;

/**
 * HungarianAlg.java
 * TODO: 
 *
 * @author Kim Dinh Son
 * Email:sonkdbk@gmail.com
 */

public class HungarianAlg2 extends AssignmentProblemSolver2{
	
	public static double[] C;
    public static int[] starMatrix;
    public static int[][] path;
    public static int[] RowCover;
    public static int[] ColCover;
    public double cost;
    public static int nrow;
    public static int ncol;
    public static int path_count = 0;
    public static int path_row_0;
    public static int path_col_0;
    public static int asgn = 0;

	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver2#Solve(java.util.Vector, java.util.Vector, sonkd.AssignmentProblemSolver2.TMethod)
	 */
	@Override
	public double Solve(Vector<Vector<Double>> DistMatrix,
			Vector<Integer> Assignment) {
		nrow = DistMatrix.size(); // number of columns (tracks)
		ncol =DistMatrix.get(0).size(); // number of rows (measurements)
		
		// Init
		int[] assignment = new int[nrow];
		C = new double[nrow * ncol];
		starMatrix = new int[nrow*ncol];
		path = new int[2 * nrow * ncol + 1][2];
		RowCover = new int[nrow];
		ColCover = new int[ncol];
		
		// Fill matrix with random numbers
		for(int i=0; i<nrow; i++)
		{
			for(int j=0; j<ncol; j++)
			{
				C[i*ncol+j] = DistMatrix.get(i).get(j);
			}
		}
		assignmentOptimal(assignment);
		
		// form result
		Assignment.clear();
		for(int x=0; x<ncol; x++)
		{
			Assignment.add(assignment[x]);
		}
		return cost;
	}
	
	/* Computes the optimal assignment (minimum overall costs) using Munkres algorithm.
	 * @see sonkd.AssignmentProblemSolver2#assignmentOptimal(int[], double, double[], int, int)
	 */
	@Override
	public void assignmentOptimal(int[] assignment) {
		cost = 0;
		for(int row=0; row<nrow; row++)
		{
			assignment[row] = -1;
		}
		if(nrow<=ncol)
			step_one();
		buildAssignmentVector(assignment, starMatrix);
		//ShowMaskMatrix(starMatrix, ColCover, RowCover, nrow, ncol);
		computeAssignmentCost(assignment);
		return;
	}
	
//	private void resetMaskAndCovers() {
//		for (int r = 0; r < nrow; r++) {
//			RowCover[r] = 0;
//			for (int c = 0; c < ncol; c++) {
//				starMatrix[r + nrow * c] = 0;
//			}
//		}
//		for (int c = 0; c < ncol; c++)
//			ColCover[c] = 0;
//	}
	
	//For each row of the cost matrix, find the smallest element and subtract
    //it from every element in its row.  When finished, Go to Step 2.
    private static void step_one()
    {
        double min_in_row;
		for (int r = 0; r < nrow; r++) {
	        min_in_row = C[0];
			for (int c = 0; c < ncol; c++)
				if (C[r*ncol+ c] < min_in_row)
					min_in_row = C[r*ncol+ c];
			for (int c = 0; c < ncol; c++)
				C[r*ncol+ c] -= min_in_row;
		}
        step_two();
    }
    
    //Find a zero (Z) in the resulting matrix.  If there is no starred 
    //zero in its row or column, star Z. Repeat for each element in the 
    //matrix. Go to Step 3.
    private static void step_two()
    {
        for (int r = 0; r < nrow; r++)
            for (int c = 0; c < ncol; c++)
            {
                if (C[r+nrow*c] == 0 && RowCover[r] == 0 && ColCover[c] == 0)
                {
                    starMatrix[r+nrow*c] = 1;
                    RowCover[r] = 1;
                    ColCover[c] = 1;
                }
            }
        for (int r = 0; r < nrow; r++)
            RowCover[r] = 0;
        for (int c = 0; c < ncol; c++)
            ColCover[c] = 0;
        step_three();
    }
    
    //Cover each column containing a starred zero.  If K columns are covered, 
    //the starred zeros describe a complete set of unique assignments.  In this 
    //case, Go to DONE, otherwise, Go to Step 4.
    private static void step_three()
    {
        int colcount;
        for (int r = 0; r < nrow; r++)
            for (int c = 0; c < ncol; c++)
                if (starMatrix[r*ncol+c] == 1)
                    ColCover[c] = 1;

        colcount = 0;
        for (int c = 0; c < ncol; c++)
            if (ColCover[c] == 1)
                colcount += 1;
        if (colcount >= ncol || colcount >=nrow)
            return;
        else
            step_four();
    }
    
    //methods to support step 4
    private static void find_a_zero(int row, int col)
    {
        int r = 0;
        int c;
        boolean done;
        row = -1;
        col = -1;
        done = false;
		while (!done) {
			c = 0;
			while (true) {
				if (C[r * ncol + c] == 0 && RowCover[r] == 0
						&& ColCover[c] == 0) {
					row = r;
					col = c;
					done = true;
				}
				c ++;
				if (c >= ncol || done)
					break;
			}
			r += 1;
			if (r >= nrow)
				// done = true;
				break;
		}
    }
    
    private static boolean star_in_row(int row)
    {
        boolean tmp = false;
        for (int c = 0; c < ncol; c++)
            if (starMatrix[row*ncol+c] == 1)
                tmp = true;
        return tmp;
    }

    private static void find_star_in_row(int row, int col)
    {
        col = -1;
        for (int c = 0; c < ncol; c++)
            if (starMatrix[row*ncol+c] == 1)
                col = c;
    }
    
    //Find a noncovered zero and prime it.  If there is no starred zero 
    //in the row containing this primed zero, Go to Step 5.  Otherwise, 
    //cover this row and uncover the column containing the starred zero. 
    //Continue in this manner until there are no uncovered zeros left. 
    //Save the smallest uncovered value and Go to Step 6.
    private static void step_four()
    {
        int row = -1;
        int col = -1;
        boolean done;

        done = false;
        while (!done)
        {
            find_a_zero(row, col);
            if (row == -1)
            {
                done = true;
                step_six();
            }
            else
            {
                starMatrix[row*ncol+col] = 2;
                if (star_in_row(row))
                {
                    find_star_in_row(row,col);
                    RowCover[row] = 1;
                    ColCover[col] = 0;
                }
                else
                {
                    done = true;
                    step_five();
                    path_row_0 = row;
                    path_col_0 = col;
                }
            }
        }
    }
    
 // methods to support step 5
    private static void find_star_in_col(int c, int r)
    {
        r = -1;
        for (int i = 0; i < nrow; i++)
            if (starMatrix[i+nrow*c] == 1)
                r = i;
    }

    private static void find_prime_in_row(int r, int c)
    {
        for (int j = 0; j < ncol; j++)
            if (starMatrix[r*ncol+j] == 2)
                c = j;
    }

	private static void augment_path() {
		for (int p = 0; p < path_count; p++)
			if (starMatrix[path[p][0] + nrow * path[p][1]] == 1)
				starMatrix[path[p][0] + nrow * path[p][1]] = 0;
			else
				starMatrix[path[p][0] + nrow * path[p][1]] = 1;
	}

    private static void clear_covers()
    {
        for (int r = 0; r < nrow; r++)
            RowCover[r] = 0;
        for (int c = 0; c < ncol; c++)
            ColCover[c] = 0;
    }

    private static void erase_primes()
    {
        for (int r = 0; r < nrow; r++)
            for (int c = 0; c < ncol; c++)
                if (starMatrix[r*ncol+c] == 2)
                    starMatrix[r*ncol+c] = 0;
    }
    
    //Construct a series of alternating primed and starred zeros as follows.  
    //Let Z0 represent the uncovered primed zero found in Step 4.  Let Z1 denote 
    //the starred zero in the column of Z0 (if any). Let Z2 denote the primed zero 
    //in the row of Z1 (there will always be one).  Continue until the series 
    //terminates at a primed zero that has no starred zero in its column.  
    //Unstar each starred zero of the series, star each primed zero of the series, 
    //erase all primes and uncover every line in the matrix.  Return to Step 3.
    private static void step_five()
    {
        boolean done;
        int r = -1;
        int c = -1;

        path_count = 1;
        path[path_count - 1][0] = path_row_0;
        path[path_count - 1][1] = path_col_0;
        done = false;
        while (!done)
        {
            find_star_in_col(path[path_count - 1][1], r);
            if (r > -1)
            {
                path_count += 1;
                path[path_count - 1][0] = r;
                path[path_count - 1][1] = path[path_count - 2][1];
            }
            else
                done = true;
            if (!done)
            {
                find_prime_in_row(path[path_count - 1][0], c);
                path_count += 1;
                path[path_count - 1][0] = path[path_count - 2][0];
                path[path_count - 1][1] = c;
            }
        }
        augment_path();
        clear_covers();
        erase_primes();
        step_three();
    }
    
    //methods to support step 6
    private static void find_smallest(double minval)
    {
        for (int r = 0; r < nrow; r++)
            for (int c = 0; c < ncol; c++)
                if (RowCover[r] == 0 && ColCover[c] == 0)
                    if (minval > C[r*ncol+c])
                        minval = C[r*ncol+c];
    }

    //Add the value found in Step 4 to every element of each covered row, and subtract 
    //it from every element of each uncovered column.  Return to Step 4 without 
    //altering any stars, primes, or covered lines.
    private static void step_six()
    {
        double minval = Double.MAX_VALUE;
        find_smallest(minval);
        for (int r = 0; r < nrow; r++)
            for (int c = 0; c < ncol; c++)
            {
                if (RowCover[r] == 1)
                    C[r*ncol+c] += minval;
                if (ColCover[c] == 0)
                    C[r*ncol+c] -= minval;
            }
        step_four();
    }

    
	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver2#computeAssignmentCost(int[], double, double[], int)
	 */
	@Override
	public void computeAssignmentCost(int[] assignment) {
		int row, col;
		for(row=0; row<nrow; row++)
		{
			col = assignment[row];
			if(col >= 0)
			{
				cost += C[row*ncol+col];
			}
		}	
	}

	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver2#buildAssignmentVector(int[], boolean[], int, int)
	 */
	@Override
	public void buildAssignmentVector(int[] assignment, int[] starMatrix) {
		for(int row=0; row<nrow; row++)
		{
			for(int col=0; col<ncol; col++)
			{
				if(starMatrix[row*ncol+col] == 1)
				{
					assignment[row] = col;
					break;
				}
			}
		}		
	}

}
