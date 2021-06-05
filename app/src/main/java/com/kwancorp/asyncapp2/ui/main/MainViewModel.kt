package com.kwancorp.asyncapp2.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kwancorp.asyncapp2.data.Repository
import io.reactivex.BackpressureOverflowStrategy
import io.reactivex.schedulers.Schedulers

class MainViewModel: ViewModel() {

    private val _itemList = MutableLiveData<ArrayList<Int>>()
    val itemList: LiveData<ArrayList<Int>> get() = _itemList

    fun getDataFromFlowable_ERROR() {
        _itemList.value?.clear()
        val startTime = System.currentTimeMillis()
        val tempList = ArrayList<Int>()
        Repository.getData_Flowable()
            .onBackpressureBuffer(128, {}, BackpressureOverflowStrategy.ERROR)
            .observeOn(Schedulers.computation())
            .subscribe {
                Thread.sleep(100)
                val endTime = System.currentTimeMillis()
                Log.d("TAG", "[${endTime - startTime}] $it")

                tempList.add(it)
                _itemList.postValue(tempList)
            }
    }

    fun getDataFromFlowable_DROP_OLDEST() {
        _itemList.value?.clear()
        val startTime = System.currentTimeMillis()
        val tempList = ArrayList<Int>()
        Repository.getData_Flowable()
            .onBackpressureBuffer(128, {}, BackpressureOverflowStrategy.DROP_OLDEST)
            .observeOn(Schedulers.computation())
            .subscribe {
                Thread.sleep(100)
                val endTime = System.currentTimeMillis()
                Log.d("TAG", "[${endTime - startTime}] $it")

                tempList.add(it)
                _itemList.postValue(tempList)
            }
    }

    fun getDataFromFlowable_DROP_LATEST() {
        _itemList.value?.clear()
        val startTime = System.currentTimeMillis()
        val tempList = ArrayList<Int>()
        Repository.getData_Flowable()
            .onBackpressureBuffer(128, {}, BackpressureOverflowStrategy.DROP_LATEST)
            .observeOn(Schedulers.computation())
            .subscribe {
                Thread.sleep(100)
                val endTime = System.currentTimeMillis()
                Log.d("TAG", "[${endTime - startTime}] $it")

                tempList.add(it)
                _itemList.postValue(tempList)
            }
    }

    fun getData_Subject() {
        _itemList.value?.clear()
        val startTime = System.currentTimeMillis()
        val tempList = ArrayList<Int>()
        val subject = Repository.getData_Subject()
        subject
            .observeOn(Schedulers.computation())
            .subscribe {
                Thread.sleep(100)
                val endTime = System.currentTimeMillis()
                Log.d("TAG", "[${endTime - startTime}] $it")

                tempList.add(it)
                _itemList.postValue(tempList)
            }
        for(n in 1..50_000_000) {
            subject.onNext(n)
        }
        subject.onComplete()
    }
}