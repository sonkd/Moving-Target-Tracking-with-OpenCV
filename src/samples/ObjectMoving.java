package samples;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.VideoCapture;

class Panel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;

	// Create a constructor method
	public Panel() {
		super();
	}

	private BufferedImage getimage() {
		return image;
	}

	public void setimage(BufferedImage newimage) {
		image = newimage;
		return;
	}

	public void setimagewithMat(Mat newimage) {
		image = this.matToBufferedImage(newimage);
		return;
	}

	/**
	 * Converts/writes a Mat into a BufferedImage.
	 * 
	 * @param matrix
	 *            Mat of type CV_8UC3 or CV_8UC1
	 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
	 */
	public BufferedImage matToBufferedImage(Mat matrix) {
		int cols = matrix.cols();
		int rows = matrix.rows();
		int elemSize = (int) matrix.elemSize();
		byte[] data = new byte[cols * rows * elemSize];
		int type;
		matrix.get(0, 0, data);
		switch (matrix.channels()) {
		case 1:
			type = BufferedImage.TYPE_BYTE_GRAY;
			break;
		case 3:
			type = BufferedImage.TYPE_3BYTE_BGR;
			// bgr to rgb
			byte b;
			for (int i = 0; i < data.length; i = i + 3) {
				b = data[i];
				data[i] = data[i + 2];
				data[i + 2] = b;
			}
			break;
		default:
			return null;
		}
		BufferedImage image2 = new BufferedImage(cols, rows, type);
		image2.getRaster().setDataElements(0, 0, cols, rows, data);
		return image2;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// BufferedImage temp=new BufferedImage(640, 480,
		// BufferedImage.TYPE_3BYTE_BGR);
		BufferedImage temp = getimage();
		// Graphics2D g2 = (Graphics2D)g;
		if (temp != null)
			g.drawImage(temp, 10, 10, temp.getWidth(), temp.getHeight(), this);
	}
}

public class ObjectMoving {
	static Mat imag = null;
	public static void main(String arg[]) {
		//System.loadLibrary("opencv_java2410");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		trackGreen();
		return;
	}
	
	private static void trackGreen() {
		JFrame frame1 = new JFrame("Camera");
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.setSize(640, 480);
		frame1.setBounds(0, 0, frame1.getWidth(), frame1.getHeight());
		Panel panel1 = new Panel();
		frame1.setContentPane(panel1);
		frame1.setVisible(true);
		
		// -- 2. Read the video stream
		VideoCapture capture = new VideoCapture(0);
		// capture.set(15, 30);
		Mat webcam_image = new Mat();
		Mat outerBox = new Mat();
		Mat diff_frame = null;
		Mat tempon_frame = null;
		ArrayList<Rect> array = new ArrayList<Rect>();
		
		Mat thresholded = new Mat();
		Mat thresholded2 = new Mat();
		capture.read(webcam_image);
		frame1.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
		
		Mat array255 = new Mat(webcam_image.height(), webcam_image.width(),
				CvType.CV_8UC1);
		array255.setTo(new Scalar(255));	
		
		Scalar hsv_min = new Scalar(0, 50, 50, 0);
		Scalar hsv_max = new Scalar(6, 255, 255, 0);
		Scalar hsv_min2 = new Scalar(175, 50, 50, 0);
		Scalar hsv_max2 = new Scalar(179, 255, 255, 0);
		
		if (capture.isOpened()) {
			int i = 0;
			while (true) {
				capture.read(webcam_image);
				if (!webcam_image.empty()) {
					imag = webcam_image.clone();
					// One way to select a range of colors by Hue
					
					outerBox = new Mat(webcam_image.size(), CvType.CV_8UC1);
					Imgproc.cvtColor(webcam_image, outerBox, Imgproc.COLOR_BGR2GRAY);
					Core.inRange(outerBox, hsv_min, hsv_max, thresholded);
					Core.inRange(outerBox, hsv_min2, hsv_max2, thresholded2);
					Core.bitwise_or(thresholded, thresholded2, thresholded);
					Imgproc.erode(thresholded, thresholded, Imgproc
							.getStructuringElement(Imgproc.MORPH_RECT,
									new Size(8, 8)));
					Imgproc.dilate(thresholded, thresholded, Imgproc
							.getStructuringElement(Imgproc.MORPH_RECT,
									new Size(8, 8)));
					
					Imgproc.GaussianBlur(outerBox, outerBox, new Size(3, 3), 0);
					
					if (i == 0) {
						frame1.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
						diff_frame = new Mat(outerBox.size(), CvType.CV_8UC1);
						tempon_frame = new Mat(outerBox.size(), CvType.CV_8UC1);
						diff_frame = outerBox.clone();
					}
					
					if (i == 1) {
						Core.subtract(outerBox, tempon_frame, diff_frame);
						Imgproc.adaptiveThreshold(diff_frame, diff_frame, 255,
								Imgproc.ADAPTIVE_THRESH_MEAN_C,
								Imgproc.THRESH_BINARY_INV, 5, 2);
						array = detection_contours(diff_frame);
						if (array.size() > 0) {

							Iterator<Rect> it2 = array.iterator();
							while (it2.hasNext()) {
								Rect obj = it2.next();
								Core.rectangle(imag, obj.br(), obj.tl(),
										new Scalar(0, 255, 0), 1);
							}

						}
					}

					i = 1;

					panel1.setimagewithMat(webcam_image);
					frame1.repaint();

				} else {
					System.out.println(" --(!) No captured frame -- Break!");
					break;
				}
			}
		}

	}
	
	public static ArrayList<Rect> detection_contours(Mat outmat) {
		Mat v = new Mat();
		Mat vv = outmat.clone();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(vv, contours, v, Imgproc.RETR_LIST,
				Imgproc.CHAIN_APPROX_SIMPLE);

		double maxArea = 100;
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
				Imgproc.drawContours(imag, contours, maxAreaIdx, new Scalar(0,
						0, 255));
			}

		}

		v.release();

		return rect_array;

	}
}