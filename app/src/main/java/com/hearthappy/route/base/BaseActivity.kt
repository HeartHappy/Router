package com.hearthappy.route.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.hearthappy.basic.AbsBaseActivity
import com.hearthappy.router.core.Router

abstract class BaseActivity<VB:ViewBinding>:AbsBaseActivity<VB>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Router.inject(this)
        super.onCreate(savedInstanceState)
    }
}