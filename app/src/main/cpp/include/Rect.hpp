//
//  Rect.hpp
//  PhotoMaker SDK
//
//  Created by scherepanov on 4/13/16.
//  Copyright © 2016 VisionLabs. All rights reserved.
//

#pragma once

#include "Point.hpp"

namespace vl {

	/**
	 * An axis-aligned rectangle in 2D space.
	 */
	struct Rect {

		Point topLeft;      //!< Top-left corner.
		Point bottomRight;  //!< Bottom-right corner.

		/**
		 * Creates an empty rect.
		 * The default rect is invsalid since it has zero area.
		 */
		Rect() {}

		/**
		 * Creates a rect from a given origin and given dimensions.
		 * @param topLeft top-left corner coords.
		 * @param width rect width.
		 * @param height rect height.
		 */
		Rect(
			const Point& topLeft,
			int32_t width,
			int32_t height)
			: topLeft(topLeft)
			, bottomRight(
				topLeft.x + width,
				topLeft.y + height)
		{}

		/**
		 * Creates a rect from given corner points.
		 * @param topLeft top-left corner coords.
		 * @param bottomRight bottom-right corner coords.
		 */
		Rect(
			const Point& topLeft,
			const Point& bottomRight)
			: topLeft(topLeft)
			, bottomRight(bottomRight)
		{}

		//! Get top-left corner y coordinate.
		//! @return the coordinate.
		int32_t getTop() const { return topLeft.y; }

		//! Get top-left corner x coordinate.
		//! @return the coordinate.
		int32_t getLeft() const { return topLeft.x; }

		//! Get bottom-right corner x coordinate.
		//! @return the coordinate.
		int32_t getRight() const { return bottomRight.y; }

		//! Get bottom-right corner y coordinate.
		//! @return the coordinate.
		int32_t getBottom() const { return bottomRight.x; }

		/**
		 * Get rect width.
		 * @note Doesn't check if th rect is valid and may return negative values
		 * if the rect top-left/bottom-right coordinates are unreasonable.
		 * @return rect width.
		 */
		int32_t getWidth() const { return bottomRight.x - topLeft.x; }

		/**
		 * Get rect width.
		 * @note Doesn't check if th rect is valid and may return negative values
		 * if the rect top-left/bottom-right coordinates are unreasonable.
		 * @return rect width.
		 */
		int32_t getHeight() const { return bottomRight.y - topLeft.y; }

		/**
		 * Computes rect area.
		 * @return area or 0 if the rect is invalid.
		 */
		int32_t getArea() const {
			return isValid() ? getWidth() * getHeight() : 0;
		}

		/**
		 * Checks if the rect is valid, i.e. width and heigth are both > 0.
		 * @return true if the rect is valid, false otherwise.
		 */
		bool isValid() const { return getWidth() > 0 && getHeight() > 0; }
	};

	inline bool operator == (const Rect& lhs, const Rect& rhs) {
		return lhs.topLeft == rhs.topLeft && lhs.bottomRight == rhs.bottomRight;
	}

	inline bool operator != (const Rect& lhs, const Rect& rhs) {
		return !(lhs == rhs);
	}
}
