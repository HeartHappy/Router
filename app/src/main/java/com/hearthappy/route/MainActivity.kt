package com.hearthappy.route

import com.hearthappy.route.base.BaseActivity
import com.hearthappy.route.databinding.ActivityMainBinding
import com.hearthappy.route.model.UserBean
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.core.Router


@Route(path = RouterPath.MAIN_ACTIVITY) class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Autowired var name: String = ""

    override fun ActivityMainBinding.initViewModelListener() {
    }

    override fun ActivityMainBinding.initView() {
        name.takeIf { it.isNotEmpty() }?.let { tvTitle.text = name }
        tvTitle.postDelayed({ tvTitle.text = "哈哈，测试" }, 5000)
    }

    override fun ActivityMainBinding.initListener() {
        btnJump.setOnClickListener { // 带参数跳转
            Router.build(RouterPath.USER_PROFILE_ACTIVITY).withObject("user", UserBean("Labubu", "987654321")).withString("username", "Hello KSP Router!").withInt("user_id", 123456).withBoolean("isPremium", true).navigation()
        }
    }

    override fun ActivityMainBinding.initData() {
    }
}