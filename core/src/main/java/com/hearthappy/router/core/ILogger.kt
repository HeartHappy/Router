package com.hearthappy.router.core

/**
 * Created Date: 2025/6/19
 * @author ChenRui
 * ClassDescription：Logger
 */
interface ILogger {

    fun showLog(isShowLog: Boolean)

    fun showStackTrace(isShowStackTrace: Boolean)

    fun debug(message: String?)

    fun debug(tag: String?, message: String?)

    fun info(message: String?)

    fun info(tag: String?, message: String?)

    fun warning(message: String?)

    fun warning(tag: String?, message: String?)

    fun error(message: String?)

    fun error(tag: String?, message: String?)

    fun error(tag: String?, message: String?, throwable: Throwable?)

    fun monitor(message: String?)

    fun isMonitorMode(): Boolean

}