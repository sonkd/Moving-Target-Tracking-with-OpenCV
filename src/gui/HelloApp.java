package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import sonkd.CONFIG;
import sonkd.Tracker;

import java.awt.Font;

/**
 * HelloApp.java
 * TODO: 
 *
 * @author Kim Dinh Son
 * Email:sonkdbk@gmail.com
 */

public class HelloApp {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// System.loadLibrary("opencv_java2410");
	}

	static Mat imag = null;
	static Mat orgin = null;
	public static Tracker tracker;

	private JFrame frame;
	private JPanel panel;
	final JLabel vidpanel1 = new JLabel();
	final JLabel vidpanel2 = new JLabel();
	final JLabel vidpanel3 = new JLabel();
	static int option  = 0;
	boolean inputCapture = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				HelloApp window = new HelloApp();
				window.frame.setVisible(true);
				window.playVideo();
			}
		});

	}

	/**
	 * Create the application.
	 */
	public HelloApp() {		
		initialize();
	}
	
	public void playVideo(){
				
		Mat inFrame = new Mat();
		Mat outbox = new Mat();
		Mat diffFrame = null;
		Vector<Rect> array = new Vector<Rect>();

		BackgroundSubtractorMOG2 mBGSub = Video
				.createBackgroundSubtractorMOG2();

		tracker = new Tracker((float) CONFIG._dt,
				(float) CONFIG._Accel_noise_mag, CONFIG._dist_thres,
				CONFIG._maximum_allowed_skipped_frames,
				CONFIG._max_trace_length);

		// Thread.sleep(1000);
		VideoCapture camera = new VideoCapture();
		if (inputCapture)
			camera.open(0);
		else
			camera.open(CONFIG.filename);
		// VideoCapture camera = new VideoCapture(0);
		int i = 0;

		if (!camera.isOpened()) {
			System.out.print("Can not open Camera, try it later.");
			return;
		}

		while (true) {
			
			switch (option) {
			case 0:
				panel.removeAll();
				panel.revalidate();
				panel.repaint();
				panel.add(vidpanel1);
				break;
			case 2:
				panel.removeAll();
				panel.revalidate();
				panel.repaint();
				panel.add(vidpanel3);
				break;
			case 1:
				panel.removeAll();
				panel.revalidate();
				panel.repaint();
				panel.add(vidpanel2);
				break;
			default:
				break;
			}
			
			if (!camera.read(inFrame))
				break;
			Imgproc.resize(inFrame, inFrame, new Size(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT),
					0., 0., Imgproc.INTER_LINEAR);
			imag = inFrame.clone();
			orgin = inFrame.clone();
			if (i == 0) {
				// jFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
				diffFrame = new Mat(outbox.size(), CvType.CV_8UC1);
				diffFrame = outbox.clone();
			}

			if (i == 1) {
				diffFrame = new Mat(inFrame.size(), CvType.CV_8UC1);
				Functions.processFrame(camera, inFrame, diffFrame, mBGSub);
				inFrame = diffFrame.clone();

				array = Functions.detectionContours(diffFrame);
				// ///////
				Vector<Point> detections = new Vector<>();
				// detections.clear();
				Iterator<Rect> it = array.iterator();
				while (it.hasNext()) {
					Rect obj = it.next();

					int ObjectCenterX = (int) ((obj.tl().x + obj.br().x) / 2);
					int ObjectCenterY = (int) ((obj.tl().y + obj.br().y) / 2);

					Point pt = new Point(ObjectCenterX, ObjectCenterY);
					detections.add(pt);
				}
				// ///////

				if (array.size() > 0) {
					tracker.update(array, detections, imag);
					Iterator<Rect> it3 = array.iterator();
					while (it3.hasNext()) {
						Rect obj = it3.next();

						int ObjectCenterX = (int) ((obj.tl().x + obj.br().x) / 2);
						int ObjectCenterY = (int) ((obj.tl().y + obj.br().y) / 2);

						Point pt = new Point(ObjectCenterX, ObjectCenterY);

						Imgproc.rectangle(imag, obj.br(), obj.tl(), new Scalar(
								0, 255, 0), 2);
						Imgproc.circle(imag, pt, 1, new Scalar(0, 0, 255), 2);
					}
				} else if (array.size() == 0) {
					tracker.updateKalman(imag, detections);
				}

				for (int k = 0; k < tracker.tracks.size(); k++) {
					int traceNum = tracker.tracks.get(k).trace.size();
					if (traceNum > 1) {
						for (int jt = 1; jt < tracker.tracks.get(k).trace
								.size(); jt++) {
							Imgproc.line(imag,
									tracker.tracks.get(k).trace.get(jt - 1),
									tracker.tracks.get(k).trace.get(jt),
									CONFIG.Colors[tracker.tracks.get(k).track_id % 9],
									2, 4, 0);
						}
					}
				}

				Imgproc.putText(imag, "Input: " + CONFIG.filename, new Point(20, 360),
						Core.FONT_HERSHEY_PLAIN, 1, new Scalar(255, 255, 255),
						1);
				Imgproc.putText(imag,
						"So track hien tai: " + tracker.tracks.size()
								+ "     Da xoa: " + tracker.track_removed,
						new Point(20, 50), Core.FONT_HERSHEY_PLAIN, 1,
						new Scalar(255, 255, 255), 1);
			}

			i = 1;

			ImageIcon image = new ImageIcon(Functions.Mat2bufferedImage(imag)); // option 0
			vidpanel1.setIcon(image);
			vidpanel1.repaint();

			ImageIcon image2 = new ImageIcon(Functions.Mat2bufferedImage(inFrame)); // option 2
			vidpanel2.setIcon(image2);
			vidpanel2.repaint();
			
			ImageIcon image3 = new ImageIcon(Functions.Mat2bufferedImage(orgin)); // option 1
			vidpanel3.setIcon(image3);
			vidpanel3.repaint();

		}
	}
	
    public void paintComponent(Graphics g) {
    }

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Moving target tracking");
		frame.setBounds(50, 50, 800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		panel = new JPanel();
		panel.setBackground(Color.GRAY);
		panel.setBounds(11, 39, 593, 371);
		CONFIG.FRAME_HEIGHT = panel.getHeight();
		CONFIG.FRAME_WIDTH = panel.getWidth();
		
		frame.getContentPane().add(panel);
		
		JButton btnOrgin = new JButton("Orgin");
		btnOrgin.setBounds(316, 421, 89, 29);
		frame.getContentPane().add(btnOrgin);
		btnOrgin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				option = 1;
			}
		});
		
		JButton buttonBG = new JButton("BS");
		buttonBG.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				option = 2;
			}
		});
		buttonBG.setBounds(415, 421, 89, 29);
		frame.getContentPane().add(buttonBG);
		
		JButton buttonResulf = new JButton("Result");
		buttonResulf.setBackground(Color.PINK);
		buttonResulf.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				option = 0;			
			}
		});
		buttonResulf.setBounds(514, 421, 89, 29);
		frame.getContentPane().add(buttonResulf);
		
		final JLabel lbFileName = new JLabel(": ");
		lbFileName.setBounds(104, 428, 188, 14);
		frame.getContentPane().add(lbFileName);
		
		JButton buttonOpen = new JButton("Open file");
		buttonOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
            	JFileChooser openFile = new JFileChooser();
                int returnVal = openFile.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					CONFIG.filename = openFile.getSelectedFile().getAbsolutePath();
					lbFileName.setText(lbFileName.getText()+openFile.getName(openFile.getSelectedFile()));
				}
            }
		});
		buttonOpen.setBounds(10, 421, 89, 29);
		frame.getContentPane().add(buttonOpen);
		
		
		final JTextArea textArea = new JTextArea("0.005");
		textArea.setBounds(708, 40, 66, 22);
		frame.getContentPane().add(textArea);
		
		JLabel lblLearningRate = new JLabel("Learning Rate = ");
		lblLearningRate.setBounds(614, 45, 93, 14);
		frame.getContentPane().add(lblLearningRate);
		
		JLabel labelMinBlob = new JLabel("MIN BLOB = ");
		labelMinBlob.setBounds(614, 78, 81, 14);
		frame.getContentPane().add(labelMinBlob);
		
		final JTextArea textArea_1 = new JTextArea("250");
		textArea_1.setBounds(695, 73, 79, 22);
		frame.getContentPane().add(textArea_1);
		
		JLabel labelMaxBlob = new JLabel("MAX BLOB = ");
		labelMaxBlob.setBounds(614, 112, 81, 14);
		frame.getContentPane().add(labelMaxBlob);
		
		final JTextArea textArea_2 = new JTextArea("2000");
		textArea_2.setBounds(695, 107, 79, 22);
		frame.getContentPane().add(textArea_2);
		
		JLabel label = new JLabel("Delta Time = ");
		label.setBounds(614, 144, 103, 14);
		frame.getContentPane().add(label);
		
		final JTextArea textArea_3 = new JTextArea("0.2");
		textArea_3.setBounds(695, 139, 79, 22);
		frame.getContentPane().add(textArea_3);
		
		JLabel label_1 = new JLabel(" Accel noise mag = ");
		label_1.setBounds(614, 177, 115, 14);
		frame.getContentPane().add(label_1);
		
		final JTextArea textArea_4 = new JTextArea("0.5");
		textArea_4.setBounds(728, 172, 46, 22);
		frame.getContentPane().add(textArea_4);
		
		JLabel label_2 = new JLabel("_dist_thres = ");
		label_2.setBounds(614, 211, 92, 14);
		frame.getContentPane().add(label_2);
		
		final JTextArea textArea_5 = new JTextArea("360");
		textArea_5.setBounds(695, 206, 79, 22);
		frame.getContentPane().add(textArea_5);
		
		JLabel label_3 = new JLabel("max skipped frames = ");
		label_3.setBounds(614, 244, 148, 14);
		frame.getContentPane().add(label_3);
		
		final JTextArea textArea_6 = new JTextArea("10");
		textArea_6.setBounds(614, 263, 73, 22);
		frame.getContentPane().add(textArea_6);
		
		JLabel label_4 = new JLabel("max trace length = ");
		label_4.setBounds(614, 296, 121, 14);
		frame.getContentPane().add(label_4);
		
		final JTextArea textArea_7 = new JTextArea("10");
		textArea_7.setBounds(614, 317, 73, 22);
		frame.getContentPane().add(textArea_7);
		
		JButton btnReset = new JButton("Submit");
		btnReset.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnReset.setBounds(650, 350, 89, 40);
		frame.getContentPane().add(btnReset);
		btnReset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				CONFIG.learningRate = Double.parseDouble(textArea.getText());
				textArea.setText(CONFIG.learningRate+"");
				
				CONFIG.MIN_BLOB_AREA = Double.parseDouble(textArea_1.getText());
				textArea_1.setText(CONFIG.MIN_BLOB_AREA+"");
				
				CONFIG.MAX_BLOB_AREA = Double.parseDouble(textArea_2.getText());
				textArea_2.setText(CONFIG.MAX_BLOB_AREA+"");
				
				CONFIG._dt = Double.parseDouble(textArea_3.getText());
				textArea_3.setText(CONFIG._dt+"");
				
				CONFIG._Accel_noise_mag = Double.parseDouble(textArea_4.getText());
				textArea_4.setText(CONFIG._Accel_noise_mag+"");
				
				CONFIG._dist_thres = Double.parseDouble(textArea_5.getText());
				textArea_5.setText(CONFIG._dist_thres+"");
				
				CONFIG._max_trace_length = Integer.parseInt(textArea_6.getText());
				textArea_6.setText(CONFIG._max_trace_length+"");
				
				CONFIG._maximum_allowed_skipped_frames = Integer.parseInt(textArea_7.getText());
				textArea_7.setText(CONFIG._maximum_allowed_skipped_frames+"");
			}
		});
		
		JButton btnStart = new JButton("START / REPLAY");
		btnStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						playVideo();
					}
				});
			}
		});
		btnStart.setBackground(Color.DARK_GRAY);
		btnStart.setForeground(Color.GREEN);
		btnStart.setBounds(11, 10, 131, 23);
		frame.getContentPane().add(btnStart);
		
		JButton btnClose = new JButton("CLOSE");
		btnClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				panel.removeAll();
				panel.repaint();
			}
		});
		btnClose.setForeground(Color.RED);
		btnClose.setBackground(Color.DARK_GRAY);
		btnClose.setBounds(149, 10, 89, 23);
		frame.getContentPane().add(btnClose);
		
		JButton btnCaptureCamera = new JButton("Capture Camera");
		btnCaptureCamera.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				inputCapture = true;
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						playVideo();
					}
				});
			}
		});
		btnCaptureCamera.setBounds(456, 10, 131, 23);
		frame.getContentPane().add(btnCaptureCamera);
	}
}
