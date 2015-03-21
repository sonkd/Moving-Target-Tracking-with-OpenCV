package sonkd;

import java.util.Vector;

import org.opencv.core.Point;

/**
 * Tracker.java TODO:
 * 
 * @author Kim Dinh Son Email:sonkdbk@gmail.com
 */

public class Tracker extends JTracker {
	int nextTrackID = 0;

	/**
	 * @param pt
	 * @param dt = 0.2
	 * @param Accel_noise_mag = 0.5
	 */
	public Tracker(Point pt, float dt, float Accel_noise_mag) {
		super(pt, dt, Accel_noise_mag);
		// TODO Auto-generated constructor stub
		track_id = nextTrackID;

		nextTrackID++;
		
		trace = new Vector<>();

		KF = new Kalman(pt, dt, Accel_noise_mag);

		prediction = pt;

		skipped_frames = 0;

		crossBorder = 0;
	}

	public void release() {
		//
	}
	
	public Tracker(float _dt, float _Accel_noise_mag, double _dist_thres,
			int _maximum_allowed_skipped_frames, int _max_trace_length) {
		super(_dt, _Accel_noise_mag, _dist_thres,
				_maximum_allowed_skipped_frames, _max_trace_length);
		// TODO Auto-generated constructor stub
		tracks = new Vector<>();
		dt=_dt;
		Accel_noise_mag=_Accel_noise_mag;
		dist_thres=_dist_thres;
		maximum_allowed_skipped_frames=_maximum_allowed_skipped_frames;
		max_trace_length=_max_trace_length;
	}

	public void update(Vector<Point> detections) {
		
		if(tracks.size()==0)
		{
			// If no tracks yet
			for(int i=0;i<detections.size();i++)
			{
				Tracker tr = new Tracker(detections.elementAt(i),dt,Accel_noise_mag);
				tracks.add(tr);
			}
		}

		// -----------------------------------
		// Number of tracks and detections
		// -----------------------------------
		int N = tracks.size();
		int M = detections.size();

		// -----------------------------------
		// If track didn't get detects long time, remove it.
		// -----------------------------------
		for(int i=0;i<tracks.size();i++)
		{
			if(tracks.get(i).skipped_frames>maximum_allowed_skipped_frames)
			{
				tracks.remove(i);
				//tracks.erase(tracks.begin()+i);
				//assignment.erase(assignment.begin()+i);
				i--;
			}
		}
	}
	
	
}
