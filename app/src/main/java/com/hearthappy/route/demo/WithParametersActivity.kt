package com.hearthappy.route.demo

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hearthappy.basic.interfaces.OnItemClickListener
import com.hearthappy.common_api.RouterPath
import com.hearthappy.common_api.model.LoginBean
import com.hearthappy.common_api.model.ParcelableBean
import com.hearthappy.common_api.model.UserBean
import com.hearthappy.route.ExampleAdapter
import com.hearthappy.route.R
import com.hearthappy.route.base.BaseActivity
import com.hearthappy.route.databinding.ActivityWithBinding
import com.hearthappy.route.model.ExampleBean
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.launcher.Router

@Route(path = RouterPath.CASE_WITH_PARAMETERS)
class WithParametersActivity : BaseActivity<ActivityWithBinding>() {
    private val exampleAdapter by lazy { ExampleAdapter() }
    private val titles = listOf("withObject", "withString", "withInt", "withBoolean", "withShort", "withLong", "withFloat", "withDouble", "withByte", "withChar", "withCharSequence", "withParcelable", "withSerializable", "withBundle", "withIntArray", "withByteArray", "withShortArray", "withCharArray", "withFloatArray", "withDoubleArray", "withBooleanArray", "withStringArray", "withLongArray", "withCharSequenceArray", "withParcelableArray", "withParcelableArrayList", "withSparseParcelableArray", "withIntArrayList", "withStringArrayList", "withCharSequenceArrayList", "withTransition", "withOptionsCompat")

    override fun ActivityWithBinding.initViewModelListener() {
    }

    override fun ActivityWithBinding.initView() {
        setTitle("App : WithParametersActivity")
        rvWithList.adapter = exampleAdapter
        rvWithList.layoutManager = LinearLayoutManager(this@WithParametersActivity)
        rvWithList.addItemDecoration(DividerItemDecoration(this@WithParametersActivity, DividerItemDecoration.VERTICAL))
        exampleAdapter.initData(getExampleData())
    }

    override fun ActivityWithBinding.initListener() {
        exampleAdapter.setOnItemClickListener(object : OnItemClickListener<ExampleBean> {
            override fun onItemClick(view : View, data : ExampleBean, position : Int, listPosition : Int) {
                val build = Router.build(RouterPath.CASE_INJECT)
                    .withString("routeKey", data.title)//The target interface only takes data of this type
                when (data.title) {
                    "withObject" -> build.withObject("withObject", UserBean("Labubu", "987654"))
                    "withString" -> build.withString("withString", "Labubu")
                    "withInt" -> build.withInt("withInt", 123456)
                    "withBoolean" -> build.withBoolean("withBoolean", true)
                    "withShort" -> build.withShort("withShort", 1234)
                    "withLong" -> build.withLong("withLong", 1234567890)
                    "withFloat" -> build.withFloat("withFloat", 123.456f)
                    "withDouble" -> build.withDouble("withDouble", 123.4567890)
                    "withByte" -> build.withByte("withByte", 123)
                    "withChar" -> build.withChar("withChar", 'b')
                    "withCharSequence" -> build.withCharSequence("withCharSequence", "from charSequence")
                    "withParcelable" -> build.withParcelable("withParcelable", ParcelableBean("Labubu", "123456"))
                    "withSerializable" -> build.withSerializable("withSerializable", LoginBean("Labubu", "123456"))
                    "withBundle" -> build.withBundle("withBundle", Bundle().apply { putString("withBundle", "bundle data") })
                    "withIntArray"-> build.withIntArray("withIntArray", intArrayOf(1, 2, 3))
                    "withByteArray" -> build.withByteArray("withByteArray", byteArrayOf(1, 2, 3))
                    "withShortArray" -> build.withShortArray("withShortArray", shortArrayOf(1, 2, 3))
                    "withCharArray" -> build.withCharArray("withCharArray", charArrayOf('a', 'b', 'c'))
                    "withFloatArray" -> build.withFloatArray("withFloatArray", floatArrayOf(1f, 2f, 3f))
                    "withDoubleArray" -> build.withDoubleArray("withDoubleArray", doubleArrayOf(1.0, 2.0, 3.0))
                    "withBooleanArray" -> build.withBooleanArray("withBooleanArray", booleanArrayOf(true, false))
                    "withStringArray" -> build.withStringArray("withStringArray", arrayOf("a", "b", "c"))
                    "withLongArray" -> build.withLongArray("withLongArray", longArrayOf(1, 2, 3))
                    "withCharSequenceArray" -> build.withCharSequenceArray("withCharSequenceArray", arrayOf("a", "b", "c"))
                    "withParcelableArray" -> build.withParcelableArray("withParcelableArray", arrayOf(ParcelableBean("Labubu", "123456"), ParcelableBean("Alibaba", "123456")))
                    "withSparseParcelableArray" -> build.withSparseParcelableArray("withSparseParcelableArray", SparseArray<Parcelable>().apply { put(0, ParcelableBean("Labubu", "123456")) })
                    "withParcelableArrayList" -> build.withParcelableArrayList("withParcelableArrayList", arrayListOf(ParcelableBean("Labubu", "123456"), ParcelableBean("Alibaba", "123456")))
                    "withIntArrayList" -> build.withIntArrayList("withIntArrayList", arrayListOf(1, 2, 3))
                    "withStringArrayList" -> build.withStringArrayList("withStringArrayList", arrayListOf("a", "b", "c"))
                    "withCharSequenceArrayList" -> build.withCharSequenceArrayList("withCharSequenceArrayList", arrayListOf("a", "b", "c"))
                    "withTransition" -> build.withTransition(R.anim.window_bottom_in, R.anim.window_bottom_out)
                    "withOptionsCompat" -> build.withOptionsCompat(ActivityOptionsCompat.makeScaleUpAnimation(view, view.width / 2, view.height / 2, view.width, view.height))
                    else -> {}
                }
                if(data.title == "withTransition" || data.title == "withOptionsCompat"){
                    build.navigation(this@WithParametersActivity)//The animation type needs to be passed into the Activity's Context
                }else{
                    build.navigation()
                }

            }
        })

    }

    override fun ActivityWithBinding.initData() {
    }

    private fun getExampleData() : List<ExampleBean> {
      return  titles.map { ExampleBean(title = it) }
    }


    companion object {
        private const val TAG = "WithParametersActivity"
    }

}