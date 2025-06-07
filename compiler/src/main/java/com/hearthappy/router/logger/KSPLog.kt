package com.hearthappy.router.logger

import com.google.devtools.ksp.processing.KSPLogger

/**
 * Created Date: 2025/6/7
 * @author ChenRui
 * ClassDescription：日志工具类
 */
class KSPLog {
    companion object {
        private lateinit var logger: KSPLogger
        fun init(logger: KSPLogger) {
            Companion.logger = logger
        }

        fun print(msg: String) {
            if (::logger.isInitialized) {
                logger.warn("Router: =====> $msg")
            }
        }
    }
}