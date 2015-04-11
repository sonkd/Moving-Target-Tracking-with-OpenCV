package test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * HungarianAlgorithmTest.java
 * TODO: 
 *
 * @author Kim Dinh Son
 * Email:sonkdbk@gmail.com
 */

public class HungarianAlgorithmTest {
	  private static double computeCost(double[][] matrix, int[] match) {
	    double result = 0;
	    Set<Integer> visited = new HashSet<Integer>();
	    for (int i = 0; i < matrix.length; i++) {
	      if (match[i] == -1) {
	        continue;
	      }
	      if (!visited.add(match[i])) {
	        Assert.fail();
	      }
	      result += matrix[i][match[i]];
	    }
	    return result;
	  }

	  @Test
	  public void test1() {
	    double[][] matrix = new double[][] { new double[] { 4.0, 1.5, 4.0 },
	        new double[] { 4.0, 4.5, 6.0 }, new double[] { 3.0, 2.25, 3.0 } };
	    HungarianAlgorithmOn3 b = new HungarianAlgorithmOn3(matrix);
	    int[] match = b.execute();
	    Assert.assertTrue(Arrays.equals(new int[] { 1, 0, 2 }, match));
	    Assert.assertEquals(8.5, computeCost(matrix, match), 0.0000001);
	  }

	  @Test
	  public void test2() {
	    double[][] matrix = new double[][] { new double[] { 1.0, 1.0, 0.8 },
	        new double[] { 0.9, 0.8, 0.1 }, new double[] { 0.9, 0.7, 0.4 } };
	    HungarianAlgorithmOn3 b = new HungarianAlgorithmOn3(matrix);
	    int[] match = b.execute();
	    Assert.assertTrue(Arrays.equals(new int[] { 0, 2, 1 }, match));
	    Assert.assertEquals(1.8, computeCost(matrix, match), 0.0000001);
	  }

	  @Test
	  public void test3() {
	    double[][] matrix = new double[][] { new double[] { 6.0, 0.0, 7.0, 5.0 },
	        new double[] { 2.0, 6.0, 2.0, 6.0 },
	        new double[] { 2.0, 7.0, 2.0, 1.0 },
	        new double[] { 9.0, 4.0, 7.0, 1.0 } };
	    HungarianAlgorithmOn3 b = new HungarianAlgorithmOn3(matrix);
	    int[] match = b.execute();
	    Assert.assertTrue(Arrays.equals(new int[] { 1, 0, 2, 3 }, match));
	    Assert.assertEquals(5, computeCost(matrix, match), 0.0000001);
	  }

	  @Test
	  public void testInvalidInput() {
	    try {
	      new HungarianAlgorithmOn3(new double[][] { new double[] { 1, 2 },
	          new double[] { 3 } });
	      Assert.fail();
	    } catch (IllegalArgumentException e) {

	    }
	    try {
	      new HungarianAlgorithmOn3(null);
	      Assert.fail();
	    } catch (NullPointerException e) {

	    }
	  }

//	  @Test
//	  public void testUnassignedJob() {
//	    double[][] matrix = new double[][] {
//	        new double[] { 6.0, 0.0, 7.0, 5.0, 2.0 },
//	        new double[] { 2.0, 6.0, 2.0, 6.0, 7.0 },
//	        new double[] { 2.0, 7.0, 2.0, 1.0, 1.0 },
//	        new double[] { 9.0, 4.0, 7.0, 1.0, 0.0 } };
//	    HungarianAlgorithmOn3 b = new HungarianAlgorithmOn3(matrix);
//	    int[] match = b.execute();
//	    Assert.assertTrue(Arrays.equals(new int[] { 1, 0, 3, 4 }, match));
//	    Assert.assertEquals(3, computeCost(matrix, match), 0.0000001);
//	  }
//
//	  @Test
//	  public void testUnassignedWorker() {
//	    double[][] matrix = new double[][] { new double[] { 6.0, 0.0, 7.0, 5.0 },
//	        new double[] { 2.0, 6.0, 2.0, 6.0 },
//	        new double[] { 2.0, 7.0, 2.0, 1.0 },
//	        new double[] { 9.0, 4.0, 7.0, 1.0 },
//	        new double[] { 0.0, 0.0, 0.0, 0.0 } };
//	    HungarianAlgorithmOn3 b = new HungarianAlgorithmOn3(matrix);
//	    int[] match = b.execute();
//	    Assert.assertTrue(Arrays.equals(new int[] { 1, -1, 2, 3, 0 }, match));
//	    Assert.assertEquals(3, computeCost(matrix, match), 0.0000001);
//	  }
	}
