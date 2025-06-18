package com.hearthappy.mylibrary2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hearthappy.mylibrary2.databinding.ActivityRouter2Binding
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.core.Router
import java.util.Locale


@Route("/model2/ui") class Router2Activity : AppCompatActivity() {


    private lateinit var viewBinding: ActivityRouter2Binding

    @Autowired var name = ""

    @Autowired var age: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRouter2Binding.inflate(layoutInflater)
        Router.inject(this)
        setContentView(viewBinding.root)
        val uri = intent.data
        uri?.let { Toast.makeText(this, "uri:$it", Toast.LENGTH_LONG).show() }
        viewBinding.apply {
            if (name.isNotEmpty()) tvTitle.text = String.format(Locale.CHINA, "name: %s,age: %d", name, age)
            btnJump.setOnClickListener {
                Router.build("/launcher/main").withString("name", "From the Router2Activity in the module").navigation()
            }
        }
    }
}