package sonkd;

import java.util.Vector;

import org.opencv.core.Point;

/**
 * Track.java TODO:
 * 
 * @author Kim Dinh Son Email:sonkdbk@gmail.com
 */

public class Track {

	public Vector<Point> trace;
	public Vector<Point> history;
	public static int NextTrackID;
	public int track_id;
	public int skipped_frames;
	public int crossBorder;
	public Point prediction;
	public Kalman KF;

	/**
	 * @param pt
	 * @param dt = 0.2
	 * @param Accel_noise_mag = 0.5
	 */
	public Track(Point pt, float dt, float Accel_noise_mag, int id) {
		trace = new Vector<>();
		history = new Vector<>();
		track_id=id;
		// Every track have its own Kalman filter,
		// it user for next point position prediction.
		// KF = new Kalman(pt);
		KF = new Kalman(pt,dt,Accel_noise_mag);
		// Here stored points coordinates, used for next position prediction.
		prediction = pt;

		skipped_frames = 0;

		crossBorder = 0;
	}	
}
