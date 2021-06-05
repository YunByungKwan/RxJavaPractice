package com.kwancorp.asyncapp2.ui.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

object MyBindingAdapter {
    @JvmStatic
    @BindingAdapter("listData")
    fun setItemList(recyclerView: RecyclerView, items: ArrayList<Int>?) {
        if(recyclerView.adapter == null) {
            recyclerView.adapter = Adapter()
        }
        val adapter = recyclerView.adapter as Adapter
        items?.let {
            adapter.updateItems(it)
            adapter.notifyDataSetChanged()
        }
    }
}