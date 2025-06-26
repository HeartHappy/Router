package com.hearthappy.route.demo

import com.hearthappy.common_api.RouterPath
import com.hearthappy.route.base.BaseActivity
import com.hearthappy.route.databinding.ActivityPathReplaceBinding
import com.hearthappy.router.annotations.Route

@Route(RouterPath.CASE_PATH_REPLACE)
class PathReplaceActivity:BaseActivity<ActivityPathReplaceBinding>() {
    override fun ActivityPathReplaceBinding.initData() {
    }

    override fun ActivityPathReplaceBinding.initListener() {
    }

    override fun ActivityPathReplaceBinding.initView() {
        setTitle("App : PathReplaceActivity")
    }

    override fun ActivityPathReplaceBinding.initViewModelListener() {
    }
}