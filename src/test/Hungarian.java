package test;

import java.util.Vector;

import old.HungarianAlg2;

/**
 * Hungarian.java
 * TODO: 
 *
 * @author Kim Dinh Son
 * Email:sonkdbk@gmail.com
 */

public class Hungarian {
	public static void main(String[] args){
		Reader rd = new Reader("munkres_1.dat");
		int nrow = rd.getNrow();
		int ncol = rd.getNCol();
        double[] C = rd.getMatrix();
        
		Vector<Vector<Double>> Cost = new Vector<>(); // size: N, M
		Vector<Integer> assignment = new Vector<>(); // assignment according to Hungarian algorithm
		for(int i=0;i<nrow;i++)
		{
			Vector<Double> costRow = new Vector<>();			
			for(int j=0;j<ncol;j++)
			{
				costRow.add(C[i+nrow*j]);
			}
			Cost.add(i, costRow);
		}
		
		ShowCostMatrix(Cost, nrow, ncol);
		
        HungarianAlg2 alg = new HungarianAlg2();
        
        alg.Solve(Cost, assignment);
        
        System.out.println();
        ShowCostMatrix(Cost, nrow, ncol);
        //ShowMaskMatrix(alg.test_assignment, ColCover, RowCover, nrow, ncol);
        
	}
	
	private static void ShowCostMatrix(Vector<Vector<Double>> cost, int nrow, int ncol)
    {
        for (int r = 0; r < nrow; r++)
        {

            for (int c = 0; c < ncol; c++)
            {
                System.out.print(cost.get(r).get(c) + " ");
            }
            System.out.println();
        }
    }
}
