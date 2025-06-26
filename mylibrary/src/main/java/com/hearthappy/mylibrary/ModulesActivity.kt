package com.hearthappy.mylibrary

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.hearthappy.common_api.HelloService
import com.hearthappy.common_api.RouterPath
import com.hearthappy.mylibrary.databinding.ActivityRouterBinding
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.launcher.Router
import com.hearthappy.router.launcher.Sorter
import com.hearthappy.router.interfaces.NavigationCallback


@Route(RouterPath.MODULES_UI) class ModulesActivity : AppCompatActivity() {


    private lateinit var viewBinding: ActivityRouterBinding
    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let {
                val resultData = it.getStringExtra("result")
                viewBinding.tvTitle.text = String.format("Launcher 返回结果: ".plus(resultData))
            }
        }
    }
    @Autowired var name: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRouterBinding.inflate(layoutInflater)
        Router.inject(this)
        setContentView(viewBinding.root)
        viewBinding.apply {
            if (name.isNotEmpty()) tvTitle.text = name

            btnUriJump.setOnClickListener { //uri Use %20 to replace spaces.
                //adb shell am start -a android.intent.action.VIEW -d  "hearthappy://kotlin.ksp.com/model2/ui?name=KSP%20Router!\&age=18"
                val uri = Uri.parse("hearthappy://kotlin.ksp.com/model2/ui?name=KSP Router!&age=18")
                Router.build(uri).navigation()
            }

            btnJump.setOnClickListener {
                val optionsCompat = ActivityOptionsCompat.makeClipRevealAnimation(root, root.width / 2, root.height / 2, root.width, root.height)
                Router.build("/model2/ui").withString("name", "KSP Router!").withInt("age", 18).withOptionsCompat(optionsCompat).navigation()
            }

            btnJump3.setOnClickListener {
                val fragment = Router.build("/model/fragment").withString("username", "KSP Router").getInstance() as Fragment
                val beginTransaction = supportFragmentManager.beginTransaction()
                beginTransaction.add(R.id.fragmentLayout, fragment)
                beginTransaction.commit()
            }
            btnJump4.setOnClickListener {
                Router.build("/service/test").navigation()
            }
            btnJump5.setOnClickListener {
                val instance = Router.build("/service/hello").getInstance() as HelloService //                val instance1 = Router.getInstance(HelloService::class.java)
                instance.sayHello("interface service from /service/hello")
                instance.sayHello("interface service from HelloService")
                Router.build("/model2/ui").withString("name", "KSP Router!").navigation()
            }
            btnJump6.setOnClickListener {
                Router.build("/model2/ui").navigation(this@ModulesActivity, 100, object : NavigationCallback {
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
            btnJump7.setOnClickListener {
                activityResultLauncher.launch(Intent(this@ModulesActivity, Router.build("/model2/ui").getDestination()))
            }
        }
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
        private const val TAG = "RouterActivity"
    }
}