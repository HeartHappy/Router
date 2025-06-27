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
import com.hearthappy.common_api.model.UserBean
import com.hearthappy.route.ExampleAdapter
import com.hearthappy.route.R
import com.hearthappy.route.base.BaseActivity
import com.hearthappy.route.databinding.ActivityWithBinding
import com.hearthappy.route.model.ExampleBean
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.core.ICourier
import com.hearthappy.router.launcher.Router

@Route(path = RouterPath.CASE_PATH_BUILD)
class WithParametersActivity : BaseActivity<ActivityWithBinding>() {
    private val exampleAdapter by lazy { ExampleAdapter() }
    private val titles=listOf(
        "withObject",
        "withString",
        "withInt",
        "withBoolean",
        "withShort",
        "withLong",
        "withFloat",
        "withDouble",
        "withByte",
        "withChar",
        "withCharSequence",
        "withParcelable",
        "withSerializable",
        "withBundle",
        "withIntArray",
        "withByteArray",
        "withShortArray",
        "withCharArray",
        "withFloatArray",
        "withDoubleArray",
        "withBooleanArray",
        "withStringArray",
        "withLongArray",
        "withCharSequenceArray",
        "withParcelableArray",
        "withParcelableArrayList",
        "withSparseParcelableArray",
        "withIntArrayList",
        "withStringArrayList",
        "withCharSequenceArrayList",
        "withTransition",
        "withOptionsCompat"
    )

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
            override fun onItemClick(view: View, data: ExampleBean, position: Int, listPosition: Int) {
                Router.build(data.path)
                when (data.title) {
                     titles[0]-> {}
                    else -> {}
                }
            }
        })

    }

    override fun ActivityWithBinding.initData() {
    }

    private fun getExampleData(): List<ExampleBean> {
        val build = Router.build(RouterPath.CASE_INJECT)
        return listOf(
            ExampleBean(title = "withObject", courier = build.withObject("withObject", UserBean("Labubu", "987654"))),
            ExampleBean(title = "withString", courier = build.withString("withString", "Labubu")),
            ExampleBean(title = "withInt", courier = build.withInt("withInt", 123456)),
            ExampleBean(title = "withBoolean", courier = build.withBoolean("withBoolean", true)),
            ExampleBean(title = "withShort", courier = build.withShort("withShort", 1234)),
            ExampleBean(title = "withLong", courier = build.withLong("withLong", 1234567890)),
            ExampleBean(title = "withFloat", courier = build.withFloat("withFloat", 123.456f)),
            ExampleBean(title = "withDouble", courier = build.withDouble("withDouble", 123.4567890)),
            ExampleBean(title = "withByte", courier = build.withByte("withByte", 123)),
            ExampleBean(title = "withChar", courier = build.withChar("withChar", 'b')),
            ExampleBean(title = "withCharSequence", courier = build.withCharSequence("withCharSequence", "from charSequence")),
            ExampleBean(title = "withParcelable", courier = build.withParcelable("withParcelable", UserBean("withParcelable", "withParcelable"))),
            ExampleBean(title = "withBundle", courier = build.withBundle("withBundle", Bundle().apply { putString("withBundle", "withBundle") })),
            ExampleBean(title = "withByteArray", courier = build.withByteArray("withByteArray", byteArrayOf(1, 2, 3))),
            ExampleBean(title = "withShortArray", courier = build.withShortArray("withShortArray", shortArrayOf(1, 2, 3))),
            ExampleBean(title = "withCharArray", courier = build.withCharArray("withCharArray", charArrayOf('a', 'b', 'c'))),
            ExampleBean(title = "withFloatArray", courier = build.withFloatArray("withFloatArray", floatArrayOf(1f, 2f, 3f))),
            ExampleBean(title = "withCharSequenceArray", courier = build.withCharSequenceArray("withCharSequenceArray", arrayOf("a", "b", "c"))),
            ExampleBean(title = "withParcelableArray", courier = build.withParcelableArray("withParcelableArray", arrayOf(UserBean("Labubu", "123456"), UserBean("Alibaba", "123456")))),
            ExampleBean(title = "withParcelableArrayList", courier = build.withParcelableArrayList("withParcelableArrayList", arrayListOf(UserBean("Labubu", "b"), UserBean("Tencent", "123456")))),
            ExampleBean(title = "withSparseParcelableArray", courier = build.withSparseParcelableArray("withSparseParcelableArray", SparseArray<Parcelable>().apply { put(0, UserBean("Labubu", "123456")) })),
            ExampleBean(title = "withIntArrayList", courier = build.withIntArrayList("withIntArrayList", arrayListOf(1, 2, 3))),
            ExampleBean(title = "withStringArrayList", courier = build.withStringArrayList("withStringArrayList", arrayListOf("a", "b", "c"))),
            ExampleBean(title = "withCharSequenceArrayList", courier = build.withCharSequenceArrayList("withCharSequenceArrayList", arrayListOf("a", "b", "c"))),
            ExampleBean(title = "withSerializable", courier = build.withSerializable("withSerializable", LoginBean("hearthappy", "123456"))),
            ExampleBean(title = "withTransition", courier = build.withTransition(R.anim.window_bottom_in, R.anim.window_bottom_out)),
            ExampleBean(title = "withOptionsCompat", courier = build.withOptionsCompat( ActivityOptionsCompat.makeClipRevealAnimation(viewBinding.root, viewBinding.root.width / 2, viewBinding.root.height / 2, viewBinding.root.width, viewBinding.root.height))),
        )
    }


    fun exampleBeansBuilder(title: String, build: ICourier): ExampleBean {
        return ExampleBean(path = RouterPath.CASE_INJECT, title = title, courier = build)
    }

    companion object {
        private const val TAG = "WithParametersActivity"
    }

}