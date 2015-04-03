package sonkd;

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
}
