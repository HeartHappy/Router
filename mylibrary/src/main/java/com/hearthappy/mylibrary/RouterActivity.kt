package com.hearthappy.mylibrary

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
            if(name.isNotEmpty())  tvTitle.text = name
            btnJump.setOnClickListener {
                Router.build("/model2/ui").withString("name", "From the RouterActivity in the module").navigation()

            }
        }
    }
}