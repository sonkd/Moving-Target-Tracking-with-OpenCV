package samples;

import static org.bytedeco.javacpp.opencv_core.CV_AA;
import static org.bytedeco.javacpp.opencv_core.CV_FILLED;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvAbsDiff;
import static org.bytedeco.javacpp.opencv_core.cvCircle;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvCountNonZero;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetSpatialMoment;
import static org.bytedeco.javacpp.opencv_imgproc.cvMoments;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;

import java.awt.Dimension;

import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_imgproc.CvMoments;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameGrabber;

/**
 * CVMotionDetector.java TODO:
 * 
 * @author Kim Dinh Son Email:sonkdbk@gmail.com
 */

public class CVMotionDetector {
	private static final int MIN_PIXELS = 100;
	// minimum number of non-black pixels needed for COG calculation
	private static final int LOW_THRESHOLD = 64;

	private IplImage prevImg, currImg, diffImg; // grayscale images (diffImg is
												// bi-level)
	private Dimension imDim = null; // image dimensions
	private Point cogPoint = null; // center-of-gravity (COG) coordinate

	public CVMotionDetector(IplImage firstFrame) {
		if (firstFrame == null) {
			System.out.println("No frame to initialize motion detector");
			System.exit(1);
		}

		imDim = new Dimension(firstFrame.width(), firstFrame.height());
		System.out.println("image dimensions: " + imDim);

		prevImg = convertFrame(firstFrame);
		currImg = null;
		diffImg = IplImage.create(prevImg.width(), prevImg.height(),
				IPL_DEPTH_8U, 1);
	} // end of CVMotionDetector()

	private IplImage convertFrame(IplImage img)
	/*
	 * Conversion involves: blurring, converting color to grayscale, and
	 * equalization
	 */
	{
		// blur image to get reduce camera noise
		cvSmooth(img, img);

		// convert to grayscale
		IplImage grayImg = IplImage.create(img.width(), img.height(),
				IPL_DEPTH_8U, 1);
		cvCvtColor(img, grayImg, CV_BGR2GRAY);

		//cvEqualizeHist(grayImg, grayImg); // spread out the grayscale range

		return grayImg;
	} // end of convertFrame()

	public void calcMove(IplImage currFrame)
	// use a new image to update the COG point
	{
		if (currFrame == null) {
			System.out.println("Current frame is null");
			return;
		}

		if (currImg != null) // store old current as the previous image
			prevImg = currImg;

		currImg = convertFrame(currFrame);

		cvAbsDiff(currImg, prevImg, diffImg);
		// calculate absolute difference between curr & previous images;
		// large value means movement; small value means no movement

		/*
		 * threshold to convert grayscale --> two-level binary: small diffs (0
		 * -- LOW_THRESHOLD) --> 0 large diffs (LOW_THRESHOLD+1 -- 255) --> 255
		 */
		cvThreshold(diffImg, diffImg, LOW_THRESHOLD, 255, CV_THRESH_BINARY);

		Point pt = findCOG(diffImg);
		if (pt != null) // only update COG point if there is a new point
			cogPoint = pt;
	} // end of calcMove()

	public Point getCOG() {
		return cogPoint;
	}

	public IplImage getCurrImg() {
		return currImg;
	}

	public IplImage getDiffImg() {
		return diffImg;
	}

	public Dimension getSize() {
		return imDim;
	}

	private Point findCOG(IplImage diffImg)
	/*
	 * If there are enough non-black pixels in the difference image (non-black
	 * means a difference, i.e. movement), then calculate the moments, and use
	 * them to calculate the (x,y) center of the white areas. These values are
	 * returned as a Point object.
	 */
	{
		Point pt = null;

		int numPixels = cvCountNonZero(diffImg); // non-zero (non-black) means
													// motion
		if (numPixels > MIN_PIXELS) {
			CvMoments moments = new CvMoments();
			cvMoments(diffImg, moments, 1); // 1 == treat image as binary
											// (0,255) --> (0,1)
			double m00 = cvGetSpatialMoment(moments, 0, 0);
			double m10 = cvGetSpatialMoment(moments, 1, 0);
			double m01 = cvGetSpatialMoment(moments, 0, 1);

			if (m00 != 0) { // create COG Point
				int xCenter = (int) Math.round(m10 / m00);
				int yCenter = (int) Math.round(m01 / m00);
				pt = new Point(xCenter, yCenter);
			}
		}
		return pt;
	} // end of findCOG()

	// -------------------------- test-rig ----------------------

	public static void main(String[] args) throws Exception {
		System.out.println("Initializing frame grabber...");

		try {
//			OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(CV_CAP_ANY); // from
//																				// webcam
			OpenCVFrameGrabber grabber = new OpenCVFrameGrabber("atrium.avi");
			
			// FFmpegFrameGrabber grabber = new
			// FFmpegFrameGrabber("tesvideo.mp4"); //from video file
			grabber.start();

			CVMotionDetector md = new CVMotionDetector(grabber.grab());

			Dimension imgDim = md.getSize();
			IplImage imgWithCOG = IplImage.create(imgDim.width, imgDim.height,
					IPL_DEPTH_8U, 1);

			// canvases for the image+COG and difference images
			CanvasFrame cogCanvas = new CanvasFrame("Camera + COG");
			cogCanvas.setLocation(0, 0);

			CanvasFrame diffCanvas = new CanvasFrame("Difference");
			diffCanvas.setLocation(imgDim.width + 5, 0);

			// display grayscale+COG and difference images, until a window is
			// closed
			while (cogCanvas.isVisible() && diffCanvas.isVisible()) {
				md.calcMove(grabber.grab());

				// show current grayscale image with COG drawn onto it
				cvCopy(md.getCurrImg(), imgWithCOG);
				Point cogPoint = md.getCOG();
				if (cogPoint != null)
					cvCircle(imgWithCOG, cvPoint(cogPoint.x(), cogPoint.y()),
							8, // radius of circle
							CvScalar.WHITE, CV_FILLED, CV_AA, 0); // line type,
																	// shift
				cogCanvas.showImage(imgWithCOG);

				diffCanvas.showImage(md.getDiffImg()); // show difference image
			}

			grabber.stop();
			cogCanvas.dispose();
			diffCanvas.dispose();
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	} // end of main()

}
