package sonkd;

import java.util.Vector;

/**
 * HungarianAlg.java
 * TODO: 
 *
 * @author Kim Dinh Son
 * Email:sonkdbk@gmail.com
 */

public class HungarianAlg extends AssignmentProblemSolver{

	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver#Solve(java.util.Vector, java.util.Vector, sonkd.AssignmentProblemSolver.TMethod)
	 */
	@Override
	public double Solve(Vector<Vector<Double>> DistMatrix,
			Vector<Integer> Assignment, TMethod Method) {
		// TODO Auto-generated method stub
		int N=DistMatrix.size(); // number of columns (tracks)
		int M=DistMatrix.get(0).size(); // number of rows (measurements)

		int[] assignment		=new int[N];
		double[] distIn		=new double[N*M];

		double  cost = 0;
		// Fill matrix with random numbers
		for(int i=0; i<N; i++)
		{
			for(int j=0; j<M; j++)
			{
				distIn[i+N*j] = DistMatrix.get(i).get(j);
			}
		}
		switch(Method)
		{
		case optimal: assignmentoptimal(assignment, cost, distIn, N, M); break;

		case many_forbidden_assignments: assignmentoptimal(assignment, cost, distIn, N, M); break;

		case without_forbidden_assignments: assignmentoptimal(assignment, cost, distIn, N, M); break;
		}

		// form result
		Assignment.clear();
		for(int x=0; x<N; x++)
		{
			Assignment.add(assignment[x]);
		}
		System.out.println("end Solve");
		return cost;
	}
	
	/* Computes the optimal assignment (minimum overall costs) using Munkres algorithm.
	 * @see sonkd.AssignmentProblemSolver#assignmentoptimal(int[], double, double[], int, int)
	 */
	@Override
	public void assignmentoptimal(int[] assignment, double cost,
			double[] distMatrixIn, int nOfRows, int nOfColumns) {
		System.out.println("begin assignmentoptimal");
		double[] distMatrix;
		double[] distMatrixTemp = new double[nOfColumns];;

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

		//
		for(row=0; row<nOfElements; row++)
		{
			value = distMatrixIn[row];
			if(value < 0)
			{
				System.out.println("All matrix elements have to be non-negative.");
			}
			distMatrix[row] = value;
		}
		
		coveredColumns = new boolean[nOfElements];
		coveredRows    = new boolean[nOfElements];
		starMatrix     = new boolean[nOfElements];
		primeMatrix    = new boolean[nOfElements];
		newStarMatrix  = new boolean[nOfElements]; /* used in step4 */
		
		/* preliminary steps */
		if(nOfRows <= nOfColumns)
		{
			minDim = nOfRows;
			for(row=0; row<nOfRows; row++)
			{
				/* find the smallest element in the row */
				System.arraycopy(distMatrix, row, distMatrixTemp, 0, nOfColumns);
				minValue = distMatrixTemp[0];
				int k;
				for(k=1; k<nOfColumns;k++){
					if(minValue>distMatrixTemp[k]){
						minValue = distMatrixTemp[k];
					}
				}
				/* subtract the smallest element from each element of the row */
				int kk;
				for(kk=0; kk<nOfColumns;kk++){
					distMatrixTemp[kk] -= minValue;
				}
			}
			/* Steps 1 and 2a */
			for(row=0; row<nOfRows; row++)
			{
				for(col=0; col<nOfColumns; col++)
				{
					if(distMatrix[row + nOfRows*col] == 0)
					{
						if(!coveredColumns[col])
						{
							starMatrix[row + nOfRows*col] = true;
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
				distMatrixTemp = new double[nOfRows];
				for(int k = 0; k<nOfRows; k++){
					distMatrixTemp[k] = distMatrix[nOfRows*col];
				}
				minValue = distMatrixTemp[0];
				int q = 1;
				for(q = 1; q < nOfRows; q++)
				{
					if(minValue>distMatrixTemp[q]){
						minValue = distMatrixTemp[q];
					}
				}
				/* subtract the smallest element from each element of the column */
				int qq = 0;
				for(qq = 0; qq < nOfRows; q++)
				{
					distMatrixTemp[qq] -= minValue;
					qq++;
				}
			}
			/* Steps 1 and 2a */
			for(col=0; col<nOfColumns; col++)
			{
				for(row=0; row<nOfRows; row++)
				{
					if(distMatrix[row + nOfRows*col] == 0)
					{
						if(!coveredRows[row])
						{
							starMatrix[row + nOfRows*col] = true;
							coveredColumns[col]           = true;
							coveredRows[row]              = true;
							break;
						}
					}
				}
			}

			for(row=0; row<nOfRows; row++)
			{
				coveredRows[row] = false;
			}
		}
		
		/* move to step 2b */
		step2b(assignment, distMatrix, starMatrix, newStarMatrix, primeMatrix, coveredColumns, coveredRows, nOfRows, nOfColumns, minDim);
		/* compute cost and remove invalid assignments */
		computeassignmentcost(assignment, cost, distMatrixIn, nOfRows);
		System.out.println("end assignmentoptimal");
		return;

	}

	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver#buildassignmentvector(int[], boolean, int, int)
	 */
	@Override
	public void buildassignmentvector(int[] assignment, boolean[] starMatrix,
			int nOfRows, int nOfColumns) {
		System.out.println("begin buildassignmentvector");
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
		
		System.out.println("end buildassignmentvector");
	}
	
	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver#computeassignmentcost(int[], double, double[], int)
	 */
	@Override
	public void computeassignmentcost(int[] assignment, double cost,
			double[] distMatrix, int nOfRows) {
		System.out.println("begin computeassignmentcost");
		int row, col;
		for(row=0; row<nOfRows; row++)
		{
			col = assignment[row];
			if(col >= 0)
			{
				cost += distMatrix[row + nOfRows*col];
			}
		}
		System.out.println("end computeassignmentcost");
	}

	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver#step2a(int[], double[], boolean[], boolean[], boolean[], boolean[], boolean[], int, int, int)
	 */
	@Override
	public void step2a(int[] assignment, double[] distMatrix,
			boolean[] starMatrix, boolean[] newStarMatrix,
			boolean[] primeMatrix, boolean[] coveredColumns,
			boolean[] coveredRows, int nOfRows, int nOfColumns, int minDim) {
		System.out.println("begin step2a");
		boolean[] starMatrixTemp = new boolean[nOfRows];
		int col;
		for(col=0; col<nOfRows;col++){
			starMatrixTemp[col] = starMatrix[nOfRows*col];
		}
		/* cover every column containing a starred zero */
		for(col=0; col<nOfColumns; col++)
		{
			int k = 0;
			while(k < nOfRows)
			{
				if(starMatrixTemp[k])
				{
					coveredColumns[col] = true;
					break;
				}
				k++;
			}
		}
		/* move to step 3 */
		step2b(assignment, distMatrix, starMatrix, newStarMatrix, primeMatrix, coveredColumns, coveredRows, nOfRows, nOfColumns, minDim);
		System.out.println("end step2a");
	}

	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver#step2b(int[], double[], boolean[], boolean[], boolean[], boolean[], boolean[], int, int, int)
	 */
	@Override
	public void step2b(int[] assignment, double[] distMatrix,
			boolean[] starMatrix, boolean[] newStarMatrix,
			boolean[] primeMatrix, boolean[] coveredColumns,
			boolean[] coveredRows, int nOfRows, int nOfColumns, int minDim) {
		System.out.println("begin step2b");
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
			buildassignmentvector(assignment, starMatrix, nOfRows, nOfColumns);
		}
		else
		{
			/* move to step 3 */
			step3(assignment, distMatrix, starMatrix, newStarMatrix, primeMatrix, coveredColumns, coveredRows, nOfRows, nOfColumns, minDim);
		}
		
		System.out.println("end step2b");
	}

	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver#step3(int[], double[], boolean[], boolean[], boolean[], boolean[], boolean[], int, int, int)
	 */
	@Override
	public void step3(int[] assignment, double[] distMatrix,
			boolean[] starMatrix, boolean[] newStarMatrix,
			boolean[] primeMatrix, boolean[] coveredColumns,
			boolean[] coveredRows, int nOfRows, int nOfColumns, int minDim) {
		System.out.println("begin step3");
		boolean zerosFound;
		int row, col, starCol;
		zerosFound = true;
		while(zerosFound)
		{
			zerosFound = false;
			for(col=0; col<nOfColumns; col++)
			{
				if(!coveredColumns[col])
				{
					for(row=0; row<nOfRows; row++)
					{
						if((!coveredRows[row]) && (distMatrix[row + nOfRows*col] == 0))
						{
							/* prime zero */
							primeMatrix[row + nOfRows*col] = true;
							/* find starred zero in current row */
							for(starCol=0; starCol<nOfColumns; starCol++)
								if(starMatrix[row + nOfRows*starCol])
								{
									break;
								}
								if(starCol == nOfColumns) /* no starred zero found */
								{
									/* move to step 4 */
									step4(assignment, distMatrix, starMatrix, newStarMatrix, primeMatrix, coveredColumns, coveredRows, nOfRows, nOfColumns, minDim, row, col);
									return;
								}
								else
								{
									coveredRows[row]        = true;
									coveredColumns[starCol] = false;
									zerosFound              = true;
									break;
								}
						}
					}
				}
			}
		}
		/* move to step 5 */
		step5(assignment, distMatrix, starMatrix, newStarMatrix, primeMatrix, coveredColumns, coveredRows, nOfRows, nOfColumns, minDim);

		System.out.println("end step3");
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
		System.out.println("begin step4");
		int n, starRow, starCol, primeRow, primeCol;
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
			if(starMatrix[starRow + nOfRows*starCol])
			{
				break;
			}
		}
		
		System.out.println(">>> check step4");
		while(starRow<nOfRows)
		{
			/* unstar the starred zero */
			newStarMatrix[starRow + nOfRows*starCol] = false;
			/* find primed zero in current row */
			primeRow = starRow;
			for(primeCol=0; primeCol<nOfColumns; primeCol++)
			{
				if(primeMatrix[primeRow + nOfRows*primeCol])
				{
					break;
				}
			}
			/* star the primed zero */
			newStarMatrix[primeRow + nOfRows*primeCol] = true;
			/* find starred zero in current column */
			starCol = primeCol;
			for(starRow=0; starRow<nOfRows; starRow++)
			{
				if(starMatrix[starRow + nOfRows*starCol])
				{
					break;
				}
			}
			starRow++;
			System.out.println(">>> check while "+starRow+" "+ nOfRows);
		}
		System.out.println(">>> check step4");
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

		System.out.println("end step4");
	}

	/* (non-Javadoc)
	 * @see sonkd.AssignmentProblemSolver#step5(int[], double[], boolean[], boolean[], boolean[], boolean[], boolean[], int, int, int)
	 */
	@Override
	public void step5(int[] assignment, double[] distMatrix,
			boolean[] starMatrix, boolean[] newStarMatrix,
			boolean[] primeMatrix, boolean[] coveredColumns,
			boolean[] coveredRows, int nOfRows, int nOfColumns, int minDim) {
		System.out.println("begin step5");
		double h, value;
		int row, col;
		/* find smallest uncovered element h */
		h = Double.MAX_VALUE;
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
		System.out.println("end step5");
	}
	
	/* Computes a suboptimal solution. Good for cases with many forbidden assignments.
	 * @see sonkd.AssignmentProblemSolver#assignmentsuboptimal1(int, double, double, int, int)
	 */
	@Override
	public void assignmentsuboptimal1(int[] assignment, double cost,
			double[] distMatrixIn, int nOfRows, int nOfColumns) {
		System.out.println("begin assignmentsuboptimal1");
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
				if(distMatrix[row + nOfRows*col]!=Double.MAX_VALUE)
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
						if(distMatrix[row + nOfRows*col]!=Double.MAX_VALUE && (nOfValidObservations[row] == 1))
						{
							singleValidationFound = true;
							break;
						}

						if(singleValidationFound)
						{
							for(row=0; row<nOfRows; row++)
								if((nOfValidObservations[row] > 1) && distMatrix[row + nOfRows*col]!=Double.MAX_VALUE)
								{
									distMatrix[row + nOfRows*col] = Double.MAX_VALUE;
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
							if(distMatrix[row + nOfRows*col]!=Double.MAX_VALUE && (nOfValidTracks[col] == 1))
							{
								singleValidationFound = true;
								break;
							}
						}

						if(singleValidationFound)
						{
							for(col=0; col<nOfColumns; col++)
							{
								if((nOfValidTracks[col] > 1) && distMatrix[row + nOfRows*col]!=Double.MAX_VALUE)
								{
									distMatrix[row + nOfRows*col] = Double.MAX_VALUE;
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
					minValue = Double.MAX_VALUE;
					for(col=0; col<nOfColumns; col++)
					{
						value = distMatrix[row + nOfRows*col];
						if(value!=Double.MAX_VALUE)
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
							distMatrix[n + nOfRows*tmpCol] = Double.MAX_VALUE;
						}
						for(n=0; n<nOfColumns; n++)
						{
							distMatrix[row + nOfRows*n] = Double.MAX_VALUE;
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
					minValue = Double.MAX_VALUE;
					for(row=0; row<nOfRows; row++)
					{
						value = distMatrix[row + nOfRows*col];
						if(value!=Double.MAX_VALUE)
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
							distMatrix[n + nOfRows*col] = Double.MAX_VALUE;
						for(n=0; n<nOfColumns; n++)
							distMatrix[tmpRow + nOfRows*n] = Double.MAX_VALUE;
					}
				}
			}
		} /* if(infiniteValueFound) */


		/* now, recursively search for the minimum element and do the assignment */
		while(true)
		{
			/* find minimum distance observation-to-track pair */
			minValue = Double.MAX_VALUE;
			for(row=0; row<nOfRows; row++)
				for(col=0; col<nOfColumns; col++)
				{
					value = distMatrix[row + nOfRows*col];
					if(value!=Double.MAX_VALUE && (value < minValue))
					{
						minValue = value;
						tmpRow   = row;
						tmpCol   = col;
					}
				}

				if(minValue!=Double.MAX_VALUE)
				{
					assignment[tmpRow] = tmpCol;
					cost += minValue;
					for(n=0; n<nOfRows; n++)
						distMatrix[n + nOfRows*tmpCol] = Double.MAX_VALUE;
					for(n=0; n<nOfColumns; n++)
						distMatrix[tmpRow + nOfRows*n] = Double.MAX_VALUE;
				}
				else
					break;

		} /* while(true) */
		System.out.print("end assignmentsuboptimal1");
	}

	/* Computes a suboptimal solution. Good for cases with many forbidden assignments.
	 * @see sonkd.AssignmentProblemSolver#assignmentsuboptimal2(int, double, double, int, int)
	 */
	public void assignmentsuboptimal2(int[] assignment, double cost,
			double[] distMatrixIn, int nOfRows, int nOfColumns) {
		System.out.print("begin assignmentsuboptimal2");
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
			minValue = Double.MAX_VALUE;
			for(row=0; row<nOfRows; row++)
				for(col=0; col<nOfColumns; col++)
				{
					value = distMatrix[row + nOfRows*col];
					if(value!=Double.MAX_VALUE && (value < minValue))
					{
						minValue = value;
						tmpRow   = row;
						tmpCol   = col;
					}
				}

				if(minValue!=Double.MAX_VALUE)
				{
					assignment[tmpRow] = tmpCol;
					cost += minValue;
					for(n=0; n<nOfRows; n++)
					{
						distMatrix[n + nOfRows*tmpCol] = Double.MAX_VALUE;
					}
					for(n=0; n<nOfColumns; n++)
					{
						distMatrix[tmpRow + nOfRows*n] = Double.MAX_VALUE;
					}
				}
				else
					break;

		} /* while(true) */
		System.out.print("end assignmentsuboptimal2");
	}
}
