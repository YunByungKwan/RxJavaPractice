package com.kwancorp.asyncapp2.data

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object Repository {

    fun getData_Flowable(): Flowable<Int> {
        return Flowable.range(1, 50_000_000)
    }

    fun getData_Subject(): PublishSubject<Int> {
        return PublishSubject.create()
    }

    fun getData10_000(): Observable<Int> {
        return Observable.range(1, 10_000)
    }

    fun getData100_000(): Observable<Int> {
        return Observable.range(1, 100_000)
    }

    fun getData1_000_000(): Observable<Int> {
        return Observable.range(1, 1_000_000)
    }

    fun getData10_000_000(): Observable<Int> {
        return Observable.range(1, 10_000_000)
    }

    fun getData10_000_Flowable(): Flowable<Int> {
        return Flowable.range(1, 10_000)
    }

    fun getData100_000_Flowable(): Flowable<Int> {
        return Flowable.range(1, 100_000)
    }

    fun getData1_000_000_Flowable(): Flowable<Int> {
        return Flowable.range(1, 1_000_000)
    }

    fun getData10_000_000_Flowable(): Flowable<Int> {
        return Flowable.range(1, 10_000_000)
    }
}