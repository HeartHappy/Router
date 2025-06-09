package com.hearthappy.route

import com.hearthappy.route.base.BaseActivity
import com.hearthappy.route.databinding.ActivityMainBinding
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.core.BundleWrapper
import com.hearthappy.router.core.Router


@Route(path = "/launcher/main") class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Autowired var name: String = ""

    override fun ActivityMainBinding.initViewModelListener() {
    }

    override fun ActivityMainBinding.initView() {
        name.takeIf { it.isNotEmpty() }?.let { tvTitle.text = name }
    }

    override fun ActivityMainBinding.initListener() {
        btnJump.setOnClickListener { // 带参数跳转
            val params = BundleWrapper().putString("username", "john_doe").putInt("user_id", 12345).putBoolean("isPremium", true)
            Router.navigate(this@MainActivity, "/user/profile", params)
            finish()
        }
    }

    override fun ActivityMainBinding.initData() {
    }
}