package test;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * TestMat.java TODO:
 * 
 * @author Kim Dinh Son Email:sonkdbk@gmail.com
 */

public class TestMat {
	private static double deltatime = 0.2;
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		Mat transitionMatrix = new Mat(4, 4, CvType.CV_8UC1, new Scalar(0));
		float[][] tM = { new float[] { 1, 0, 1, 0 },
				new float[] { 0, 1, 0, 1 }, new float[] { 0, 0, 1, 0 },
				new float[] { 0, 0, 0, 1 } };
		
		for (int j = 0; j < 4; j++) {
			for (int k = 0; k < 4; k++) {
				transitionMatrix.put(j, k, tM[j][k]);
			}
		}

		System.out.print(transitionMatrix.dump());
	}
}
