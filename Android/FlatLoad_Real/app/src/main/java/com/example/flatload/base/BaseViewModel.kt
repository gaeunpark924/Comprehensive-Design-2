package com.example.flatload.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel: ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    //Observable을 옵저빙할 때 addDisposable()을 쓴다
    //diposable은 옵저버가 옵저버블을 옵저빙할때 생성되는 객체 즉. RxJava로 비동기 통신할 때 사용한다는 뜻.
    fun addDisposable(disposable: Disposable){
        compositeDisposable.add(disposable)                 //여러개의 diposable 객체를 하나의 객체에서 관리할 수 있게
    }
    //view가 부서질때 viewModel의 onCleared()를 호출
    //옵저버블들이 전부 클리어됨
    override fun onCleared(){
        compositeDisposable.clear()
        super.onCleared()
    }

}