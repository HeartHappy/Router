package com.hearthappy.mylibrary2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hearthappy.mylibrary2.databinding.ActivityRouter2Binding
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.core.BundleWrapper
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
            tvTitle.text = name
            btnJump.setOnClickListener {
                Router.navigate(this@Router2Activity,"/launcher/main", BundleWrapper().apply {
                    putString("name","From the Router2Activity in the module")
                })
            }
        }
    }
}