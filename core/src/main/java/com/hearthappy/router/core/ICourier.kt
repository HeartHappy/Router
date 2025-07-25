package com.hearthappy.router.core

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import androidx.core.app.ActivityOptionsCompat
import java.io.Serializable

/**
 * Created Date: 2025/6/25
 * @author ChenRui
 * ClassDescription：Courier Manager
 */
interface ICourier : ISorter {

    fun addFlags(flags: Int): ICourier

    fun withAction(action: String): ICourier

    fun withObject(key: String, value: Any?): ICourier

    fun withString(key: String, value: String?): ICourier

    fun withInt(key: String, value: Int): ICourier

    fun withBoolean(key: String, value: Boolean): ICourier

    fun withShort(key: String, value: Short): ICourier

    fun withLong(key: String, value: Long): ICourier

    fun withFloat(key: String, value: Float): ICourier

    fun withDouble(key: String, value: Double): ICourier

    fun withByte(key: String, value: Byte): ICourier

    fun withChar(key: String, value: Char): ICourier

    fun withCharSequence(key: String, value: CharSequence?): ICourier

    fun withParcelable(key: String, value: Parcelable?): ICourier

    fun withSerializable(key: String, value: Serializable?): ICourier

    fun withBundle(key: String, bundle: Bundle?): ICourier


    //Array
    fun withIntArray(key: String, value: IntArray?): ICourier

    fun withByteArray(key: String, value: ByteArray?): ICourier

    fun withShortArray(key: String, value: ShortArray?): ICourier

    fun withCharArray(key: String, value: CharArray?): ICourier

    fun withFloatArray(key: String, value: FloatArray?): ICourier

    fun withDoubleArray(key: String, value: DoubleArray?): ICourier

    fun withBooleanArray(key: String, value: BooleanArray?): ICourier

    fun withStringArray(key: String, value: Array<String>?): ICourier

    fun withLongArray(key: String, value: LongArray?): ICourier

    fun withCharSequenceArray(key: String, value: Array<CharSequence>?): ICourier

    fun withParcelableArray(key: String, value: Array<Parcelable>?): ICourier

    // ArrayList
    fun withParcelableArrayList(key: String, value: ArrayList<Parcelable>?): ICourier

    fun withSparseParcelableArray(key: String, value: SparseArray<out Parcelable>?): ICourier

    fun withIntArrayList(key: String, value: ArrayList<Int>?): ICourier

    fun withStringArrayList(key: String, value: ArrayList<String>?): ICourier

    fun withCharSequenceArrayList(key: String, value: ArrayList<CharSequence>?): ICourier


    //animator
    fun withTransition(enterAnim: Int, exitAnim: Int): ICourier

    fun withOptionsCompat(compat: ActivityOptionsCompat): ICourier


}