package test;

import java.util.Vector;

import org.opencv.core.Point;

/**
 * Track.java TODO:
 * 
 * @author Kim Dinh Son Email:sonkdbk@gmail.com
 */

public abstract class Track {

	public Vector<Point> trace;
	public static int NextTrackID;
	public int track_id;
	public int skipped_frames;
	public int crossBorder;
	public Point prediction;
	public Kalman KF;

	/**
	 * @param pt
	 * @param dt
	 * @param Accel_noise_mag
	 */
	public Track(Point pt, float dt, float Accel_noise_mag) {
	}

	/**
	 * @param pt
	 * @param dt
	 * @param Accel_noise_mag
	 */
	public Track(float _dt, float _Accel_noise_mag, double _dist_thres,
			int _maximum_allowed_skipped_frames, int _max_trace_length) {
	}

	public void release() {
	};

}
