cmake_minimum_required(VERSION 3.4.1)

include_directories(src/main/cpp/include/)
add_library(camera-lib SHARED src/main/cpp/camera-lib.cpp)

# set_target_properties(lib_photo_maker PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libPhotoMaker.so)

find_library(log-lib log)

# find_library(log-lib log)
target_link_libraries(camera-lib
                      ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libPhotoMaker.so
                      ${log-lib})
