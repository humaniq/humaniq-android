//
//  Def.hpp
//  PhotoMaker SDK
//
//  Created by scherepanov on 4/13/16.
//  Copyright © 2016 VisionLabs. All rights reserved.
//

#pragma once

#if defined (_WIN32) 
	#if defined (PM_DLL_EXPORTS)
		#define PM_API __declspec(dllexport)
	#else
		#define PM_API __declspec(dllimport)
	#endif
#else
	#if __GNUC__ >= 4 || defined (__clang__)
		#define PM_API __attribute__ ((visibility ("default")))
	#else
		#define PM_API
	#endif
#endif

namespace vl {

	/** @addtogroup CoreGroup
	 *  @{
	 */

	/** 
	 * Private copy constructor and copy assignment ensure classes derived 
	 * from NonCopyable cannot be copied.
	 */
	struct PM_API NonCopyable
	{
#ifndef DOXYGEN_SHOULD_SKIP_THIS
	protected:
		NonCopyable() {}
		~NonCopyable() {}
	private:
		NonCopyable(const NonCopyable&);
		NonCopyable& operator=(const NonCopyable&);
#endif
	};

	/** @} */ // end of CoreGroup

	/** @addtogroup VersionGroup Version
	 *  @brief PhotoMaker build type and version definitions.
	 *  @{
	 */

	/** General version information. */
	enum Version {
		VERSION_MAJOR = 0, //!< Major version.
		VERSION_MINOR = 5, //!< Minor version.
		VERSION_PATCH = 0  //!< Patch version.
	};

	/** GIT hash. */
	static const char VERSION_HASH[] = "19e5f2d604a2472145f34e0558f04bbca79444a0";

	/** @} */ // end of VersionGroup
}
