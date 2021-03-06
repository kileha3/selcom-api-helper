package com.furahitechpay.furahitechpay.util

import android.content.Context
import com.furahitechpay.furahitechpay.model.TokenResponse

interface BaseView {

    /**
     * Run task on main UI thred
     */
    fun runOnUiThread(runnable: Runnable)

    /**
     * Get application context
     */
    fun getBaseContext(): Context

    /**
     * Show error message to the UI
     * @param message: Message to be shown
     */
    fun showError(message: String)

    /**
     * Show or hide progress view
     * @param show Boolean flag to indicate whether progress is shown oe not.
     */
    fun showProgressVisible(show: Boolean)

    fun setButtonProps(btnColor: Int, txtColor: Int)

    fun setButtonEnabled(enabled: Boolean)

    fun showPaymentResponse(response: TokenResponse)

    fun dismissDialog()
}