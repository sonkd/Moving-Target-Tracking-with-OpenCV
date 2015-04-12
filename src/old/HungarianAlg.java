package old;

import java.util.Vector;

/**
 * HungarianAlg.java
 * TODO: 
 *
 * @author Kim Dinh Son
 * Email:sonkdbk@gmail.com
 */

public class HungarianAlg extends AssignmentProblemSolver{

	static double MAX = Double.MAX_VALUE;
	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver#Solve(java.util.Vector, java.util.Vector, sonkd.AssignmentProblemSolver.TMethod)
	 */
	@Override
	public double Solve(Vector<Vector<Double>> DistMatrix,
			Vector<Integer> Assignment, TMethod Method) {
		// TODO Auto-generated method stub
		int N = DistMatrix.size(); // number of columns (tracks)
		int M = DistMatrix.get(0).size(); // number of rows (measurements)

		int[] assignment = new int[N];
		double[] distIn = new double[N * M];

		double  cost = 0;
		// Fill matrix with random numbers
		for(int i=0; i<N; i++)
		{
			for(int j=0; j<M; j++)
			{
				distIn[i+N*j] = DistMatrix.get(i).get(j);
			}
		}
		switch (Method) {
		case optimal:
			assignmentOptimal(assignment, cost, distIn, N, M);
			break;

		case many_forbidden_assignments:
			assignmentSuboptimal_1(assignment, cost, distIn, N, M);
			break;

		case without_forbidden_assignments:
			assignmentSuboptimal_2(assignment, cost, distIn, N, M);
			break;
		}

		// form result
		Assignment.clear();
		for (int x = 0; x < N; x++) {
			Assignment.add(assignment[x]);
		}
		return cost;
	}
	
	/* Computes the optimal assignment (minimum overall costs) using Munkres algorithm.
	 * @see sonkd.AssignmentProblemSolver#assignmentoptimal(int[], double, double[], int, int)
	 */
	@Override
	public void assignmentOptimal(int[] assignment, double cost,
			double[] distMatrixIn, int nOfRows, int nOfColumns) {
		double[] distMatrix;
		double  value;
		double  minValue;

		boolean[] coveredColumns;
		boolean[] coveredRows;
		boolean[] starMatrix;
		boolean[] newStarMatrix;
		boolean[] primeMatrix;

		int nOfElements;
		int minDim;
		int row;
		int col;
		
		// Init
		cost = 0;
		for(row=0; row<nOfRows; row++)
		{
			assignment[row] = -1;
		}
		
		// Generate distance matrix
		// and check matrix elements positiveness :)

		// Total elements number
		nOfElements   = nOfRows * nOfColumns;
		distMatrix    = new double[nOfElements];

		for (row = 0; row < nOfElements; row++) {
			value = distMatrixIn[row];
			if (value < 0) {
				System.out
						.println("All matrix elements have to be non-negative.");
			}
			distMatrix[row] = value;
		}
		
		coveredColumns = new boolean[nOfColumns];
		coveredRows    = new boolean[nOfRows];
		starMatrix     = new boolean[nOfElements];
		primeMatrix    = new boolean[nOfElements];
		newStarMatrix  = new boolean[nOfElements]; /* used in step4 */
		
		/*
		 * preliminary steps.
		 * Create an nxm matrix called the cost matrix in
		 * which each element represents the cost of assigning one of n workers
		 * to one of m jobs. Rotate the matrix so that there are at least as
		 * many columns as rows and let k=min(n,m).
		 */
		if(nOfRows <= nOfColumns)
		{
			minDim = nOfRows;
			for(row=0; row<nOfRows; row++)
			{
				/* find the smallest element in the row */
				minValue = distMatrix[0];
				for (col = 0; col < nOfColumns; col++) {
					if (minValue > distMatrix[row * nOfColumns + col]) {
						minValue = distMatrix[row * nOfColumns + col];
					}
				}
				/* subtract the smallest element from each element of the row */
				for (col = 0; col < nOfColumns; col++) {
					distMatrix[row * nOfColumns + col] -= minValue;
				}
			}
			/* Steps 1 and 2a */
			for(row=0; row<nOfRows; row++)
			{
				for(col=0; col<nOfColumns; col++)
				{
					if(distMatrix[row * nOfColumns + col] == 0)
					{
						if(!coveredColumns[col])
						{
							starMatrix[row * nOfColumns + col] = true;
							coveredColumns[col]           = true;
							break;
						}
					}
				}
			}
		}
		else /* if(nOfRows > nOfColumns) */
		{
			minDim = nOfColumns;
			for(col=0; col<nOfColumns; col++)
			{
				/* find the smallest element in the column */
				minValue = distMatrix[0];

				for (row = 0; row < nOfRows; row++) {
					if (minValue > distMatrix[row + col * nOfRows]) {
						minValue = distMatrix[row + col * nOfRows];
					}
				}
				/* subtract the smallest element from each element of the column */
				for (row = 0; row < nOfRows; row++) {
					distMatrix[row + col * nOfRows] -= minValue;
				}

			}
			/* Steps 1 and 2a */
			for (col = 0; col < nOfColumns; col++) {
				for (row = 0; row < nOfRows; row++) {
					if (distMatrix[row + nOfRows * col] == 0) {
						if (!coveredRows[row]) {
							starMatrix[row + nOfRows * col] = true;
							coveredColumns[col] = true;
							coveredRows[row] = true;
							break;
						}
					}
				}
			}

			for (row = 0; row < nOfRows; row++) {
				coveredRows[row] = false;
			}
		}
		
		/* move to step 2b */
		step2b(assignment, distMatrix, starMatrix, newStarMatrix, primeMatrix, coveredColumns, coveredRows, nOfRows, nOfColumns, minDim);
		/* compute cost and remove invalid assignments */
		computeAssignmentCost(assignment, cost, distMatrixIn, nOfRows);
		return;

	}

	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver#buildassignmentvector(int[], boolean, int, int)
	 */
	@Override
	public void buildAssignmentVector(int[] assignment, boolean[] starMatrix,
			int nOfRows, int nOfColumns) {
		int row, col;
		for(row=0; row<nOfRows; row++)
		{
			for(col=0; col<nOfColumns; col++)
			{
				if(starMatrix[row + nOfRows*col])
				{
					assignment[row] = col;
					break;
				}
			}
		}	
		
	}
	
	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver#computeassignmentcost(int[], double, double[], int)
	 */
	@Override
	public void computeAssignmentCost(int[] assignment, double cost,
			double[] distMatrix, int nOfRows) {
		int row, col;
		for(row=0; row<nOfRows; row++)
		{
			col = assignment[row];
			if(col >= 0)
			{
				cost += distMatrix[row + nOfRows * col];
			}
		}
	}

	/*
	 * Find a zero (Z) in the resulting matrix. If there is no starred zero in
	 * its row or column, star Z. Repeat for each element in the matrix. Go to
	 * Step 3.
	 * 
	 * @see sonkd.AssignmentProblemSolver#step2a(int[], double[], boolean[],
	 * boolean[], boolean[], boolean[], boolean[], int, int, int)
	 */
	@Override
	public void step2a(int[] assignment, double[] distMatrix,
			boolean[] starMatrix, boolean[] newStarMatrix,
			boolean[] primeMatrix, boolean[] coveredColumns,
			boolean[] coveredRows, int nOfRows, int nOfColumns, int minDim) {
		int col, row;
		/* cover every column containing a starred zero */
		for (col = 0; col < nOfColumns; col++) {
			for (row = 0; row < nOfRows; row++)
				if (starMatrix[row * nOfColumns + col]) {
					coveredColumns[col] = true;
					break;
				}
		}
		/* move to step 3 */
		step2b(assignment, distMatrix, starMatrix, newStarMatrix, primeMatrix, coveredColumns, coveredRows, nOfRows, nOfColumns, minDim);
	}

	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver#step2b(int[], double[], boolean[], boolean[], boolean[], boolean[], boolean[], int, int, int)
	 */
	@Override
	public void step2b(int[] assignment, double[] distMatrix,
			boolean[] starMatrix, boolean[] newStarMatrix,
			boolean[] primeMatrix, boolean[] coveredColumns,
			boolean[] coveredRows, int nOfRows, int nOfColumns, int minDim) {
		int col, nOfCoveredColumns;
		/* count covered columns */
		nOfCoveredColumns = 0;
		for(col=0; col<nOfColumns; col++)
		{
			if(coveredColumns[col])
			{
				nOfCoveredColumns++;
			}
		}
		if(nOfCoveredColumns == minDim)
		{
			/* algorithm finished */
			buildAssignmentVector(assignment, starMatrix, nOfRows, nOfColumns);
		}
		else
		{
			/* move to step 3 */
			step3(assignment, distMatrix, starMatrix, newStarMatrix, primeMatrix, coveredColumns, coveredRows, nOfRows, nOfColumns, minDim);
		}
	}

	/*
	 * Cover each column containing a starred zero. If K columns are covered,
	 * the starred zeros describe a complete set of unique assignments. In this
	 * case, Go to DONE, otherwise, Go to Step 4.
	 * 
	 * @see sonkd.AssignmentProblemSolver#step3(int[], double[], boolean[],
	 * boolean[], boolean[], boolean[], boolean[], int, int, int)
	 */
	@Override
	public void step3(int[] assignment, double[] distMatrix,
			boolean[] starMatrix, boolean[] newStarMatrix,
			boolean[] primeMatrix, boolean[] coveredColumns,
			boolean[] coveredRows, int nOfRows, int nOfColumns, int minDim) {
		boolean zerosFound;
		int row, col, starCol;
		zerosFound = true;
		while (zerosFound) {
			zerosFound = false;
			for (col = 0; col < nOfColumns; col++) {
				if (!coveredColumns[col]) {
					for (row = 0; row < nOfRows; row++) {
						if ((!coveredRows[row])
								&& (distMatrix[row + col * nOfRows] == 0)) {
							/* prime zero */
							primeMatrix[row + col * nOfRows] = true;
							/* find starred zero in current row */
							for (starCol = 0; starCol < nOfColumns; starCol++)
								if (starMatrix[row * nOfColumns + starCol]) {
									break;
								}
							if (starCol == nOfColumns) /* no starred zero found */
							{
								/* move to step 4 */
								step4(assignment, distMatrix, starMatrix,
										newStarMatrix, primeMatrix,
										coveredColumns, coveredRows, nOfRows,
										nOfColumns, minDim, row, col);
								return;
							} else {
								coveredRows[row] = true;
								coveredColumns[starCol] = false;
								zerosFound = true;
								break;
							}
						}
					}
				}
			}
		}
		/* move to step 5 */
		step5(assignment, distMatrix, starMatrix, newStarMatrix, primeMatrix, coveredColumns, coveredRows, nOfRows, nOfColumns, minDim);
	}

	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver#step4(int[], double[], boolean[], boolean[], boolean[], boolean[], boolean[], int, int, int, int, int)
	 */
	@Override
	public void step4(int[] assignment, double[] distMatrix,
			boolean[] starMatrix, boolean[] newStarMatrix,
			boolean[] primeMatrix, boolean[] coveredColumns,
			boolean[] coveredRows, int nOfRows, int nOfColumns, int minDim,
			int row, int col) {
		int n, starRow, starCol, primeRow, primeCol = 0;
		int nOfElements = nOfRows*nOfColumns;
		/* generate temporary copy of starMatrix */
		for(n=0; n<nOfElements; n++)
		{
			newStarMatrix[n] = starMatrix[n];
		}
		/* star current zero */
		newStarMatrix[row + nOfRows*col] = true;
		/* find starred zero in current column */
		starCol = col;
		for(starRow=0; starRow<nOfRows; starRow++)
		{
			if(starMatrix[starRow * nOfColumns + starCol])
			{
				break;
			}
		}

		while(starRow<nOfRows)
		{
			/* unstar the starred zero */
			newStarMatrix[starRow + nOfRows*starCol] = false;
			/* find primed zero in current row */
			primeRow = starRow;
			for (primeCol = 0; primeCol < nOfColumns; primeCol++) {
				if (primeMatrix[primeRow + nOfRows * primeCol]) {
					break;
				}
			}
			
			/* star the primed zero */
			newStarMatrix[primeRow + nOfRows*primeCol] = true;			
			/* find starred zero in current column */
			starCol = primeCol;
			for(starRow=0; starRow<nOfRows; starRow++)
			{
				if(starMatrix[starRow  * nOfColumns + starCol])
				{
					break;
				}
				//System.out.println(" starRow "+starRow+" size "+nOfColumns);
			}
		}

		/* use temporary copy as new starMatrix */
		/* delete all primes, uncover all rows */
		for(n=0; n<nOfElements; n++)
		{
			primeMatrix[n] = false;
			starMatrix[n]  = newStarMatrix[n];
		}
		for(n=0; n<nOfRows; n++)
		{
			coveredRows[n] = false;
		}
		/* move to step 2a */
		step2a(assignment, distMatrix, starMatrix, newStarMatrix, primeMatrix, coveredColumns, coveredRows, nOfRows, nOfColumns, minDim);

	}

	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver#step5(int[], double[], boolean[], boolean[], boolean[], boolean[], boolean[], int, int, int)
	 */
	@Override
	public void step5(int[] assignment, double[] distMatrix,
			boolean[] starMatrix, boolean[] newStarMatrix,
			boolean[] primeMatrix, boolean[] coveredColumns,
			boolean[] coveredRows, int nOfRows, int nOfColumns, int minDim) {
		double h, value;
		int row, col;
		/* find smallest uncovered element h */
		h = MAX;
		for(row=0; row<nOfRows; row++)
		{
			if(!coveredRows[row])
			{
				for(col=0; col<nOfColumns; col++)
				{
					if(!coveredColumns[col])
					{
						value = distMatrix[row + nOfRows*col];
						if(value < h)
						{
							h = value;
						}
					}
				}
			}
		}
		/* add h to each covered row */
		for(row=0; row<nOfRows; row++)
		{
			if(coveredRows[row])
			{
				for(col=0; col<nOfColumns; col++)
				{
					distMatrix[row + nOfRows*col] += h;
				}
			}
		}
		/* subtract h from each uncovered column */
		for(col=0; col<nOfColumns; col++)
		{
			if(!coveredColumns[col])
			{
				for(row=0; row<nOfRows; row++)
				{
					distMatrix[row + nOfRows*col] -= h;
				}
			}
		}
		/* move to step 3 */
		step3(assignment, distMatrix, starMatrix, newStarMatrix, primeMatrix, coveredColumns, coveredRows, nOfRows, nOfColumns, minDim);
	}
	
	/* Computes a suboptimal solution. Good for cases with many forbidden assignments.
	 * @see sonkd.AssignmentProblemSolver#assignmentsuboptimal1(int, double, double, int, int)
	 */
	@Override
	public void assignmentSuboptimal_1(int[] assignment, double cost,
			double[] distMatrixIn, int nOfRows, int nOfColumns) {
		boolean infiniteValueFound, finiteValueFound, repeatSteps, allSinglyValidated, singleValidationFound;
		int n, row, col, tmpRow = 0, tmpCol = 0;
		int[] nOfValidObservations, nOfValidTracks;
		double value, minValue;
		double[] distMatrix = distMatrixIn;

		cost = 0;

		for(row=0; row<nOfRows; row++)
		{
			assignment[row] = -1;
		}

		/* allocate memory */
		nOfValidObservations  = new int[nOfRows];
		nOfValidTracks        = new int[nOfColumns];

		/* compute number of validations */
		infiniteValueFound = false;
		finiteValueFound  = false;
		for(row=0; row<nOfRows; row++)
		{
			for(col=0; col<nOfColumns; col++)
			{
				if(distMatrix[row + nOfRows*col]!=MAX)
				{
					nOfValidTracks[col]       += 1;
					nOfValidObservations[row] += 1;
					finiteValueFound = true;
				}
				else
					infiniteValueFound = true;
			}
		}

		if(infiniteValueFound)
		{
			if(!finiteValueFound)
			{
				return;
			}
			repeatSteps = true;

			while(repeatSteps)
			{
				repeatSteps = false;

				/* step 1: reject assignments of multiply validated tracks to singly validated observations		 */
				for(col=0; col<nOfColumns; col++)
				{
					singleValidationFound = false;
					for(row=0; row<nOfRows; row++)
						if(distMatrix[row + nOfRows*col]!=MAX && (nOfValidObservations[row] == 1))
						{
							singleValidationFound = true;
							break;
						}

						if(singleValidationFound)
						{
							for(row=0; row<nOfRows; row++)
								if((nOfValidObservations[row] > 1) && distMatrix[row + nOfRows*col]!=MAX)
								{
									distMatrix[row + nOfRows*col] = MAX;
									nOfValidObservations[row] -= 1;
									nOfValidTracks[col]       -= 1;
									repeatSteps = true;
								}
						}
				}

				/* step 2: reject assignments of multiply validated observations to singly validated tracks */
				if(nOfColumns > 1)
				{
					for(row=0; row<nOfRows; row++)
					{
						singleValidationFound = false;
						for(col=0; col<nOfColumns; col++)
						{
							if(distMatrix[row + nOfRows*col]!=MAX && (nOfValidTracks[col] == 1))
							{
								singleValidationFound = true;
								break;
							}
						}

						if(singleValidationFound)
						{
							for(col=0; col<nOfColumns; col++)
							{
								if((nOfValidTracks[col] > 1) && distMatrix[row + nOfRows*col]!=MAX)
								{
									distMatrix[row + nOfRows*col] = MAX;
									nOfValidObservations[row] -= 1;
									nOfValidTracks[col]       -= 1;
									repeatSteps = true;
								}
							}
						}
					}
				}
			} /* while(repeatSteps) */

			/* for each multiply validated track that validates only with singly validated  */
			/* observations, choose the observation with minimum distance */
			for(row=0; row<nOfRows; row++)
			{
				if(nOfValidObservations[row] > 1)
				{
					allSinglyValidated = true;
					minValue = MAX;
					for(col=0; col<nOfColumns; col++)
					{
						value = distMatrix[row + nOfRows*col];
						if(value!=MAX)
						{
							if(nOfValidTracks[col] > 1)
							{
								allSinglyValidated = false;
								break;
							}
							else if((nOfValidTracks[col] == 1) && (value < minValue))
							{
								tmpCol   = col;
								minValue = value;
							}
						}
					}

					if(allSinglyValidated)
					{
						assignment[row] = tmpCol;
						cost += minValue;
						for(n=0; n<nOfRows; n++)
						{
							distMatrix[n + nOfRows*tmpCol] = MAX;
						}
						for(n=0; n<nOfColumns; n++)
						{
							distMatrix[row + nOfRows*n] = MAX;
						}
					}
				}
			}

			/* for each multiply validated observation that validates only with singly validated  */
			/* track, choose the track with minimum distance */
			for(col=0; col<nOfColumns; col++)
			{
				if(nOfValidTracks[col] > 1)
				{
					allSinglyValidated = true;
					minValue = MAX;
					for(row=0; row<nOfRows; row++)
					{
						value = distMatrix[row + nOfRows*col];
						if(value!=MAX)
						{
							if(nOfValidObservations[row] > 1)
							{
								allSinglyValidated = false;
								break;
							}
							else if((nOfValidObservations[row] == 1) && (value < minValue))
							{
								tmpRow   = row;
								minValue = value;
							}
						}
					}

					if(allSinglyValidated)
					{
						assignment[tmpRow] = col;
						cost += minValue;
						for(n=0; n<nOfRows; n++)
							distMatrix[n + nOfRows*col] = MAX;
						for(n=0; n<nOfColumns; n++)
							distMatrix[tmpRow + nOfRows*n] = MAX;
					}
				}
			}
		} /* if(infiniteValueFound) */


		/* now, recursively search for the minimum element and do the assignment */
		while(true)
		{
			/* find minimum distance observation-to-track pair */
			minValue = MAX;
			for(row=0; row<nOfRows; row++)
				for(col=0; col<nOfColumns; col++)
				{
					value = distMatrix[row + nOfRows*col];
					if(value!=MAX && (value < minValue))
					{
						minValue = value;
						tmpRow   = row;
						tmpCol   = col;
					}
				}

				if(minValue!=MAX)
				{
					assignment[tmpRow] = tmpCol;
					cost += minValue;
					for(n=0; n<nOfRows; n++)
						distMatrix[n + nOfRows*tmpCol] = MAX;
					for(n=0; n<nOfColumns; n++)
						distMatrix[tmpRow + nOfRows*n] = MAX;
				}
				else
					break;

		} /* while(true) */
	}

	/* Computes a suboptimal solution. Good for cases with many forbidden assignments.
	 * @see sonkd.AssignmentProblemSolver#assignmentsuboptimal2(int, double, double, int, int)
	 */
	public void assignmentSuboptimal_2(int[] assignment, double cost,
			double[] distMatrixIn, int nOfRows, int nOfColumns) {
		int n, row, col, tmpRow = 0, tmpCol = 0, nOfElements;
		double value, minValue;
		nOfElements   = nOfRows * nOfColumns;
		double[] distMatrix = new double[nOfElements];
		
		for(n=0; n<nOfElements; n++)
		{
			distMatrix[n] = distMatrixIn[n];
		}

		/* initialization */
		cost = 0;
		for(row=0; row<nOfRows; row++)
		{
			assignment[row] = -1;
		}

		/* recursively search for the minimum element and do the assignment */
		while(true)
		{
			/* find minimum distance observation-to-track pair */
			minValue = MAX;
			for(row=0; row<nOfRows; row++)
				for(col=0; col<nOfColumns; col++)
				{
					value = distMatrix[row + nOfRows*col];
					if(value!=MAX && (value < minValue))
					{
						minValue = value;
						tmpRow   = row;
						tmpCol   = col;
					}
				}

				if(minValue!=MAX)
				{
					assignment[tmpRow] = tmpCol;
					cost += minValue;
					for(n=0; n<nOfRows; n++)
					{
						distMatrix[n + nOfRows*tmpCol] = MAX;
					}
					for(n=0; n<nOfColumns; n++)
					{
						distMatrix[tmpRow + nOfRows*n] = MAX;
					}
				}
				else
					break;

		} /* while(true) */
	}
}
