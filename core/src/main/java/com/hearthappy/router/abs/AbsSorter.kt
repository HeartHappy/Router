package com.hearthappy.router.abs

import android.content.Context
import android.content.Intent
import com.hearthappy.router.interfaces.NavigationCallback

/**
 * Created Date: 2025/6/25
 * @author ChenRui
 * ClassDescription： Responsible for task classification
 */
interface AbsSorter {
    fun navigation()

    fun navigation(context: Context?)

    fun navigation(context: Context?, requestCode: Int)

    fun navigation(context: Context?, requestCode: Int, callback: NavigationCallback?)

    fun getIntent(): Intent

    fun getDestination(): Class<*>

    fun getInstance(): Any

    fun <T> getInstance(instance: Class<T>): T?
}