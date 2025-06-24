package com.hearthappy.mylibrary

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.hearthappy.common_api.HelloService
import com.hearthappy.mylibrary.databinding.ActivityRouterBinding
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.core.Router


@Route("/model/ui") class RouterActivity : AppCompatActivity() {


    private lateinit var viewBinding: ActivityRouterBinding

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
                val instance1 = Router.getInstance(HelloService::class.java)
                instance.sayHello("interface service from /service/hello")
                instance.sayHello("interface service from HelloService")
                Router.build("/model2/ui").withString("name", "KSP Router!").withInt("age", 18).navigation()

            }
        }
    }
}