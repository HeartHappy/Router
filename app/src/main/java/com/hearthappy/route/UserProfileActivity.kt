package com.hearthappy.route

import com.hearthappy.route.base.BaseActivity
import com.hearthappy.route.databinding.ActivityUserProfileBinding
import com.hearthappy.route.interceptor.ActivityInterceptor
import com.hearthappy.route.model.UserBean
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.core.Router

@Route(path = RouterPath.USER_PROFILE_ACTIVITY)
class UserProfileActivity : BaseActivity<ActivityUserProfileBinding>() {
    @Autowired var username: String = ""

    @Autowired(name = "user_id") var userId: Int = 0

    @Autowired var isPremium: Boolean = false

    @Autowired var user: UserBean? = null


    override fun ActivityUserProfileBinding.initViewModelListener() {
    }

    override fun ActivityUserProfileBinding.initView() {
        tvDes.text = String.format("username: $username, userId: $userId, isPremium: $isPremium,\nuser:$user")
    }

    override fun ActivityUserProfileBinding.initListener() {
        btnJump1.setOnClickListener {
            ActivityInterceptor.interceptorSwitch = false
            Router.with(this@UserProfileActivity).build(RouterPath.MODEL_UI).withString("name", "From the UserProfileActivity in the app").navigation()
        }
        btnJump2.setOnClickListener {
            ActivityInterceptor.interceptorSwitch = true
            Router.with(this@UserProfileActivity).build(RouterPath.MODEL_UI).withString("name", "From the UserProfileActivity in the app").navigation()
        }
    }

    override fun ActivityUserProfileBinding.initData() {
    }
}