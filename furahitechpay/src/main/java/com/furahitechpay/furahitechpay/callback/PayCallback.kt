package com.furahitechpay.furahitechpay.callback

import com.furahitechpay.furahitechpay.model.TokenResponse

interface PayCallback {
    fun onSuccess(tokenResponse: TokenResponse)

    fun onFailre(message: String)
}