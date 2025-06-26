package com.hearthappy.mylibrary

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.hearthappy.basic.AbsBaseActivity
import com.hearthappy.common_api.RouterPath
import com.hearthappy.mylibrary.databinding.ActivityForResultBinding
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.interfaces.NavigationCallback
import com.hearthappy.router.launcher.Router
import com.hearthappy.router.launcher.Sorter


@Route(RouterPath.CASE_ACTIVITY_FOR_RESULT)
class ForResultActivity : AbsBaseActivity<ActivityForResultBinding>() {

    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let {
                val resultData = it.getStringExtra("result")
                viewBinding.tvTitle.text = String.format("Launcher 返回结果: ".plus(resultData))
            }
        }
    }

    override fun ActivityForResultBinding.initData() {
    }

    override fun ActivityForResultBinding.initListener() {
        btnJump1.setOnClickListener {
            Router.build(RouterPath.MODULES2_UI).navigation(this@ForResultActivity, 100, object : NavigationCallback {
                override fun onFound(sorter: Sorter) {
                    Log.d(TAG, "onFound: ${sorter.getPath()}")
                }

                override fun onLost(sorter: Sorter) {
                    Log.d(TAG, "onLost: ${sorter.getPath()}")
                }

                override fun onArrival(sorter: Sorter) {
                    Log.d(TAG, "onArrival: ${sorter.getPath()}")
                }

                override fun onInterrupt(sorter: Sorter) {
                    Log.d(TAG, "onInterrupt: ${sorter.getPath()}")
                }
            })
        }
        btnJump2.setOnClickListener {
            activityResultLauncher.launch(Intent(this@ForResultActivity, Router.build(RouterPath.MODULES2_UI).getDestination()))
        }
    }

    override fun ActivityForResultBinding.initView() {
        Router.inject(this@ForResultActivity)
        setTitle("modules1 : ForResultActivity")
    }

    override fun ActivityForResultBinding.initViewModelListener() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringExtra("result")
                viewBinding.tvTitle.text = String.format("onActivityResult 返回结果: ".plus(result))
            } else {
                viewBinding.tvTitle.text = "操作取消或无返回结果"
            }
        }
    }

    companion object {
        private const val TAG = "ForResultActivity"
    }
}