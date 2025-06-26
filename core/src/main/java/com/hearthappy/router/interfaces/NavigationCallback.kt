package com.hearthappy.router.interfaces

import com.hearthappy.router.launcher.Sorter

/**
 * Created Date: 2025/6/25
 * @author ChenRui
 * ClassDescription：Navigation callback
 */
interface NavigationCallback {

    /**
     * Callback when find the destination.
     *
     * @param sorter meta
     */
    fun onFound(sorter: Sorter)

    /**
     * Callback after lose your way.
     *
     * @param sorter meta
     */
    fun onLost(sorter: Sorter)

    /**
     * Callback after navigation.
     *
     * @param sorter meta
     */
    fun onArrival(sorter: Sorter)

    /**
     * Callback on interrupt.
     *
     * @param sorter meta
     */
    fun onInterrupt(sorter: Sorter)
}