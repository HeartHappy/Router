package com.hearthappy.mylibrary2

import android.os.Bundle
import android.os.Parcelable
import com.hearthappy.basic.AbsBaseActivity
import com.hearthappy.common_api.RouterPath
import com.hearthappy.common_api.model.UserBean
import com.hearthappy.mylibrary2.databinding.ActivityInjectBinding
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import java.io.Serializable

@Route(RouterPath.CASE_INJECT) class InjectActivity : AbsBaseActivity<ActivityInjectBinding>() {


    @Autowired var withObject: UserBean? = null

    @Autowired var withString: String? = null

    @Autowired var withInt: Int = -1

    @Autowired var withBoolean: Boolean = false

    @Autowired var withShort: Short = -1

    @Autowired var withLong: Long = -1

    @Autowired var withFloat: Float = -1f

    @Autowired var withDouble: Double = -1.0

    @Autowired var withChar: Char = 'a'

    @Autowired var withByte: Byte = -1

    @Autowired var withCharSequence: CharSequence? = null

    @Autowired var withParcelable: Parcelable? = null

    @Autowired var withSerializable: Serializable? = null


//    @Autowired var withParcelableArray: Array<Parcelable>? = null
//
//    @Autowired var withParcelableArrayList: ArrayList<Parcelable>? = null
//
//    @Autowired var withSparseParcelableArray: SparseArray<out Parcelable>? = null
//
//    @Autowired var withIntegerArrayList: ArrayList<Int>? = null
//
//    @Autowired var withStringArrayList: ArrayList<String>? = null
//
//    @Autowired var withCharSequenceArrayList: ArrayList<CharSequence>? = null
//
//    @Autowired var withSerializableArrayList: ArrayList<Serializable>? = null

    @Autowired var withByteArray: ByteArray? = null

    @Autowired var withCharArray: CharArray? = null

    @Autowired var withShortArray: ShortArray? = null

    @Autowired var withIntArray: IntArray? = null

    @Autowired var withLongArray: LongArray? = null

    @Autowired var withFloatArray: FloatArray? = null

    @Autowired var withDoubleArray: DoubleArray? = null

    @Autowired var withBooleanArray: BooleanArray? = null

    @Autowired var withStringArray: Array<String>? = null

    @Autowired var withCharSequenceArray: Array<CharSequence>? = null

    @Autowired var withBundle: Bundle? = null


    override fun ActivityInjectBinding.initData() {
    }

    override fun ActivityInjectBinding.initListener() {
    }

    override fun ActivityInjectBinding.initView() {
        setTitle("modules2: InjectActivity")
    }

    override fun ActivityInjectBinding.initViewModelListener() {
    }
}