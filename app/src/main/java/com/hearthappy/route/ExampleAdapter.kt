package com.hearthappy.route

import com.hearthappy.basic.AbsSpecialAdapter
import com.hearthappy.route.databinding.ItemExampleBinding
import com.hearthappy.route.model.ExampleBean

class ExampleAdapter :AbsSpecialAdapter<ItemExampleBinding, ExampleBean>() {
    override fun ItemExampleBinding.bindViewHolder(data: ExampleBean, position: Int) {
        tvExampleTitle.text=position.plus(1).toString().plus("、${data.title}")
    }
}