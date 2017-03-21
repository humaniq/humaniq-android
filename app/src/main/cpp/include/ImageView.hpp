//
//  ImageView.hpp
//  PhotoMaker SDK
//
//  Created by scherepanov on 4/13/16.
//  Copyright © 2016 VisionLabs. All rights reserved.
//

#pragma once

#include "Def.hpp"
#include "Rect.hpp"

namespace vl {

	/**
	 * Supported image formats and color spaces enumeration.
	 */
	enum ImageFormat {

		//! 32 bit per pixel; color channel order is RGBA.
		FMT_R8G8B8A8,

		//! 8 bit per pixel; luminance only. Corresponds to Y plane of YUV format.
		FMT_Y8,

		//! 12 bit per pixel; semi-planar YUV (aka NV21) format, native to Android.
		//! The first (width * height) bytes basically make an Y plane.
		FMT_YUV420sp
	};

	/**
	 * Holds pointer to raw pixel data as well as image dimensions and fomat.
	 * Does NOT imply data ownership and/or copying.
	 */
	struct PM_API ImageView {

		void* pixels;	//!< Raw pixel data.

		int32_t width;  //!< Image width.
		int32_t height; //!< Image height.

		int32_t format; //!< Image data format. @see ImageFormat.

		/**
		 * Constructs a default image view.
		 * The default view does not represent an image and thus is invalid.
		 */
		ImageView();

		/**
		 * Constructs an image view from memory.
		 * @param pixels image pixels.
		 * @param width image width.
		 * @param height image height.
		 * @param format image format.
		 */
		ImageView(
			void* pixels,
			int32_t width,
			int32_t height,
			int32_t format = FMT_R8G8B8A8);

		/**
		 * Checks whether a view is associated with a non-empty image.
		 * @return true if pixels is not null and width and height are both > 0.
		 */
		bool isValid() const {
			return pixels && width > 0 && height > 0;
		}

		/**
		 * Get image bounding rect.
		 * The returned rect origin is (0; 0) and dimensions are equal to the
		 * ones of the image.
		 * @return bounding rect.
		 */
		Rect getRect() const { return Rect(Point(0, 0), width, height); }

		/**
		 * Copy one ImageView's data into another, doing conversion if needed.
		 * @param an ImageView over the data to be substituted.
		 */
		void copyTo(ImageView& destination);
	};


	/**
	 * Get number of bytes per pixel for a given image format.
	 * @param format data format. @see ImageFormat.
	 * @return number of bytes per pixel.
	 */
	inline int32_t getNumBytesPerPixel(int32_t format) {
		switch (format) {
		case FMT_R8G8B8A8:
			return 4;
		case FMT_Y8:
			return 1;
		default:
			return 0;
		}
	}

	/**
	 * Get number of bytes per image row for a given image format and row length.
	 * @param format data format. @see ImageFormat.
	 * @param size image row length in pixels.
	 * @return number of bytes per row.
	 */
	inline int32_t getNumBytesPerImageRow(int32_t format, int32_t size) {
		if (format == FMT_YUV420sp) {
			return size * 3 / 2;
		} else {
			return getNumBytesPerPixel(format) * size;
		}
	}

	/**
	 * Get number of bytes per image view.
	 * @param view image view.
	 * @return number of bytes per view.
	 */
	inline int32_t getNumBytesPerImageView(const ImageView& view) {
		return getNumBytesPerImageRow(view.format, view.width) * view.height;
	}
}
