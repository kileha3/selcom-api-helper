package com.furahitechpay.furahitechpay

import androidx.fragment.app.FragmentActivity
import com.furahitechpay.furahitechpay.callback.PayCallback
import com.furahitechpay.furahitechpay.mobile.MobileFragment
import com.furahitechpay.furahitechpay.util.BaseFragment
import com.furahitechpay.furahitechpay.model.BillingInfo
import com.furahitechpay.furahitechpay.model.PaymentRequest

class FurahitechPay {

    var isCardPayment: Boolean = false

    var isEnglish: Boolean = false

    var activity: FragmentActivity? = null

    var paymentRequest: PaymentRequest? = null

    var paymentBilling: BillingInfo? = null

    var authToken: String? = null


    fun payNow(callback: PayCallback){

        if(paymentRequest == null){
            error("Make sure payment request is created before sending an actual request")
        }

        if(isCardPayment && paymentBilling == null){
            error("Make sure billing information are filled out before proceeding")
        }

        val baseFragment = if(isCardPayment) BaseFragment() else MobileFragment.getInstance(callback)
        baseFragment.show(activity!!.supportFragmentManager.beginTransaction(), baseFragment.toString())
    }


    companion object{
        val instance = FurahitechPay()
    }
}