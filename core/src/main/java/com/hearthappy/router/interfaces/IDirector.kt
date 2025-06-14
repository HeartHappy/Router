package com.hearthappy.router.interfaces

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import java.io.Serializable


interface IDirector {

    fun addFlags(flags : Int) : IDirector

    fun withAction(action : String) : IDirector

    fun withObject(key : String, value : Any) : IDirector

    fun withString(key : String, value : String) : IDirector

    fun withInt(key : String, value : Int) : IDirector

    fun withBoolean(key : String, value : Boolean) : IDirector

    fun withShort(key : String, value : Short) : IDirector

    fun withLong(key : String, value : Long) : IDirector

    fun withFloat(key : String, value : Float) : IDirector

    fun withDouble(key : String, value : Double) : IDirector

    fun withByte(key : String, value : Byte) : IDirector

    fun withChar(key : String, value : Char) : IDirector

    fun withCharSequence(key : String, value : CharSequence) : IDirector

    fun withParcelable(key : String, value : Parcelable) : IDirector

    fun withParcelableArray(key : String, value : Array<Parcelable>) : IDirector

    fun withParcelableArrayList(key : String, value : ArrayList<Parcelable>) : IDirector

    fun withSparseParcelableArray(key : String, value : SparseArray<out Parcelable>) : IDirector

    fun withIntegerArrayList(key : String, value : ArrayList<Int>) : IDirector

    fun withStringArrayList(key : String, value : ArrayList<String>) : IDirector

    fun withCharSequenceArrayList(key : String, value : ArrayList<CharSequence>) : IDirector

    fun withSerializable(key : String, value : Serializable) : IDirector

    fun withByteArray(key : String, value : ByteArray) : IDirector

    fun withShortArray(key : String, value : ShortArray) : IDirector

    fun withCharArray(key : String, value : CharArray) : IDirector

    fun withFloatArray(key : String, value : FloatArray) : IDirector

    fun withCharSequenceArray(key : String, value : Array<CharSequence>) : IDirector

    fun withTransition(enterAnim : Int, exitAnim : Int) : IDirector

    fun withBundle(key : String, bundle : Bundle) : IDirector

    fun navigation()

    fun navigation(context : Context?)

    fun getInstance() : Any

    fun <T> getInstance(instance : Class<T>) : T?

}