package sonkd;

import java.util.Vector;

import org.opencv.core.Point;

/**
 * Tracker.java TODO:
 * 
 * @author Kim Dinh Son Email:sonkdbk@gmail.com
 */

public class Tracker extends JTracker{
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
	
	double euclideanDist(Point p, Point q)
	{
	    Point diff= new Point(p.x-q.x, p.y-q.y);
	    return Math.sqrt(diff.x*diff.x + diff.y*diff.y);    
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
		// int N = tracks.size();
		// int M = detections.size();
		
		// Cost matrix.
		Vector<Vector<Double>> Cost = new Vector<>(); // size: N, M
		Vector<Integer> assignment = new Vector<>(); // assignment according to Hungarian algorithm

		// -----------------------------------
		// Caculate cost matrix (distances)
		// -----------------------------------
		for(int i=0;i<tracks.size();i++)
		{
			Vector<Double> costRow = new Vector<>();			
			for(int j=0;j<detections.size();j++)
			{
				costRow.add(j,euclideanDist(tracks.get(i).prediction, detections.get(j)));
			}
			Cost.add(i, costRow);
		}
		
		// -----------------------------------
		// Solving assignment problem (tracks and predictions of Kalman filter)
		// -----------------------------------
		HungarianAlg APS = new HungarianAlg();
		APS.Solve(Cost,assignment, HungarianAlg.TMethod.optimal);

		// -----------------------------------
		// clean assignment from pairs with large distance
		// -----------------------------------
		// Not assigned tracks
		Vector<Integer> not_assigned_tracks = new Vector<>();

		for(int i=0;i<assignment.size();i++)
		{
			if(assignment.get(i)!=-1)
			{
				if(Cost.get(i).get(assignment.get(i))>dist_thres)
				{
					assignment.set(i, -1);
					// Mark unassigned tracks, and increment skipped frames counter,
					// when skipped frames counter will be larger than threshold, track will be deleted.
					not_assigned_tracks.add(i);
				}
			}
			else
			{
				// If track have no assigned detect, then increment skipped frames counter.
				tracks.get(i).skipped_frames++;
			}

		}

		// -----------------------------------
		// If track didn't get detects long time, remove it.
		// -----------------------------------
		for(int i=0;i<tracks.size();i++)
		{
			if(tracks.get(i).skipped_frames>maximum_allowed_skipped_frames)
			{
				tracks.remove(i);
				tracks.clear();
				assignment.clear();
				i--;
			}
		}
		// -----------------------------------
		// Search for unassigned detects
		// -----------------------------------
		Vector<Integer> not_assigned_detections = new Vector<>();
		for(int i=0;i<detections.size();i++)
		{
			int it = assignment.indexOf(i);
			if(it==assignment.lastElement())
			{
				not_assigned_detections.add(i);
			}
		}

		// -----------------------------------
		// and start new tracks for them.
		// -----------------------------------
		if(not_assigned_detections.size()!=0)
		{
			for(int i=0;i<not_assigned_detections.size();i++)
			{
				Tracker tr=new Tracker(detections.get(not_assigned_detections.get(i)),dt,Accel_noise_mag);
				tracks.add(tr);
			}
		}

		// Update Kalman Filters state

		for(int i=0;i<assignment.size();i++)
		{
			// If track updated less than one time, than filter state is not correct.

			tracks.get(i).KF.getPrediction();

			if(assignment.get(i)!=-1) // If we have assigned detect, then update using its coordinates,
			{
				tracks.get(i).skipped_frames=0;
				tracks.get(i).prediction=tracks.get(i).KF.update(detections.get(assignment.get(i)),true);
			}else				  // if not continue using predictions
			{
				tracks.get(i).prediction=tracks.get(i).KF.update(new Point(0,0),false);
			}

			if(tracks.get(i).trace.size()>max_trace_length)
			{
				//tracks.get(i).trace.erase(tracks.get(i).trace.begin(),tracks.get(i).trace.end()-max_trace_length);
				int k = 0;
				while (k < tracks.get(i).trace.size() - max_trace_length) {
					tracks.get(i).trace.remove(tracks.get(i).trace.indexOf(k));
					k++;
				}
			}

			tracks.get(i).trace.add(tracks.get(i).prediction);
			tracks.get(i).KF.setLastResult(tracks.get(i).prediction);
		}
	}

}
