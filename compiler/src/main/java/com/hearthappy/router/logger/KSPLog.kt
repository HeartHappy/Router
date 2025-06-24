package com.hearthappy.router.logger

import com.google.devtools.ksp.processing.KSPLogger

/**
 * Created Date: 2025/6/7
 * @author ChenRui
 * ClassDescription：日志工具类
 */
class KSPLog {

    companion object {
        private fun formatTime(milliseconds: Long): String {
            val seconds = milliseconds / 1000
            val remainingMilliseconds = milliseconds % 1000
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return when {
                minutes > 0 -> "%d m %d s %d ms".format(minutes, remainingSeconds, remainingMilliseconds)
                remainingSeconds > 0 -> "%d s %d ms".format(remainingSeconds, remainingMilliseconds)
                else -> "%d ms".format(remainingMilliseconds)
            }
        }

        private lateinit var logger: KSPLogger
        fun init(logger: KSPLogger) {
            Companion.logger = logger
        }

        fun print(msg: String) {
            isInit { logger.warn("Router: =====> $msg") }
        }

        fun printRouterTook(count: Int, measureTimeMillis: Long) {
            isInit { logger.warn("Router: ===================> Generate Router file count:$count,took:${formatTime(measureTimeMillis)} <===================\n") }
        }

        fun printInterceptorTook(count: Int, measureTimeMillis: Long) {
            isInit { logger.warn("Interceptor: ===================> Generate Interceptor file count:$count,took:${formatTime(measureTimeMillis)} <===================\n") }
        }

        private fun isInit(block: () -> Unit) {
            if (::logger.isInitialized) block()
        }
    }
}