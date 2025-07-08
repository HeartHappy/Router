package com.hearthappy.router.launcher

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import androidx.core.app.ActivityOptionsCompat
import com.hearthappy.router.core.ICourier
import com.hearthappy.router.exception.HandlerException
import com.hearthappy.router.service.SerializationService
import java.io.Serializable

/**
 * Created Date: 2025/6/8
 * @author ChenRui
 * ClassDescription：Courier
 *
 * Responsible for packaging related products and forwarding them to other related personnel.
 * He also provides other services, such as interception.
 */
class Courier(path: String, uri: Uri?) : Sorter() {

    init {
        setPath(path)
        setUri(uri)
    }
    override fun addFlags(flags: Int): ICourier {
        pack.flags = pack.flags or flags
        return this
    }

    override fun withAction(action: String): ICourier {
        pack.action = action
        return this
    }

    override fun withObject(key: String, value: Any?): ICourier {
        serializationService?.let {
            pack.bundle.putString(key, it.toJson(value))
        } ?: run {
            serializationService = Router.getInstance(SerializationService::class.java)
            serializationService?.let { pack.bundle.putString(key, it.toJson(value)) } ?: throw HandlerException("The SerializationService interface has no implementation class")
        }
        return this
    }

    override fun withString(key: String, value: String?): ICourier {
        pack.bundle.putString(key, value)
        return this
    }

    override fun withInt(key: String, value: Int): ICourier {
        pack.bundle.putInt(key, value)
        return this
    }

    override fun withBoolean(key: String, value: Boolean): ICourier {
        pack.bundle.putBoolean(key, value)
        return this
    }

    override fun withShort(key: String, value: Short): ICourier {
        pack. bundle.putShort(key, value)
        return this
    }

    override fun withLong(key: String, value: Long): ICourier {
        pack.bundle.putLong(key, value)
        return this
    }

    override fun withFloat(key: String, value: Float): ICourier {
        pack.bundle.putFloat(key, value)
        return this
    }

    override fun withDouble(key: String, value: Double): ICourier {
        pack.bundle.putDouble(key, value)
        return this
    }

    override fun withByte(key: String, value: Byte): ICourier {
        pack.bundle.putByte(key, value)
        return this
    }

    override fun withChar(key: String, value: Char): ICourier {
        pack.bundle.putChar(key, value)
        return this
    }

    override fun withCharSequence(key: String, value: CharSequence?): ICourier {
        pack.bundle.putCharSequence(key, value)
        return this
    }

    override fun withParcelable(key: String, value: Parcelable?): ICourier {
        pack.bundle.putParcelable(key, value)
        return this
    }

    override fun withParcelableArray(key: String, value: Array<Parcelable>?): ICourier {
        pack.bundle.putParcelableArray(key, value)
        return this
    }


    override fun withParcelableArrayList(key: String, value: ArrayList<Parcelable>?): ICourier {
        pack.bundle.putParcelableArrayList(key, value)
        return this
    }

    override fun withSparseParcelableArray(key: String, value: SparseArray<out Parcelable>?): ICourier {
        pack.bundle.putSparseParcelableArray(key, value)
        return this
    }

    override fun withIntArrayList(key: String, value: ArrayList<Int>?): ICourier {
        pack.bundle.putIntegerArrayList(key, value)
        return this
    }

    override fun withStringArrayList(key: String, value: ArrayList<String>?): ICourier {
        pack.bundle.putStringArrayList(key, value)
        return this
    }

    override fun withCharSequenceArrayList(key: String, value: ArrayList<CharSequence>?): ICourier {
        pack.bundle.putCharSequenceArrayList(key, value)
        return this
    }

    override fun withSerializable(key: String, value: Serializable?): ICourier {
        pack.bundle.putSerializable(key, value)
        return this
    }

    override fun withIntArray(key: String, value: IntArray?): ICourier {
        pack.bundle.putIntArray(key, value)
        return this
    }

    override fun withByteArray(key: String, value: ByteArray?): ICourier {
        pack.bundle.putByteArray(key, value)
        return this
    }

    override fun withShortArray(key: String, value: ShortArray?): ICourier {
        pack.bundle.putShortArray(key, value)
        return this
    }

    override fun withCharArray(key: String, value: CharArray?): ICourier {
        pack.bundle.putCharArray(key, value)
        return this
    }

    override fun withFloatArray(key: String, value: FloatArray?): ICourier {
        pack.bundle.putFloatArray(key, value)
        return this
    }

    override fun withDoubleArray(key: String, value: DoubleArray?): ICourier {
        pack.bundle.putDoubleArray( key, value)
        return this
    }

    override fun withBooleanArray(key: String, value: BooleanArray?): ICourier {
        pack.bundle.putBooleanArray(key, value)
        return this
    }

    override fun withStringArray(key: String, value: Array<String>?): ICourier {
        pack.bundle.putStringArray(key, value)
        return this
    }

    override fun withLongArray(key: String, value: LongArray?): ICourier {
        pack.bundle.putLongArray(key, value)
        return this
    }

    override fun withCharSequenceArray(key: String, value: Array<CharSequence>?): ICourier {
        pack.bundle.putCharSequenceArray(key, value)
        return this
    }

    override fun withTransition(enterAnim: Int, exitAnim: Int): ICourier {
        pack.enterAnim = enterAnim
        pack.exitAnim = exitAnim
        return this
    }
    override fun withOptionsCompat(compat: ActivityOptionsCompat): ICourier {
        pack.optionsCompat = compat.toBundle()
        return this
    }

    override fun withBundle(key: String, bundle: Bundle?): ICourier {
        pack.bundle.putBundle(key, bundle)
        return this
    }

}