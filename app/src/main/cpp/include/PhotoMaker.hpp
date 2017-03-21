//
//  PhotoMaker.hpp
//  PhotoMaker SDK
//
//  Created by scherepanov on 4/13/16.
//  Copyright © 2016 VisionLabs. All rights reserved.
//

#pragma once

#include "ImageView.hpp"

namespace vl {

	/** @addtogroup PhotoMakerGroup PhotoMaker
	 *  @{
	 */

#ifdef WITH_LIVENESS
	/**
	 * enum for eyes-liveness result
	 */
	enum EYES_RESULT{
		NULL_EYES = 0, // no detection available
		CLOSED_EYES = 1, // eyes are closed
		OPENED_EYES = 2, // eye are opened
		UNKNOWN_EYES = 3 // we don't shure, eyes are opened or closed
	};
#endif
	/**
	 * Photo maker.
	 * Tracks a single face in a video stream and tries to pick the best shot.
	 */
	struct PM_API PhotoMaker {

		/**
		 * Constructs a PhotoMaker instance with default parameter values.
		 * @note The instance must be initialized before use; @see load().
		 */
		PhotoMaker();
		~PhotoMaker();

		/**
		 * Check if this PhotoMaker instance was successfully initialized.
		 * @see load().
		 * @note true if the instance was initialized, false otherwise.
		 */
		bool isLoaded() const;

		/**
		 * Initializes internal detectors and trackers for use.
		 * Should be called once for each PhotoMaker instance.
		 * @note The specified path prefix should not contain trailig slashes.
		 * @see isLoaded().
		 * @param path path prefix to the model files.
		 * @return true if succeeded, false otherwise.
		 */
		bool load(const char* path);

		/**
		 * Reset tracking.
		 * Call this when need to start tracking a new face.
		 */
		void reset();

		/**
		 * Check whether a face detection is available.
		 * @return true if a face was found (via detection OR tracking) and it's
		 * coordinates can be retrieved; false otherwise.
		 */
		bool haveFaceDetection() const;

		/**
		 * Check whether a face detection is obtained via tracking.
		 * @return true if the detection is predicted.
		 */
		bool faceDetectionIsPredicted() const;

		/**
		 * Get current face detection coordinates.
		 * @note This function outputs valid result only if a detection is
		 * available (@see haveFaceDetection()); otherwise invalid rect is returned.
		 * @return face detection coordinates.
		 */
		Rect getFaceDetection() const;
		
		/**
		 * Get current face for demonstration (w/out jitter)
		 * @note Otputs valid result only if a detection is avaliable
		 * otherwise invalid rect returned
		 * @return demonstration face detection coordinates
		 */
		Rect getSmoothedFaceDetection() const;

		/**
		 * Try detect a face on the video frame.
		 * This function will try to track a previous detection (if any) if
		 * face detection is not possible with the provided frame.
		 * This function will update best shot if appropriate.
		 * @note You should call update() once after each new frame is 
		 * submitted to update results.
		 * @see submit(). 
		 */
		void update();

		/**
		 * Submit a video frame for processing.
		 * @param frame the frame.
		 */
		void submit(const ImageView& frame) const;

		/**
		 * Get best shot image.
		 * The returned image matrix may be empty if no best shot was made.
		 * @see haveBestShot() to determine if the best shot is available.
		 * @return best shot.
		 */
		ImageView getBestShot() const;

		/**
		 * Get best frame.
		 * The returned image matrix may be empty if no best frame was made.
		 * @see haveBestShot() to determine if the best shot and best frame are available.
		 * @see setSaveBestFrameEnabled() to enable storing entire frame from which the best shot was made.
		 * @return best frame.
		 */
		ImageView getBestFrame() const;

		/**
		 * Get best frame detection
		 * @see haveBestShot() to determine if the best shot and best frame are available.
		 * @see setSaveBestFrameEnabled() to enable storing entire frame from which the best shot was made.
		 * @return best frame detection in frame coordinates
		 */
		Rect getBestFrameDetection() const;
		
		/**
		 * Enable storing entire frame from which the best shot was made.
		 * @note Affects getBestFrame() and getBestFrameDetection(); enable saving to make mentioned functions 
		 * work. By default is disabled.
		 * @param value true - save full frames; false - dont's save.
		 */
		void setSaveBestFrameEnabled(bool value);
		
		/**
		 * Check if storing entire frame from which the best shot was made is enabled.
		 * @note By default is disabled.
		 * @return true is enabled; false if disabled.
		 */
		bool getSaveBestFrameEnabled() const;

		/**
		 * Check whether the best shot is available.
		 * @return true if an image is available; false otherwise.
		 */
		bool haveBestShot() const;

		/**
		 * Get current processed frame number.
		 * @note returns reasonable results if update() was called at least once.
		 * @return current processed frame number.
		 */
		int32_t getCurrentFrameNumber() const;

		/**
		 * Get best shot frame number.
		 * @note returns reasonable results if haveBestShot() returns true.
		 * @return best shot frame number.
		 */
		int32_t getBestShotFrameNumber() const;

		/**
		 * Get number of last frame when a successful wace detection was made.
		 * @note returns reasonable results if faceDetectionIsPredicted() returns true.
		 * @return frame number.
		 */
		int32_t getLastFrameWithDetectionNumber() const;

		/**
		 * Set scale factor for input video frames.
		 * Used to downscale input image to improve processing speed.
		 * @param factor the scale factor; shoud be within (0, 1] range.
		 */
		void setFrameScaleFactor(float factor);

		/**
		 * Get current scale factor for input video frames.
		 * @return the scale factor.
		 */
		float getFrameScaleFactor() const;

		/**
		 * Set maximum number of frames to keep track of the face when good face
		 * detection is not available.
		 * Tracking is used to predict fac position in case of obscurrance and/or
		 * rapid movements of the face.
		 * @param number number of frmaes to track.
		 */
		void setMaxNumberOfFramesWithoutDetection(int32_t number);

		/**
		 * Get maximum number of frames to keep track of the face.
		 * @return the number of frames.
		 */
		int32_t getMaxNumberOfFramesWithoutDetection() const;

		/**
		 * Tell the maker to stop shooting in search for the best face when at
		 * least one face scored above a certain threshold. If set to true, the
		 * maker will stop, while otherwise it will continue taking photos and
		 * constantly refine results.
		 * @see setBestShotScoreThreshold().
		 * @param value new value.
		 */
		void setStopAfterBestShot(bool value);

		/**
		 * Check if the wake should stop after encountering the first best shot.
		 * @return true if the make should stop, false otherwise.
		 */
		bool getStopAfterBestShot() const;

		/**
		 * Set minimum best shot detection score value.
		 * @param value score value.
		 */
		void setBestShotScoreThreshold(float value);

		/**
		 * Get minimum best shot detection score value.
		 * @return minimum score value.
		 */
		float getBestShotScoreThreshold() const;

		/**
		 * Set maximum movement value to prevent blurred shots.
		 * @param value movement threshold.
		 */
		void setMovementThreshold(float value);

		/**
		 * Check if the head movement is within the movement threshold.
		 * Allows to determine if the face is steady, thus eligible for
		 * the best shot procedure.
		 * Large movements should be avoided to prevent motion blurring
		 * of the best shot photo.
		 * @note Only makes sense if haveFaceDetection() returns true.
		 * @return true if the head is steady.
		 */
		bool isSlowMovement() const;

		/**
		 * Get the most recent movement estimation.
		 * @note Only makes sense if haveFaceDetection() returns true.
		 * @return current movement value.
		 */
		float getMovementValue() const;

		/**
		 * Set portrait  maximum height (for best shot).
		 * @note Default is 320.
		 * @param value max height in range [1, MAX_INT].
		 */
		void setPortraitMaxHeight(int value);

		/**
		 * Get portrait (for best shot) maximum height.
		 * @note Default is 320.
		 * @return portrait max height value.
		 */
		int getPortraitMaxHeight() const;

		/**
		 * Set maximum head rotation about all three coordiante axes.
		 * If head rotation angles do not exceed the given limit, the 
		 * head pose is considered frontal, thus eligible for the best
		 * shot procedure.
		 * @note Absolute value should be specified; this will allow
		 * rotations within [-value, value] range.
		 * @note Default is 10 degrees.
		 * @param value rotation limit (in degrees).
		 */
		void setRotationThreshold(float value);

		/**
		 * Get head rotation limit.
		 * @return rotation limit value (in degrees).
		 */
		float getRotationThreshold() const;

		/**
		 * Get the most recent rotation estimation.
		 * @note Only makes sense if haveFaceDetection() returns true.
		 * @return true if the head is within rotation limits.
		 */
		bool isFrontalPose() const;

		/**
		 * Set face detector min confidence.
		 * This controls probabilty to find any face on frame
		 * @note The less value is, the better probabilty to find face, but less accuracy.
		 * @note The default value is 0.04.
		 * @param score detector confidence value in range [0, 1].
		 */
		void setMinDetectorConfidence(float score) const;

		/**
		 * Get face detector min confidence.
		 * @return confidence score value in range [0, 1].
		 */
		float getMinDetectorConfidence() const;

		/**
		 * Set maximum face height to frame height ratio.
		 * This controls maximum size of a detected face, allowing to
		 * ignore smaller faces and preserve enough context around bigger
		 * ones.
		 * @note The less difference between face size and frame size is,
		 * the better is detection performance.
		 * @note The default ratio is 0.8.
		 * @param scale value in range (0, 1].
		 */
		void setMaxFaceScaleFactor(float scale);

		/**
		 * Set minimum face height to frame height ratio.
		 * This controls maximum size of a detected face, allowing to
		 * ignore smaller faces and preserve enough context around bigger
		 * ones.
		 * Gerenally smaller faces are less suitable for recognition purposes
		 * due to the lack of detail, despite they may have bigger detection
		 * scores sometimes.
		 * @note The less difference between face size and frame size is,
		 * the better is detection performance.
		 * @note The default ratio is 0.2.
		 * @param scale value in range (0, 1].
		 */
		void setMinFaceScaleFactor(float scale);

		/**
		 * Get  minimum face height to frame height ratio.
		 * @return minimum ratio value.
		 */
		float getMinFaceScaleFactor() const;

		/**
		 * Get  maximum face height to frame height ratio.
		 * @return maximum ratio value.
		 */
		float getMaxFaceScaleFactor() const;

#ifdef WITH_LIVENESS
		/**
		 * Get current eyes state
		 * @return NULL_EYES if no detection available now or detection is predicted
		 * @return OPENED_EYES if eyes are opened
		 * @return CLOSED_EYES if eyes are closed
		 * @return UNKNOWN_EYES if we not shure in both eyes
		 */
		EYES_RESULT getEyesState() const; 
#endif

#ifndef DOXYGEN_SHOULD_SKIP_THIS
	private:
		struct Impl; Impl *self;
#endif
	};
	/** @} */ // end of PhotoMakerGroup
}
