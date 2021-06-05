package com.kwancorp.asyncapp2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kwancorp.asyncapp2.R
import com.kwancorp.asyncapp2.databinding.ListItemBinding
import java.text.DecimalFormat

class Adapter: RecyclerView.Adapter<Adapter.ViewHolder>() {

    private val itemList = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ListItemBinding>(
            inflater, R.layout.list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    fun updateItems(items: ArrayList<Int>) {
        itemList.clear()
        itemList.addAll(items)
    }

    inner class ViewHolder(
        private val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(num: Int) {
            val df = DecimalFormat("#,###")
            binding.textView.text = df.format(num)
        }
    }
}