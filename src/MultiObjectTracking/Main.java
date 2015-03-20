package MultiObjectTracking;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;

/**
 * Main.java TODO:
 * 
 * @author Kim Dinh Son Email:sonkdbk@gmail.com
 */

public class Main {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// System.loadLibrary("opencv_java2410");
	}

	static Mat imag;
	static Kalman KF;

	public static void main(String[] args) throws InterruptedException {
		JFrame jFrame = new JFrame("MULTIPLE-TARGET TRACKING");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel vidpanel = new JLabel();
		jFrame.setContentPane(vidpanel);
		jFrame.setSize(640, 480);
		jFrame.setVisible(true);

		// ////////////////////////////////////////////////////////
		JFrame jFrame2 = new JFrame("MULTIPLE-TARGET TRACKING");
		jFrame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel vidpanel2 = new JLabel();
		jFrame2.setContentPane(vidpanel2);
		jFrame2.setSize(640, 480);
		jFrame2.setVisible(true);
		// ////////////////////////////////////////////////////////

		Mat frame = new Mat();
		Mat outbox = new Mat();

		Mat diffFrame = null;

		ArrayList<Rect> array = new ArrayList<Rect>();

		BackgroundSubtractorMOG2 mBGSub = new BackgroundSubtractorMOG2();

		// VideoCapture camera = new
		// VideoCapture(VideoCapture.class.getResource(
		// "/atrium.avi").getPath());
		// Thread.sleep(1000);
		VideoCapture camera = new VideoCapture();
		camera.open("atrium.avi");
		// VideoCapture camera = new VideoCapture(0);
		//Size sz = new Size(640, 480);
		int i = 0;

		if (!camera.isOpened()) {
			System.out.print("Can not open Camera, try it later.");
			return;
		}

		// initial KalmanFilter
		KF = new Kalman();
		KF.init();

		while (true) {
			if (camera.read(frame)) {
				//Imgproc.resize(frame, frame, sz);
				imag = frame.clone();;

				if (i == 0) {
					jFrame.setSize(frame.width(), frame.height());
					diffFrame = new Mat(outbox.size(), CvType.CV_8UC1);
					diffFrame = outbox.clone();
				}

				if (i == 1) {

					// Core.subtract(outbox, temponFrame, diffFrame);
					diffFrame = new Mat(frame.size(), CvType.CV_8UC1);
					processFrame(camera, frame, frame, mBGSub);

					Imgproc.threshold(frame, diffFrame, 127, 255,
							Imgproc.THRESH_BINARY_INV);
				

					// ----------Process noise-------------//
//					Imgproc.GaussianBlur(diffFrame, diffFrame, new Size(3, 3),
//							0);
//
//					Imgproc.erode(diffFrame, diffFrame, Imgproc.getStructuringElement(
//							Imgproc.MORPH_RECT, new Size(8, 8)));
//					Imgproc.dilate(diffFrame, diffFrame, Imgproc.getStructuringElement(
//							Imgproc.MORPH_RECT, new Size(8, 8)));
					// ----------Process noise-------------//

					array = detectionContours(diffFrame);
					array.remove(array.size() - 1); // ?
					if (array.size() > 0) {
						Iterator<Rect> it2 = array.iterator();
						while (it2.hasNext()) {
							Rect obj = it2.next();
							Core.rectangle(imag, obj.br(), obj.tl(),
									new Scalar(0, 255, 0), 2);

							int ObjectCenterX = (int) ((obj.tl().x + obj.br().x) / 2);
							int ObjectCenterY = (int) ((obj.tl().y + obj.br().y) / 2);

							Point state = new Point((float) ObjectCenterX,
									(float) ObjectCenterY);
							Core.circle(imag, state, 1, new Scalar(0, 0, 255),
									2);

							// state
//							Mat update = KF.predict();
//							update = KF.correct();
//							// Update the center of the object
//							// state = new Point(update);
//							System.out.println(update.dump());
//							Core.circle(imag, state, 1, new Scalar(255, 255, 255),
//									2);

						}
					}
				}

				i = 1;

				ImageIcon image = new ImageIcon(Mat2bufferedImage(imag));
				vidpanel.setIcon(image);
				vidpanel.repaint();
				// temponFrame = outerBox.clone();

				ImageIcon image2 = new ImageIcon(Mat2bufferedImage(frame));
				vidpanel2.setIcon(image2);
				vidpanel2.repaint();
			} else {

			}
		}

	}

	// background substraction
	protected static void processFrame(VideoCapture capture, Mat mRgba,
			Mat mFGMask, BackgroundSubtractorMOG2 mBGSub) {
		capture.retrieve(mRgba, Imgproc.COLOR_BGR2RGB);
		// GREY_FRAME also works and exhibits better performance
		mBGSub.apply(mRgba, mFGMask, 0.001);
		// Imgproc.cvtColor(mFGMask, mRgba, Imgproc.COLOR_GRAY2BGRA, 0);
		Mat openElem = Imgproc
				.getStructuringElement(Imgproc.MORPH_RECT,
						new Size(5, 5), new Point(2, 2));
		Mat closeElem = Imgproc
				.getStructuringElement(Imgproc.MORPH_RECT,
						new Size(7, 7), new Point(3, 3));
		// pixel >127 chuyen ve 1
		// Imgproc.adaptiveThreshold(diffFrame, diffFrame, 255,
		// Imgproc.ADAPTIVE_THRESH_MEAN_C,
		// Imgproc.THRESH_BINARY_INV, 7, 127);

		Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN,
				openElem);
		Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_CLOSE,
				closeElem);
	}

	private static BufferedImage Mat2bufferedImage(Mat image) {
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(".jpg", image, bytemat);
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

	public static ArrayList<Rect> detectionContours(Mat outmat) {
		Mat v = new Mat();
		Mat vv = outmat.clone();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(vv, contours, v, Imgproc.RETR_LIST,
				Imgproc.CHAIN_APPROX_SIMPLE);

		double maxArea = 500;
		int maxAreaIdx = -1;
		Rect r = null;
		ArrayList<Rect> rect_array = new ArrayList<Rect>();

		for (int idx = 0; idx < contours.size(); idx++) {
			Mat contour = contours.get(idx);
			double contourarea = Imgproc.contourArea(contour);
			if (contourarea > maxArea) {
				// maxArea = contourarea;
				maxAreaIdx = idx;
				r = Imgproc.boundingRect(contours.get(maxAreaIdx));
				rect_array.add(r);
				// Imgproc.drawContours(imag, contours, maxAreaIdx, new
				// Scalar(0,
				// 0, 255));
			}

		}

		v.release();
		return rect_array;
	}

}
