package com.hearthappy.route

import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hearthappy.basic.interfaces.OnItemClickListener
import com.hearthappy.common_api.HelloService
import com.hearthappy.common_api.RouterPath
import com.hearthappy.mylibrary.R
import com.hearthappy.route.base.BaseActivity
import com.hearthappy.route.databinding.ActivityMainBinding
import com.hearthappy.route.model.ExampleBean
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.launcher.Router


@Route(path = RouterPath.MAIN_ACTIVITY) class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val exampleAdapter by lazy { ExampleAdapter() }
    override fun ActivityMainBinding.initViewModelListener() {
    }

    override fun ActivityMainBinding.initView() {
        setTitle("Welcome KSP Router")
        rvExamples.adapter = exampleAdapter
        rvExamples.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        rvExamples.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
        exampleAdapter.initData(getExampleData())
    }

    override fun ActivityMainBinding.initListener() {
        exampleAdapter.setOnItemClickListener(object : OnItemClickListener<ExampleBean> {
            override fun onItemClick(view: View, data: ExampleBean, position: Int, listPosition: Int) {
                when (data.path) {
                    RouterPath.CASE_FRAGMENT -> {
                        val fragment = Router.build(RouterPath.MODULES_FRAGMENT).withString("username", "I am the loaded Fragment").getInstance() as Fragment
                        val beginTransaction = supportFragmentManager.beginTransaction()
                        beginTransaction.add(R.id.fragmentLayout, fragment)
                        beginTransaction.commit()
                    }
                    RouterPath.CASE_SERVICE -> Router.build(RouterPath.SERVICE_BACKEND).navigation().also {
                        tvDes.text = getString(com.hearthappy.route.R.string.hint_service_start_msg)
                    }
                    RouterPath.CASE_PROVIDER_SERVICE -> {
                        val instance = Router.build(RouterPath.SERVICE_HELLO).getInstance() as HelloService //                val instance1 = Router.getInstance(HelloService::class.java)
                        val sayHello = instance.sayHello("KSP Router") //                        instance.sayHello("interface service from HelloService")
                        tvDes.text = sayHello
                    }
                    else -> Router.build(data.path).greenChannel().navigation()
                }
            }
        })
    }

    override fun ActivityMainBinding.initData() {
    }

    private fun getExampleData(): MutableList<ExampleBean> {
        return mutableListOf<ExampleBean>().apply {
            add(ExampleBean(RouterPath.CASE_PATH_BUILD, "Jump with parameters"))
            add(ExampleBean(RouterPath.CASE_PATH_REPLACE, "Path processing"))
            add(ExampleBean(RouterPath.CASE_INTERCEPTOR, "Interceptor"))
            add(ExampleBean(RouterPath.CASE_ACTIVITY_FOR_RESULT, "ActivityForResult"))
            add(ExampleBean(RouterPath.CASE_FRAGMENT, "Fragment instantiation"))
            add(ExampleBean(RouterPath.CASE_SERVICE, "Service instantiation"))
            add(ExampleBean(RouterPath.CASE_PROVIDER_SERVICE, "Provider Service Instantiation"))
        }
    }
}