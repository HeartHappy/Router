package com.hearthappy.mylibrary2

import android.os.Bundle
import com.hearthappy.basic.AbsBaseFragment
import com.hearthappy.mylibrary2.databinding.FragmentRouterBinding
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.launcher.Router

@Route("/model/fragment") class RouterFragment : AbsBaseFragment<FragmentRouterBinding>() {

    @Autowired var username : String = ""

    override fun FragmentRouterBinding.initViewModelListener() {
    }

    override fun FragmentRouterBinding.initView(savedInstanceState : Bundle?) {

        Router.inject(this@RouterFragment)
        if (username.isNotEmpty()) tvTitle.text = tvTitle.text.toString().plus("\nusername:$username")
    }

    override fun FragmentRouterBinding.initListener() {
    }

    override fun FragmentRouterBinding.initData() {
    }
}