package com.hearthappy.route.demo

import com.hearthappy.common_api.RouterPath
import com.hearthappy.route.base.BaseActivity
import com.hearthappy.route.databinding.ActivityInterceptorBinding
import com.hearthappy.route.interceptor.ActivityInterceptor
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.launcher.Router

@Route(RouterPath.CASE_INTERCEPTOR)
class InterceptorActivity:BaseActivity<ActivityInterceptorBinding>() {
    override fun ActivityInterceptorBinding.initData() {
    }

    override fun ActivityInterceptorBinding.initListener() {
        btnJump1.setOnClickListener {
            ActivityInterceptor.interceptorSwitch = false
            Router.build(RouterPath.CASE_ACTIVITY_FOR_RESULT).withString("name", "From the UserProfileActivity in the app").navigation(this@InterceptorActivity)
        }
        btnJump2.setOnClickListener {
            ActivityInterceptor.interceptorSwitch = true
            Router.build(RouterPath.CASE_ACTIVITY_FOR_RESULT).withString("name", "From the UserProfileActivity in the app").navigation()
        }
    }

    override fun ActivityInterceptorBinding.initView() {
        setTitle("App : InterceptorActivity")
    }

    override fun ActivityInterceptorBinding.initViewModelListener() {
    }
}