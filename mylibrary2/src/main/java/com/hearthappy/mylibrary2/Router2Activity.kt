package com.hearthappy.mylibrary2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hearthappy.mylibrary2.databinding.ActivityRouter2Binding
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.core.Router


@Route("/model2/ui") class Router2Activity : AppCompatActivity() {


    private lateinit var viewBinding: ActivityRouter2Binding

    @Autowired var name = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRouter2Binding.inflate(layoutInflater)
        Router.inject(this)
        setContentView(viewBinding.root)
        viewBinding.apply {
            if (name.isNotEmpty()) tvTitle.text = name
            btnJump.setOnClickListener {
                Router.with(this@Router2Activity).build("/launcher/main").withString("name", "From the Router2Activity in the module").navigation()
            }
        }
    }
}