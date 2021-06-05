package com.kwancorp.asyncapp2.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kwancorp.asyncapp2.R
import com.kwancorp.asyncapp2.databinding.MainActBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.main_act)
        binding.activity = this
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        viewModel.itemList.observe(this, Observer {
            binding.recyclerView.scrollToPosition(it.size - 1)
        })

        binding.startButton.setOnClickListener {
            viewModel.getDataFromFlowable_DROP_LATEST()
        }
    }
}