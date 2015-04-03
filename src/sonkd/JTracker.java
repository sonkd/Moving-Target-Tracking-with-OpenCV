package sonkd;

import java.util.Vector;

import org.opencv.core.Point;

/**
 * JTracker.java TODO:
 * 
 * @author Kim Dinh Son Email:sonkdbk@gmail.com
 */

public abstract class JTracker extends Track {

	public float dt;

	public float Accel_noise_mag;

	public double dist_thres;

	public int maximum_allowed_skipped_frames;

	public int max_trace_length;

	public Vector<Track> tracks;

	public abstract void update(Vector<Point> detections);

	/**
	 * @param pt
	 * @param dt
	 * @param Accel_noise_mag
	 */
	public JTracker(float _dt, float _Accel_noise_mag, double _dist_thres,
			int _maximum_allowed_skipped_frames, int _max_trace_length) {
	}

	/**
	 * @param pt
	 * @param dt2
	 * @param accel_noise_mag2
	 */
	public JTracker(Point pt, float dt2, float accel_noise_mag2) {
	}
}
