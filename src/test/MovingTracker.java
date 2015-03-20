package test;

import java.util.Vector;

import org.opencv.core.Point;

/**
 * MovingTracker.java
 * TODO: 
 *
 * @author Kim Dinh Son
 * Email:sonkdbk@gmail.com
 */

public class MovingTracker {
	public Kalman KF;
	public Vector<Point> predictions;
	
	public MovingTracker(){
		predictions = new Vector<>();
	}

}
