package com.hearthappy.route.demo

import com.hearthappy.common_api.RouterPath
import com.hearthappy.route.base.BaseActivity
import com.hearthappy.route.databinding.ActivityWithBinding
import com.hearthappy.route.interceptor.ActivityInterceptor
import com.hearthappy.route.model.UserBean
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.launcher.Router

@Route(path = RouterPath.CASE_PATH_BUILD)
class WithActivity : BaseActivity<ActivityWithBinding>() {
    @Autowired
    var username : String = ""

    @Autowired(name = "user_id")
    var userId : Int = 0

    @Autowired
    var isPremium : Boolean = false

    @Autowired
    var user : UserBean? = null


    override fun ActivityWithBinding.initViewModelListener() {
    }

    override fun ActivityWithBinding.initView() {
        setTitle("App : WithActivity")
        tvDes.text =
            String.format("username: $username, userId: $userId, isPremium: $isPremium,\nuser:$user")
    }

    override fun ActivityWithBinding.initListener() {
        btnJump1.setOnClickListener {
            ActivityInterceptor.interceptorSwitch = false
            Router.build(RouterPath.CASE_ACTIVITY_FOR_RESULT).withString("name", "From the UserProfileActivity in the app").navigation(this@WithActivity)
        }
        btnJump2.setOnClickListener {
            ActivityInterceptor.interceptorSwitch = true
            Router.build(RouterPath.CASE_ACTIVITY_FOR_RESULT).withString("name", "From the UserProfileActivity in the app").navigation()
        }
    }

    override fun ActivityWithBinding.initData() {
    }
}