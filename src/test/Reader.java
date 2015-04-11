package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author KimDinh
 * 
 */
public class Reader {
	private int nrow;
	private int ncol;
	private double[] matrix;

	public Reader(String matrix) {
		inputMatrixFromFile(matrix);		
	}

	public void inputMatrixFromFile(String graph) {
		File file = new File(graph);
		try {
			Scanner input;
			try (FileInputStream is = new FileInputStream(file)) {
				input = new Scanner(is, "UTF-8");
				nrow = (int) input.nextDouble();
				ncol = (int) input.nextDouble();

				matrix = new double[nrow * ncol];

				for (int r = 0; r < nrow; r++)
					for (int c = 0; c < ncol; c++)
						matrix[r + nrow * c] = input.nextDouble();
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the file " + ioe);
		}
	}
	
	public int getNrow(){
		return this.nrow;
	}
	
	public int getNCol(){
		return this.ncol;
	}
	
	public double[] getMatrix(){
		return this.matrix;
	}
}
