package MultiObjectTracking;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.video.KalmanFilter;

/**
 * Kalman.java TODO:
 * 
 * @author Kim Dinh Son Email:sonkdbk@gmail.com
 */

public class Kalman extends KalmanFilter{
	private KalmanFilter kalman;
	private Mat measurement;

	public void init() {
		kalman = new KalmanFilter(4, 2, 0, CvType.CV_32F);
		// state = new MatOfFloat(4, 1, CvType.CV_32FC1); toa do (X,Y), van toc
		// (0,0)
		// transitionMatrix = new Mat(4, 2, CvType.CV_32FC1);
		// float[][] tM = { new float[] { 1, 0, 1, 0 },
		// new float[] { 0, 1, 0, 1 }, new float[] { 0, 0, 1, 0 },
		// new float[] { 0, 0, 0, 1 } };
		// for (int j = 0; j < 4; j++) {
		// transitionMatrix.put(j, 0, tM[j]);
		// }

		measurement = new Mat(2, 1, CvType.CV_32F);

		// kalman.statePre.at<float>(0) = startX;
		// kalman.statePre.at<float>(1) = startY;
		// kalman.statePre.at<float>(2) = 0;
		// kalman.statePre.at<float>(3) = 0;
		// setIdentity(kalman.measurementMatrix);
		// setIdentity(kalman.processNoiseCov, Scalar::all(1e-4));
		// setIdentity(kalman.measurementNoiseCov, Scalar::all(10));
		// setIdentity(kalman.errorCovPost, Scalar::all(.1));
	}
	
	public void setMeasurement(MatOfFloat measurement){
		this.measurement = measurement;
	}
	
	public Mat getMeasurement(){
		return this.measurement;
	}

	public Mat correct() {
		System.out.println(measurement.dump());
		return kalman.correct(measurement);
	}

	public Mat predict() {
		return kalman.predict();
	}
}
