//
// Created by dong on 2019/5/12.
//

#ifndef BULLETDEMO_LOG_H
#define BULLETDEMO_LOG_H

#include "android/log.h"
#define TAG    "bullet" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__) // 定义LOGD类型
#endif //BULLETDEMO_LOG_H
