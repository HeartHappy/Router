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
import com.hearthappy.mylibrary.databinding.ActivityRouterBinding
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.core.Courier
import com.hearthappy.router.core.Router
import com.hearthappy.router.interfaces.NavigationCallback


@Route("/model/ui") class RouterActivity : AppCompatActivity() {


    private lateinit var viewBinding: ActivityRouterBinding
    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let {
                val resultData = it.getStringExtra("result")
                viewBinding.tvTitle.text = "返回结果: ".plus(resultData)
            }
        }
    };
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
                val instance = Router.build("/service/hello").getInstance() as HelloService
//                val instance1 = Router.getInstance(HelloService::class.java)
                instance.sayHello("interface service from /service/hello")
                instance.sayHello("interface service from HelloService")
                Router.build("/model2/ui").withString("name", "KSP Router!").navigation()
                Router.build("").getDestination()
            }
            btnJump6.setOnClickListener {
                Router.build("/model2/ui").navigation(this@RouterActivity,100,object : NavigationCallback {
                    override fun onFound(courier: Courier) {
                        Log.d(TAG, "onFound: ${courier.getPath()}")
                    }

                    override fun onLost(courier: Courier) {
                        Log.d(TAG, "onLost: ${courier.getPath()}")
                    }

                    override fun onArrival(courier: Courier) {
                        Log.d(TAG, "onArrival: ${courier.getPath()}")
                    }

                    override fun onInterrupt(courier: Courier) {
                        Log.d(TAG, "onInterrupt: ${courier.getPath()}")
                    }
                })
            }
            btnJump7.setOnClickListener { // TODO: launch启动
//                Router.build("/model2/ui").destination
//                Router.build("").getIntent()
//                activityResultLauncher.launch( Intent(this,Router2Activity::class.java))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringExtra("result")
                viewBinding.tvTitle.text = "返回结果: ".plus(result)
            } else {
                viewBinding.tvTitle.text = "操作取消或无返回结果"
            }
        }
    }
    companion object{
        private const val TAG = "RouterActivity"
    }
}