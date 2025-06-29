package com.hearthappy.mylibrary2

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import androidx.core.util.size
import com.hearthappy.basic.AbsBaseActivity
import com.hearthappy.common_api.HelloService
import com.hearthappy.common_api.RouterPath
import com.hearthappy.common_api.model.ParcelableBean
import com.hearthappy.common_api.model.UserBean
import com.hearthappy.mylibrary2.databinding.ActivityInjectBinding
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.launcher.Router
import java.io.Serializable

@Route(RouterPath.CASE_INJECT) class InjectActivity : AbsBaseActivity<ActivityInjectBinding>() {
    @Autowired var routeKey : String = ""

    @Autowired var withObject : UserBean? = null

    @Autowired var withString : String? = null

    @Autowired var withInt : Int = -1

    @Autowired var withBoolean : Boolean = false

    @Autowired var withShort : Short = -1

    @Autowired var withLong : Long = -1

    @Autowired var withFloat : Float = -1f

    @Autowired var withDouble : Double = -1.0

    @Autowired var withChar : Char = 'a'

    @Autowired var withByte : Byte = -1

    @Autowired var withCharSequence : CharSequence? = null

    @Autowired var withParcelable : Parcelable? = null

    @Autowired var withSerializable : Serializable? = null

    @Autowired var withBundle : Bundle? = null

    //array
    @Autowired var withByteArray : ByteArray? = null

    @Autowired var withCharArray : CharArray? = null

    @Autowired var withShortArray : ShortArray? = null

    @Autowired var withIntArray : IntArray? = null

    @Autowired var withLongArray : LongArray? = null

    @Autowired var withFloatArray : FloatArray? = null

    @Autowired var withDoubleArray : DoubleArray? = null

    @Autowired var withBooleanArray : BooleanArray? = null

    @Autowired var withStringArray : Array<String>? = null

    @Autowired var withCharSequenceArray : Array<CharSequence>? = null

    @Autowired var withParcelableArray : Array<Parcelable>? = null

    //list
    @Autowired var withParcelableArrayList : ArrayList<Parcelable>? = null

    @Autowired var withSparseParcelableArray : SparseArray<out Parcelable>? = null

    @Autowired var withIntArrayList : ArrayList<Int>? = null

    @Autowired var withStringArrayList : ArrayList<String>? = null

    @Autowired var withCharSequenceArrayList : ArrayList<CharSequence>? = null

    @Autowired
    var helloService: HelloService? = null

    override fun ActivityInjectBinding.initData() {
       val data= when (routeKey) {
            "withObject" -> withObject.toString()
            "withString" -> withString
            "withInt" -> withInt.toString()
            "withBoolean" -> withBoolean.toString()
            "withShort" -> withShort.toString()
            "withLong" -> withLong.toString()
            "withFloat" -> withFloat.toString()
            "withDouble" -> withDouble.toString()
            "withByte" -> withByte.toString()
            "withChar" -> withChar.toString()
            "withCharSequence" -> withCharSequence.toString()
            "withParcelable" -> withParcelable.toString()
            "withSerializable" -> withSerializable.toString()
            "withBundle" -> withBundle?.get(routeKey).toString()
            "withIntArray" -> withIntArray?.joinToString(",")
            "withByteArray" -> withByteArray?.joinToString(",")
            "withShortArray" -> withShortArray?.joinToString(",")
            "withCharArray" -> withCharArray?.joinToString(",")
            "withFloatArray" -> withFloatArray?.joinToString(",")
            "withDoubleArray" -> withDoubleArray?.joinToString(",")
            "withBooleanArray" -> withBooleanArray?.joinToString(",")
            "withStringArray" -> withStringArray?.joinToString(",")
            "withLongArray" -> withLongArray?.joinToString(",")
            "withParcelableArray" -> withParcelableArray?.joinToString(",")
            "withCharSequenceArray" -> withCharSequenceArray?.joinToString(",")
            "withSparseParcelableArray" -> withSparseParcelableArray?.joinToString(",")
            "withParcelableArrayList" -> withParcelableArrayList?.joinToString(",")
            "withIntArrayList" -> withIntArrayList?.joinToString(",")
            "withStringArrayList" -> withStringArrayList?.joinToString(",")
            "withCharSequenceArrayList" -> withCharSequenceArrayList?.joinToString(",")
            else -> "Not Found:${routeKey}"
        }
        tvDes.text = String.format("data: %s", data)

    }

    override fun ActivityInjectBinding.initListener() {
    }

    override fun ActivityInjectBinding.initView() {
        Router.inject(this@InjectActivity)
        setTitle("modules2: InjectActivity")
        tvTitle.text=String.format("title: %s\n${helloService?.sayHello("KSP Router")}",routeKey)
    }

    override fun ActivityInjectBinding.initViewModelListener() {
    }

    //SparseArray<out Parcelable>.joinToString(",")
    fun SparseArray<out Parcelable>.joinToString(split : String) : String {
        val builder = StringBuilder()
        for (i in 0 until size) {
            builder.append(get(keyAt(i)).toString())
            if (i != size - 1) {
                builder.append(split)
            }
        }
        return builder.toString()
    }
}