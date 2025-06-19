package com.hearthappy.router.logger

import android.text.TextUtils
import android.util.Log
import com.hearthappy.router.core.BuildConfig
import com.hearthappy.router.core.Router
import com.hearthappy.router.interfaces.ILogger

class DefaultLogger : ILogger {
    private var isShowLog: Boolean = false

    private var isShowStackTrace: Boolean = false

    private var isMonitorMode: Boolean = false

    private var defaultTag: String = Router.TAG
    override fun showLog(isShowLog: Boolean) {
        this.isShowLog = isShowLog
    }

    override fun showStackTrace(isShowStackTrace: Boolean) {
        this.isShowStackTrace = isShowStackTrace
    }

    override fun debug(message: String?) {
        if (BuildConfig.DEBUG && isShowLog) {
            val stackTraceElement = Thread.currentThread().stackTrace[3]
            Log.d(defaultTag, message + getExtInfo(stackTraceElement))
        }
    }

    override fun debug(tag: String?, message: String?) {
        if (BuildConfig.DEBUG && isShowLog) {
            val stackTraceElement = Thread.currentThread().stackTrace[3]
            Log.d(if (TextUtils.isEmpty(tag)) defaultTag else tag, message + getExtInfo(stackTraceElement))
        }
    }

    override fun info(message: String?) {
        if (BuildConfig.DEBUG && isShowLog) {
            val stackTraceElement = Thread.currentThread().stackTrace[3]
            Log.i(defaultTag, message + getExtInfo(stackTraceElement))
        }
    }

    override fun info(tag: String?, message: String?) {
        if (BuildConfig.DEBUG && isShowLog) {
            val stackTraceElement = Thread.currentThread().stackTrace[3]
            Log.i(if (TextUtils.isEmpty(tag)) defaultTag else tag, message + getExtInfo(stackTraceElement))
        }
    }

    override fun warning(message: String?) {
        if (BuildConfig.DEBUG && isShowLog) {
            val stackTraceElement = Thread.currentThread().stackTrace[3]
            Log.w(defaultTag, message + getExtInfo(stackTraceElement))
        }
    }

    override fun warning(tag: String?, message: String?) {
        if (BuildConfig.DEBUG && isShowLog) {
            val stackTraceElement = Thread.currentThread().stackTrace[3]
            Log.w(if (TextUtils.isEmpty(tag)) defaultTag else tag, message + getExtInfo(stackTraceElement))
        }
    }

    override fun error(message: String?) {
        if (BuildConfig.DEBUG && isShowLog) {
            val stackTraceElement = Thread.currentThread().stackTrace[3]
            Log.e(defaultTag, message + getExtInfo(stackTraceElement))
        }
    }

    override fun error(tag: String?, message: String?) {
        if (BuildConfig.DEBUG && isShowLog) {
            val stackTraceElement = Thread.currentThread().stackTrace[3]
            Log.e(if (TextUtils.isEmpty(tag)) defaultTag else tag, message + getExtInfo(stackTraceElement))
        }
    }

    override fun error(tag: String?, message: String?, e: Throwable?) {
        if (BuildConfig.DEBUG && isShowLog) {
            val stackTraceElement = Thread.currentThread().stackTrace[3]
            Log.e(if (TextUtils.isEmpty(tag)) defaultTag else tag, message + getExtInfo(stackTraceElement))
        }
    }

    override fun monitor(message: String?) {
        if (BuildConfig.DEBUG && isShowLog && isMonitorMode()) {
            val stackTraceElement = Thread.currentThread().stackTrace[3]
            Log.d("$defaultTag::monitor", message + getExtInfo(stackTraceElement))
        }
    }

    override fun isMonitorMode(): Boolean {
        return isMonitorMode
    }

    private fun getExtInfo(stackTraceElement: StackTraceElement): String {
        val separator = " & "
        val sb = StringBuilder("[")
        if (isShowStackTrace) {
            val threadName = Thread.currentThread().name
            val fileName = stackTraceElement.fileName
            val className = stackTraceElement.className
            val methodName = stackTraceElement.methodName
            val threadID = Thread.currentThread().id
            val lineNumber = stackTraceElement.lineNumber

            sb.append("ThreadId=").append(threadID).append(separator)
            sb.append("ThreadName=").append(threadName).append(separator)
            sb.append("FileName=").append(fileName).append(separator)
            sb.append("ClassName=").append(className).append(separator)
            sb.append("MethodName=").append(methodName).append(separator)
            sb.append("LineNumber=").append(lineNumber)
        }
        sb.append(" ] ")
        return sb.toString()
    }

}