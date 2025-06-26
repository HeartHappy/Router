package com.hearthappy.mylibrary2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hearthappy.common_api.HelloService
import com.hearthappy.common_api.RouterPath
import com.hearthappy.mylibrary2.databinding.ActivityRouter2Binding
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.launcher.Router
import java.util.Locale


@Route(RouterPath.MODULES2_UI) class Modules2Activity : AppCompatActivity() {


    private lateinit var viewBinding: ActivityRouter2Binding

    @Autowired var title = ""

    @Autowired var age: Int = 0

    @Autowired
    var helloService:HelloService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRouter2Binding.inflate(layoutInflater)
        Router.inject(this)
        setTitle("Modules2 : Modules2Activity")
        setContentView(viewBinding.root)
        viewBinding.apply {
            if (title.isNotEmpty()) tvTitle.text = String.format(Locale.CHINA, "name: %s,age: %d\n", title, age).plus(helloService?.sayHello("KSP Router"))
            btnJump.setOnClickListener {
                Router.build(RouterPath.MAIN_ACTIVITY).navigation()
            }
            btnJump1.setOnClickListener {
                val result: String = tvTitle.text.toString()
                val resultIntent = Intent()
                resultIntent.putExtra("result", result)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}