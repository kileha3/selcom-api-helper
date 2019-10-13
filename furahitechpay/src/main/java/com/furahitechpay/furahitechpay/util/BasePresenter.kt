package com.furahitechpay.furahitechpay.util

abstract class BasePresenter<V : BaseView>(internal var view: V) {

    open fun onCreate(){}

    open fun onDestroy(){}

}