package com.furahitechpay.furahitechpay.util

interface BaseCallback<T> {

    fun onSuccess(success:T)

    fun onFailure(message: String)
}