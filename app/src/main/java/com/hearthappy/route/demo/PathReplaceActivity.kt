package com.hearthappy.route.demo

import android.net.Uri
import com.hearthappy.common_api.RouterPath
import com.hearthappy.route.base.BaseActivity
import com.hearthappy.route.databinding.ActivityPathReplaceBinding
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.launcher.Router

@Route(RouterPath.CASE_PATH_REPLACE)
class PathReplaceActivity : BaseActivity<ActivityPathReplaceBinding>() {
    override fun ActivityPathReplaceBinding.initData() {
    }

    override fun ActivityPathReplaceBinding.initListener() {
        btnJump1.setOnClickListener {
            Router.build(RouterPath.MODULES_UI).navigation()

        }
        btnJump2.setOnClickListener {
            //uri Use %20 to replace spaces.
            //adb shell am start -a android.intent.action.VIEW -d  "hearthappy://kotlin.ksp.com/model2/ui?title=KSP%20Router!\&age=18"
            val uri = Uri.parse("hearthappy://kotlin.ksp.com/model2/ui?name=Uri jump to Modules2Activity &age=18")
            Router.build(uri).navigation()
        }
    }

    override fun ActivityPathReplaceBinding.initView() {
        setTitle("App : PathReplaceActivity")

    }

    override fun ActivityPathReplaceBinding.initViewModelListener() {
    }
}