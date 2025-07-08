package com.hearthappy.mylibrary2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hearthappy.common_api.RouterPath
import com.hearthappy.mylibrary2.databinding.ActivityRouter2Binding
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.launcher.Router


@Route(RouterPath.MODULES2_UI) class Modules2Activity : AppCompatActivity() {


    private lateinit var viewBinding: ActivityRouter2Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRouter2Binding.inflate(layoutInflater)
        Router.inject(this)
        setTitle("Modules2 : Modules2Activity")
        setContentView(viewBinding.root)
        viewBinding.apply {
            //获取uri
            val uri = intent.data
            uri?.let {  tvTitle.text = uri.toString() }
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