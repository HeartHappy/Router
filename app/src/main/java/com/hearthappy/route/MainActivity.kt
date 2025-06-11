package com.hearthappy.route

import com.hearthappy.route.base.BaseActivity
import com.hearthappy.route.databinding.ActivityMainBinding
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.core.Router


@Route(path = "/launcher/main") class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Autowired var name: String = ""

    override fun ActivityMainBinding.initViewModelListener() {
    }

    override fun ActivityMainBinding.initView() {
        name.takeIf { it.isNotEmpty() }?.let { tvTitle.text = name }
        tvTitle.postDelayed({tvTitle.text="哈哈，测试"},5000)
    }

    override fun ActivityMainBinding.initListener() {
        btnJump.setOnClickListener { // 带参数跳转
            Router.with(this@MainActivity).build("/user/profile").withString("username", "Hello KSP Router!").withInt("user_id",123456).withBoolean("isPremium",true).navigation()
        }
    }

    override fun ActivityMainBinding.initData() {
    }
}