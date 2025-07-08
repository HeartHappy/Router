package com.hearthappy.router.core

import android.content.Context
import com.hearthappy.router.interfaces.NavigationCallback

/**
 * Created Date: 2025/6/25
 * @author ChenRui
 * ClassDescription： Responsible for task classification
 */
interface ISorter {

    fun navigation()

    fun navigation(context: Context?)

    fun navigation(context: Context?, requestCode: Int)

    fun navigation(context: Context?, requestCode: Int, callback: NavigationCallback?)

    fun greenChannel():ISorter

    fun getDestination(): Class<*>

    fun getInstance(): Any

}