package com.hearthappy.mylibrary

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
            btnJump.setOnClickListener {
                Router.build("/model2/ui").withString("name", "KSP Router!").withInt("age",18).navigation()
            }
            btnUriJump.setOnClickListener {
                //uri Use %20 to replace spaces.
                //adb shell am start -a android.intent.action.VIEW -d  "hearthappy://kotlin.ksp.com/model2/ui?name=KSP%20Router!\&age=18"
                val uri = Uri.parse("hearthappy://kotlin.ksp.com/model2/ui?name=KSP Router!&age=18")
                Router.build(uri).navigation()
            }
        }
    }
}