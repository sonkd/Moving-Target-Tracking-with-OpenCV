package sonkd;

import java.awt.Toolkit;

import org.opencv.core.Scalar;

/**
 * CONFIG.java
 * TODO: 
 *
 * @author Kim Dinh Son
 * Email:sonkdbk@gmail.com
 */

public class CONFIG {
	// static String filename =
	// "H:/VIDEO/Footage/Crowd_PETS09/S2/L2/Time_14-55/View_001/frame_%04d.jpg");
	// static String filename = "H:/VIDEO/Footage/Project Final/768x576.avi";
	// static String filename = "H:/VIDEO/Footage/Project Final/MatchSoccer.wmv";
	// static String filename = "H:/VIDEO/Footage/Project Final/SingleTracking.mp4";
	// static String filename = "H:/VIDEO/Footage/Project Final/FroggerHighway.mp4";
	// static String filename = "H:/VIDEO/Footage/Project Final/street.mov";
	public static String filename = "atrium.avi";
	
	public static int FRAME_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
	public static int FRAME_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height / 2;
	
	public static double MIN_BLOB_AREA = 250;
	public static double MAX_BLOB_AREA = 3000;
	
	public static Scalar Colors[] = { new Scalar(255, 0, 0), new Scalar(0, 255, 0),
		new Scalar(0, 0, 255), new Scalar(255, 255, 0),
		new Scalar(0, 255, 255), new Scalar(255, 0, 255),
		new Scalar(255, 127, 255), new Scalar(127, 0, 255),
		new Scalar(127, 0, 127) };
	
	public static double learningRate = 0.005;
	
	public static double _dt = 0.2;
	public static double _Accel_noise_mag = 0.5;
	public static double _dist_thres = 360;
	public static int _maximum_allowed_skipped_frames = 10;
	public static int _max_trace_length = 10;
}
