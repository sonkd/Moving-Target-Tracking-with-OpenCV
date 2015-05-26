package sonkd;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.video.KalmanFilter;

/**
 * Kalman.java TODO:
 * 
 * @author Kim Dinh Son Email:sonkdbk@gmail.com
 */

public class Kalman extends KalmanFilter {
	private KalmanFilter kalman;
	private Point LastResult;
	private double deltatime;

	public void init() {

	}
	
	public Kalman(Point pt){
		kalman = new KalmanFilter(4, 2, 0, CvType.CV_32F);

		Mat transitionMatrix = new Mat(4, 4, CvType.CV_32F, new Scalar(0));
		float[] tM = { 
			    1, 0, 1, 0, 
			    0, 1, 0, 1,
			    0, 0, 1, 0,
			    0, 0, 0, 1 } ;
		transitionMatrix.put(0,0,tM);

		kalman.set_transitionMatrix(transitionMatrix);

		LastResult = pt;
		Mat statePre = new Mat(4, 1, CvType.CV_32F, new Scalar(0)); // Toa do (x,y), van toc (0,0)
		statePre.put(0, 0, pt.x);
		statePre.put(1, 0, pt.y);
		kalman.set_statePre(statePre);

		kalman.set_measurementMatrix(Mat.eye(2,4, CvType.CV_32F));
		
		Mat processNoiseCov = Mat.eye(4, 4, CvType.CV_32F);
		processNoiseCov = processNoiseCov.mul(processNoiseCov, 1e-4);
		kalman.set_processNoiseCov(processNoiseCov);

		Mat id1 = Mat.eye(2,2, CvType.CV_32F);
		id1 = id1.mul(id1,1e-1);
		kalman.set_measurementNoiseCov(id1);
		
		Mat id2 = Mat.eye(4,4, CvType.CV_32F);
		//id2 = id2.mul(id2,0.1);
		kalman.set_errorCovPost(id2);
	}

	public Kalman(Point pt, double dt, double Accel_noise_mag) {
		kalman = new KalmanFilter(4, 2, 0, CvType.CV_32F);
		deltatime = dt;

		Mat transitionMatrix = new Mat(4, 4, CvType.CV_32F, new Scalar(0));
		float[] tM = { 
			    1, 0, 1, 0, 
			    0, 1, 0, 1,
			    0, 0, 1, 0,
			    0, 0, 0, 1 } ;
		transitionMatrix.put(0,0,tM);

		kalman.set_transitionMatrix(transitionMatrix);

		// init
		LastResult = pt;
		Mat statePre = new Mat(4, 1, CvType.CV_32F, new Scalar(0)); // Toa do (x,y), van toc (0,0)
		statePre.put(0, 0, pt.x);
		statePre.put(1, 0, pt.y);
		statePre.put(2, 0, 0);
		statePre.put(3, 0, 0);
		kalman.set_statePre(statePre);

		Mat statePost = new Mat(4, 1, CvType.CV_32F, new Scalar(0));
		statePost.put(0, 0, pt.x);
		statePost.put(1, 0, pt.y);
		statePost.put(2, 0, 0);
		statePost.put(3, 0, 0);
		kalman.set_statePost(statePost);
		
		kalman.set_measurementMatrix(Mat.eye(2,4, CvType.CV_32F));

		//Mat processNoiseCov = Mat.eye(4, 4, CvType.CV_32F);
		Mat processNoiseCov = new Mat(4, 4, CvType.CV_32F, new Scalar(0));
		float[] dTime = { (float) (Math.pow(deltatime, 4.0) / 4.0), 0,
				(float) (Math.pow(deltatime, 3.0) / 2.0), 0, 0,
				(float) (Math.pow(deltatime, 4.0) / 4.0), 0,
				(float) (Math.pow(deltatime, 3.0) / 2.0),
				(float) (Math.pow(deltatime, 3.0) / 2.0), 0,
				(float) Math.pow(deltatime, 2.0), 0, 0,
				(float) (Math.pow(deltatime, 3.0) / 2.0), 0,
				(float) Math.pow(deltatime, 2.0) };
		processNoiseCov.put(0, 0, dTime);
		
		processNoiseCov = processNoiseCov.mul(processNoiseCov, Accel_noise_mag); // Accel_noise_mag = 0.5
		kalman.set_processNoiseCov(processNoiseCov);

		Mat id1 = Mat.eye(2,2, CvType.CV_32F);
		id1 = id1.mul(id1,1e-1);
		kalman.set_measurementNoiseCov(id1);
		
		Mat id2 = Mat.eye(4,4, CvType.CV_32F);
		id2 = id2.mul(id2,.1);
		kalman.set_errorCovPost(id2);
	}

	public Point getPrediction() {
		Mat prediction = kalman.predict();
		LastResult = new Point(prediction.get(0, 0)[0], prediction.get(1, 0)[0]);
		return LastResult;
	}

	public Point update(Point p, boolean dataCorrect) {
		Mat measurement = new Mat(2, 1, CvType.CV_32F, new Scalar(0)) ; 
		if (!dataCorrect) {
			measurement.put(0, 0, LastResult.x);
			measurement.put(1, 0, LastResult.y);
		} else {
			measurement.put(0, 0, p.x);
			measurement.put(1, 0, p.y);
		}
		// Correction
		Mat estimated = kalman.correct(measurement);
		LastResult.x = estimated.get(0, 0)[0];
		LastResult.y = estimated.get(1, 0)[0];
		return LastResult;
	}
	
	// check
	public Point correction(Point p){
		Mat measurement = new Mat(2, 1, CvType.CV_32F, new Scalar(0));
		measurement.put(0, 0, p.x);
		measurement.put(1, 0, p.y);
		
		Mat estimated = kalman.correct(measurement);
		LastResult.x = estimated.get(0, 0)[0];
		LastResult.y = estimated.get(1, 0)[0];
		return LastResult;
	}

	/**
	 * @return the deltatime
	 */
	public double getDeltatime() {
		return deltatime;
	}

	/**
	 * @param deltatime
	 *            the deltatime to set
	 */
	public void setDeltatime(double deltatime) {
		this.deltatime = deltatime;
	}

	/**
	 * @return the lastResult
	 */
	public Point getLastResult() {
		return LastResult;
	}

	/**
	 * @param lastResult
	 *            the lastResult to set
	 */
	public void setLastResult(Point lastResult) {
		LastResult = lastResult;
	}
}
