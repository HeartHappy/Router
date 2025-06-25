package com.hearthappy.router.interfaces

import com.hearthappy.router.core.Courier

/**
 * Created Date: 2025/6/25
 * @author ChenRui
 * ClassDescription：Callback after navigation
 */
interface NavigationCallback {

    /**
     * Callback when find the destination.
     *
     * @param courier meta
     */
    fun onFound(courier: Courier)

    /**
     * Callback after lose your way.
     *
     * @param courier meta
     */
    fun onLost(courier: Courier)

    /**
     * Callback after navigation.
     *
     * @param courier meta
     */
    fun onArrival(courier: Courier)

    /**
     * Callback on interrupt.
     *
     * @param courier meta
     */
    fun onInterrupt(courier: Courier)
}