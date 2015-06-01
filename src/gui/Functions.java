package gui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.videoio.VideoCapture;

import sonkd.CONFIG;

/**
 * Functions.java
 * TODO: 
 *
 * @author Kim Dinh Son
 * Email:sonkdbk@gmail.com
 */

public class Functions {
	// background substractionMOG2
	protected static void processFrame(VideoCapture capture, Mat mRgba,
			Mat mFGMask, BackgroundSubtractorMOG2 mBGSub) {
		// GREY_FRAME also works and exhibits better performance
		mBGSub.apply(mRgba, mFGMask, CONFIG.learningRate);
		Imgproc.cvtColor(mFGMask, mRgba, Imgproc.COLOR_GRAY2BGRA, 0);
		Mat erode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(
				8, 8));
		Mat dilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
				new Size(8, 8));

		Mat openElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
				new Size(3, 3), new Point(1, 1));
		Mat closeElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
				new Size(7, 7), new Point(3, 3));

		Imgproc.threshold(mFGMask, mFGMask, 127, 255, Imgproc.THRESH_BINARY);
		Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, erode);
		Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, dilate);
		Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, openElem);
		Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_CLOSE, closeElem);
	}

	public static BufferedImage Mat2bufferedImage(Mat image) {
		MatOfByte bytemat = new MatOfByte();
		Imgcodecs.imencode(".jpg", image, bytemat);
		byte[] bytes = bytemat.toArray();
		InputStream in = new ByteArrayInputStream(bytes);
		BufferedImage img = null;
		try {
			img = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
	}

	public static Vector<Rect> detectionContours(Mat outmat) {
		Mat v = new Mat();
		Mat vv = outmat.clone();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(vv, contours, v, Imgproc.RETR_LIST,
				Imgproc.CHAIN_APPROX_SIMPLE);

		int maxAreaIdx = -1;
		Rect r = null;
		Vector<Rect> rect_array = new Vector<Rect>();

		for (int idx = 0; idx < contours.size(); idx++) {
			Mat contour = contours.get(idx);
			double contourarea = Imgproc.contourArea(contour);
			if (contourarea > CONFIG.MIN_BLOB_AREA && contourarea < CONFIG.MAX_BLOB_AREA) {
				// MIN_BLOB_AREA = contourarea;
				maxAreaIdx = idx;
				r = Imgproc.boundingRect(contours.get(maxAreaIdx));
				rect_array.add(r);
				// Imgproc.drawContours(imag, contours, maxAreaIdx, new
				// Scalar(255, 255, 255));
			}

		}

		v.release();
		return rect_array;
	}
}
