//
//  Point.hpp
//  PhotoMaker SDK
//
//  Created by scherepanov on 4/13/16.
//  Copyright © 2016 VisionLabs. All rights reserved.
//

#pragma once

#include <cstdint>

namespace vl {

	/** @addtogroup UtilityGroup Utility
	 *  @{
	 */

	/**
	 * A point in 2D space.
	 */
	struct Point {

		int32_t x; //!< X coordinate.
		int32_t y; //!< Y coordinate.

		/**
		 * Creates a point at (0; 0).
		 */
		Point()
			: x(0), y(0) {}

		/**
		 * Creates a point at given coords.
		 * @param x x coordinate.
		 * @param y y coordinate.
		 */
		Point(
			int32_t x,
			int32_t y)
			: x(x), y(y) {}
	};


	inline bool operator == (const Point& lhs, const Point& rhs) {
		return lhs.x == rhs.x && lhs.y == rhs.y;
	}

	inline bool operator != (const Point& lhs, const Point& rhs) {
		return !(lhs == rhs);
	}

	/** @} */ // end of UtilityGroup
}
