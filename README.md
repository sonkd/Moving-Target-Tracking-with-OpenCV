# Moving Target Tracking with OpenCV

A Java application that tracks moving objects in video streams using **OpenCV** and a **Kalman Filter**. The project focuses on real-time motion detection and target tracking from video or camera feeds—common use cases in computer vision and surveillance systems.

---

## Overview

This project demonstrates how to:
- Capture and process video frames with OpenCV (Java)
- Detect and track moving targets across frames
- Use a Kalman Filter to predict motion and smooth noisy detections

It is suitable for learning and experimentation in **computer vision**, **object tracking**, and **motion analysis**.

---

## Key Technologies

- **Java (JDK 8+)** – Core programming language  
- **OpenCV (Java API)** – Image and video processing  
- **Kalman Filter** – Motion prediction and state estimation  
- **Maven / Gradle (optional)** – Dependency management

---

## Setup

1. **Install Java**
   - Ensure JDK 8 or later is installed.

2. **Install OpenCV**
   - Download OpenCV (e.g., 4.x) from https://opencv.org
   - Extract it to a local directory.

3. **Configure OpenCV for Java**
   - Add `opencv-<version>.jar` (found in `build/java/`) to your project classpath.
   - Set the native library path to `build/java/x64` (or `x86`, depending on OS).

4. **Load OpenCV Native Library**
   ```java
   System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

5. **Build the Project**
- Compile using your IDE or command line (javac).
- If using Maven/Gradle, include the OpenCV dependency accordingly.

---

## Usage

Run the main tracking class after setting the native library path:
```bash
   java -Djava.library.path="/path/to/opencv/build/java/x64" \
     -cp .:opencv-<version>.jar com.example.TrackerMain
```

The application will:
- Open a camera stream or video file
- Detect a moving target
- Track its motion frame-by-frame using a Kalman Filter
- Display the tracking result in real time

---

## **Applications**

- Computer vision research & learning
- Motion detection from video
- Camera-based tracking systems
- Surveillance and monitoring prototypes

---

## **Topics**
- Computer Vision
- Object Tracking
- Motion Detection
- Kalman Filter
- Surveillance Systems
