package com.hearthappy.route

import com.hearthappy.route.base.BaseActivity
import com.hearthappy.route.databinding.ActivityUserProfileBinding
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.core.BundleWrapper
import com.hearthappy.router.core.Router

@Route(path = "/user/profile")
class UserProfileActivity : BaseActivity<ActivityUserProfileBinding>() {
    @Autowired var username: String = ""

    @Autowired(name = "user_id") var userId: Int = 0

    @Autowired var isPremium: Boolean = false


    override fun ActivityUserProfileBinding.initViewModelListener() {
    }

    override fun ActivityUserProfileBinding.initView() {
        tvDes.text = String.format("username: $username, userId: $userId, isPremium: $isPremium")
    }

    override fun ActivityUserProfileBinding.initListener() {
        btnJump.setOnClickListener {
            Router.navigate(this@UserProfileActivity, "/model/ui",  BundleWrapper().apply {
                putString("name",  "From the UserProfileActivity in the app")
            })
        }
    }

    override fun ActivityUserProfileBinding.initData() {
    }
}