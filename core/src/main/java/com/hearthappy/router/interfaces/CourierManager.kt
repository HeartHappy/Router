package com.hearthappy.router.interfaces

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import androidx.core.app.ActivityOptionsCompat
import com.hearthappy.router.abs.AbsSorter
import java.io.Serializable

/**
 * Created Date: 2025/6/25
 * @author ChenRui
 * ClassDescription：Courier Manager
 */
interface CourierManager:AbsSorter {

    fun addFlags(flags: Int): CourierManager

    fun withAction(action: String): CourierManager

    fun withObject(key: String, value: Any): CourierManager

    fun withString(key: String, value: String): CourierManager

    fun withInt(key: String, value: Int): CourierManager

    fun withBoolean(key: String, value: Boolean): CourierManager

    fun withShort(key: String, value: Short): CourierManager

    fun withLong(key: String, value: Long): CourierManager

    fun withFloat(key: String, value: Float): CourierManager

    fun withDouble(key: String, value: Double): CourierManager

    fun withByte(key: String, value: Byte): CourierManager

    fun withChar(key: String, value: Char): CourierManager

    fun withCharSequence(key: String, value: CharSequence): CourierManager

    fun withParcelable(key: String, value: Parcelable): CourierManager

    fun withParcelableArray(key: String, value: Array<Parcelable>): CourierManager

    fun withParcelableArrayList(key: String, value: ArrayList<Parcelable>): CourierManager

    fun withSparseParcelableArray(key: String, value: SparseArray<out Parcelable>): CourierManager

    fun withIntegerArrayList(key: String, value: ArrayList<Int>): CourierManager

    fun withStringArrayList(key: String, value: ArrayList<String>): CourierManager

    fun withCharSequenceArrayList(key: String, value: ArrayList<CharSequence>): CourierManager

    fun withSerializable(key: String, value: Serializable): CourierManager

    fun withByteArray(key: String, value: ByteArray): CourierManager

    fun withShortArray(key: String, value: ShortArray): CourierManager

    fun withCharArray(key: String, value: CharArray): CourierManager

    fun withFloatArray(key: String, value: FloatArray): CourierManager

    fun withCharSequenceArray(key: String, value: Array<CharSequence>): CourierManager

    fun withTransition(enterAnim: Int, exitAnim: Int): CourierManager

    fun withOptionsCompat(compat: ActivityOptionsCompat): CourierManager

    fun withBundle(key: String, bundle: Bundle): CourierManager
}